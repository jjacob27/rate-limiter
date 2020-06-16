package com.jiju.services.ratelimiter.interfaces.tokenbucket;

import com.jiju.services.ratelimiter.beans.Rule;
import com.jiju.services.ratelimiter.beans.TokenBucket;

public interface TokenBucketStore {
    TokenBucket createTokenBucket(String identifier, Rule throttlingRule);
    TokenBucket getTokenBucket(String identifier);
    void setTokenBucket(String identifier, TokenBucket bucket);
}
