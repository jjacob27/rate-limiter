package com.jiju.services.ratelimiter.strategy.impl;

import com.jiju.services.ratelimiter.beans.LimitDuration;
import com.jiju.services.ratelimiter.beans.Rule;
import com.jiju.services.ratelimiter.beans.Strategy;
import com.jiju.services.ratelimiter.beans.TokenBucket;
import com.jiju.services.ratelimiter.interfaces.tokenbucket.TokenBucketStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@SuppressWarnings("unused")
public class  TokenBucketBasedRateLimiter extends AbstractRateLimiter {

    @Autowired
    private TokenBucketStore tokenBucketStore;

    @Override
    public boolean allowRequest(String requestUrl)  {
        Rule throttlingRule = rulesStore.getRuleForRequestPattern(requestUrl);
        if(throttlingRule!=null){
            String tokenBucketId = getIdentifier(throttlingRule);
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

        if(isRequestInSameWindowAsLastOne(limitDuration, lastUpdatedTime, now)){
            int tokensAvailable = tb.getTokensAvailable();
            if(tokensAvailable>0)
            {
                allocateTokenForRequest(tb, now, tokensAvailable);
                return true;
            }
            else {
                return false;
            }
        }
        else {
            int tokensAvailable = limitDuration.getLimit();
            allocateTokenForRequest(tb, now, tokensAvailable);
            return true;
        }
    }

    private void allocateTokenForRequest(TokenBucket tb, LocalDateTime now, int tokensAvailable) {
        tokensAvailable -= 1;
        tb.setLastUpdatedTime(now);
        tb.setTokensAvailable(tokensAvailable);
        tokenBucketStore.setTokenBucket(tb.getTokenIdentifier(), tb);
    }

    @Override
    public Strategy getStrategy() {
        return Strategy.TOKEN_BUCKET;
    }
}
