package com.jiju.services.ratelimiter.strategy.impl;

import com.jiju.services.ratelimiter.beans.*;
import com.jiju.services.ratelimiter.interfaces.slidingwindow.SlidingWindowStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Service
@SuppressWarnings("unused")
public class SlidingWindowCounterBasedRateLimiter extends AbstractRateLimiter {

    @Autowired
    private SlidingWindowStore swStore;

    @Override
    public boolean allowRequest(String requestUrl) {
        Rule throttlingRule = rulesStore.getRuleForRequestPattern(requestUrl);
        if(throttlingRule!=null){
            String slidingWindowId = getIdentifier(throttlingRule);
            SlidingWindow sw = swStore.getSlidingWindow(slidingWindowId);
            if(sw == null){
                sw=swStore.createSlidingWindow(slidingWindowId,throttlingRule);
            }
            return allocateToken(sw,throttlingRule);
        }
        return true;
    }

    private boolean allocateToken(SlidingWindow sw, Rule throttlingRule) {
        LimitDuration limitDuration = throttlingRule.getLimit();
        if(limitDuration.getLimit() <= 0)
        {
            // means do not allow requests
            return false;
        }

        LocalDateTime lastUpdatedTime =sw.getLastUpdatedTime();
        LocalDateTime now = LocalDateTime.now();

        boolean isRequestInSameWindow = isRequestInSameWindowAsLastOne(limitDuration,lastUpdatedTime,now);

        if(isRequestInSameWindow){
            long requestsAlreadyServiced = sw.getRequests().stream()
                    .map(SlidingWindowItem::getCounter)
                    .reduce(0L,(a,b)->a+b);
            if(requestsAlreadyServiced+1 <= limitDuration.getLimit()){
                return addItemToSlidingWindow(sw, now);
            } else {
                return false;
            }
        } else {
            sw.purgeAllRequests();
            return addItemToSlidingWindow(sw, now);
        }
    }

    private boolean addItemToSlidingWindow(SlidingWindow sw, LocalDateTime now) {
        sw.getRequests().add(SlidingWindowItem.builder()
                .epochTime(now.toEpochSecond(OffsetDateTime.now().getOffset()))
                .counter(1)
                .build());
        swStore.setSlidingWindow(sw.getTokenIdentifier(), sw);
        return true;
    }

    @Override
    public Strategy getStrategy() {
        return Strategy.SLIDING_WINDOW_COUNTER;
    }
}
