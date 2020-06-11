package com.jiju.services.ratelimiter.interfaces.tokenbucket;

import com.jiju.services.ratelimiter.beans.Rule;
import com.jiju.services.ratelimiter.beans.TokenBucket;

public interface TokenBucketStore {
    TokenBucket createTokenBucket(String tokenBucketIdentifier, Rule throttlingRule);
    TokenBucket getTokenBucket(String tokenBucketIdentifier);
    void setTokenBucket(String tokenBucketIdentifier, TokenBucket bucket);
}
