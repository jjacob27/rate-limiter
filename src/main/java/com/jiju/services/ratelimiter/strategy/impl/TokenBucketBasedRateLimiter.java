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

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class  TokenBucketBasedRateLimiter implements RateLimiter {

    @Autowired
    RulesStore rulesStore;

    @Autowired
    TokenBucketStore tokenBucketStore;

    @Autowired
    TokenBucketIdGenerator tokenBucketIdGenerator;


    @Override
    public boolean allowRequest(String requestUrl)  {
        boolean allowRequest = true;

        Rule throttlingRule = rulesStore.getRuleForRequestPattern(requestUrl);
        if(throttlingRule!=null){
            String tokenBucketId = tokenBucketIdGenerator
                    .generateTokenBucketId(throttlingRule.getRequestMatchPattern());
            TokenBucket tb =tokenBucketStore.getTokenBucket(tokenBucketId);
            if(tb==null){
                tb = tokenBucketStore.createTokenBucket(tokenBucketId, throttlingRule);
            }
            return allocateTokens(tb, throttlingRule);
        }
        return allowRequest;
    }

    boolean allocateTokens(TokenBucket tb, Rule throttlingRule){
        LimitDuration limitDuration = throttlingRule.getLimit();
        if(limitDuration.getLimit() <= 0)
        {
            // means do not allow requests
            return false;
        }

        LocalDateTime lastUpdatedTime =tb.getLastUpdatedTime();
        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between(lastUpdatedTime, now);
        long diff = 0;
        switch(limitDuration.getTimePeriod()){
            case MINUTE:
                diff = duration.toMinutes();
                break;
            case HOUR:
                diff = duration.toHours();
                break;
            case DAY:
                diff = duration.toDays();
                break;
        }
        Integer tokensAvailableCurrently = tb.getTokensAvailable()+
                Long.valueOf((diff*limitDuration.getLimit())).intValue();
        System.out.println("Currently available tokens ="+tokensAvailableCurrently);

        int numTokenRequiredForThisRequest = throttlingRule.getNumTokensPerRequest();
        if(tokensAvailableCurrently >= numTokenRequiredForThisRequest)
        {
            tb.setTokensAvailable(tokensAvailableCurrently-numTokenRequiredForThisRequest);
            tb.setLastUpdatedTime(now);
            tokenBucketStore.setTokenBucket(tb.getTokenIdentifier(),tb);
            return true;
        }
        else
            return false;
    }

    @Override
    public Strategy getStrategy() {
        return Strategy.TOKEN_BUCKET;
    }
}
