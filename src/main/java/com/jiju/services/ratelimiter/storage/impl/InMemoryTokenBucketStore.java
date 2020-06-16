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
    public TokenBucket createTokenBucket(String identifier, Rule r) {
        TokenBucket tb=TokenBucket.builder().lastUpdatedTime(LocalDateTime.now())
                .tokenIdentifier(identifier)
                .tokensAvailable(r.getLimit().getLimit())
                .build();
        tokenBucketStore.put(identifier,tb);
        return tb;
    }

    @Override
    public TokenBucket getTokenBucket(String identifier) {
        return tokenBucketStore.get(identifier);
    }

    @Override
    public void setTokenBucket(String identifier, TokenBucket bucket) {
        tokenBucketStore.put(identifier,bucket);
    }
}
