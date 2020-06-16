package com.jiju.services.ratelimiter.strategy.impl;

import com.jiju.services.ratelimiter.beans.LimitDuration;
import com.jiju.services.ratelimiter.beans.Rule;
import com.jiju.services.ratelimiter.beans.Strategy;
import com.jiju.services.ratelimiter.beans.TokenBucket;
import com.jiju.services.ratelimiter.interfaces.*;
import com.jiju.services.ratelimiter.interfaces.tokenbucket.TokenBucketIdGenerator;
import com.jiju.services.ratelimiter.interfaces.tokenbucket.TokenBucketStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@SuppressWarnings("unused")
public class  TokenBucketBasedRateLimiter implements RateLimiter {

    @Autowired
    RulesStore rulesStore;

    @Autowired
    TokenBucketStore tokenBucketStore;

    @Autowired
    TokenBucketIdGenerator tokenBucketIdGenerator;


    @Override
    public boolean allowRequest(String requestUrl)  {
        Rule throttlingRule = rulesStore.getRuleForRequestPattern(requestUrl);
        if(throttlingRule!=null){
            String tokenBucketId = tokenBucketIdGenerator
                    .generateTokenBucketId(throttlingRule.getRequestMatchPattern());
            TokenBucket tb =tokenBucketStore.getTokenBucket(tokenBucketId);
            if(tb==null){
                tb = tokenBucketStore.createTokenBucket(tokenBucketId, throttlingRule);
            }
            return allocateToken(tb, throttlingRule);
        }
        return true;
    }

    boolean allocateToken(TokenBucket tb, Rule throttlingRule){
        LimitDuration limitDuration = throttlingRule.getLimit();
        if(limitDuration.getLimit() <= 0)
        {
            // means do not allow requests
            return false;
        }

        LocalDateTime lastUpdatedTime =tb.getLastUpdatedTime();
        LocalDateTime now = LocalDateTime.now();

        if(determineIfThisRequestIsInSameWindowAsLastRequest(limitDuration, lastUpdatedTime, now)){
            int tokensAvailable = tb.getTokensAvailable();
            if(tokensAvailable>=throttlingRule.getNumTokensPerRequest())
            {
                allocateTokenForRequest(tb, throttlingRule, now, tokensAvailable);
                return true;
            }
            else {
                return false;
            }
        }
        else {
            int tokensAvailable = limitDuration.getLimit();
            allocateTokenForRequest(tb, throttlingRule, now, tokensAvailable);
            return true;
        }
    }

    private void allocateTokenForRequest(TokenBucket tb, Rule throttlingRule, LocalDateTime now, int tokensAvailable) {
        tokensAvailable -= throttlingRule.getNumTokensPerRequest();
        tb.setLastUpdatedTime(now);
        tb.setTokensAvailable(tokensAvailable);
        tokenBucketStore.setTokenBucket(tb.getTokenIdentifier(), tb);
    }

    private boolean determineIfThisRequestIsInSameWindowAsLastRequest(LimitDuration limitDuration, LocalDateTime lastUpdatedTime, LocalDateTime now) {
        boolean requestInSameWindowAsLastRequest = false;
        switch (limitDuration.getTimePeriod()){
            case MINUTE:
                if(lastUpdatedTime.getYear()==now.getYear() &&
                        lastUpdatedTime.getDayOfYear() == now.getDayOfYear() &&
                            lastUpdatedTime.getHour() == now.getHour() &&
                                lastUpdatedTime.getMinute() == now.getMinute())
                    requestInSameWindowAsLastRequest =true;
                break;

            case HOUR:
                if(lastUpdatedTime.getYear()==now.getYear() &&
                        lastUpdatedTime.getDayOfYear() == now.getDayOfYear() &&
                        lastUpdatedTime.getHour() == now.getHour())
                    requestInSameWindowAsLastRequest = true;
                break;

            case DAY:
                if(lastUpdatedTime.getYear()==now.getYear() &&
                        lastUpdatedTime.getDayOfYear() == now.getDayOfYear())
                    requestInSameWindowAsLastRequest = true;
                break;

            case MONTH:
                if(lastUpdatedTime.getYear()==now.getYear() &&
                        lastUpdatedTime.getMonth() == now.getMonth())
                    requestInSameWindowAsLastRequest = true;
                break;

            case YEAR:
                if(lastUpdatedTime.getYear()==now.getYear())
                    requestInSameWindowAsLastRequest = true;
                break;
        }
        return requestInSameWindowAsLastRequest;
    }

    @Override
    public Strategy getStrategy() {
        return Strategy.TOKEN_BUCKET;
    }
}
