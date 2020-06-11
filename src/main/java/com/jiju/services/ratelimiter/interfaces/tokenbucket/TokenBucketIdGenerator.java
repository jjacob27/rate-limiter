package com.jiju.services.ratelimiter.interfaces.tokenbucket;

public interface TokenBucketIdGenerator {
    String generateTokenBucketId(String bucketRulePattern);
}
