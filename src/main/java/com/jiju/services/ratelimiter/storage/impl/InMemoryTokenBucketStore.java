package com.jiju.services.ratelimiter.storage.impl;

import com.jiju.services.ratelimiter.beans.Rule;
import com.jiju.services.ratelimiter.beans.TokenBucket;
import com.jiju.services.ratelimiter.interfaces.tokenbucket.TokenBucketStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@SuppressWarnings("unused")
public class InMemoryTokenBucketStore implements TokenBucketStore {
    private Map<String, TokenBucket> tokenBucketStore = new ConcurrentHashMap<>();

    @Override
    public TokenBucket createTokenBucket(String tokenBucketIdentifier, Rule r) {
        TokenBucket tb=TokenBucket.builder().lastUpdatedTime(LocalDateTime.now())
                .tokenIdentifier(tokenBucketIdentifier)
                .tokensAvailable(r.getNumTokensPerRequest()*r.getLimit().getLimit())
                .build();
        tokenBucketStore.put(tokenBucketIdentifier,tb);
        return tb;
    }

    @Override
    public TokenBucket getTokenBucket(String tokenBucketIdentifier) {
        return tokenBucketStore.get(tokenBucketIdentifier);
    }

    @Override
    public void setTokenBucket(String tokenBucketIdentifier, TokenBucket bucket) {
        tokenBucketStore.put(tokenBucketIdentifier,bucket);
    }
}
