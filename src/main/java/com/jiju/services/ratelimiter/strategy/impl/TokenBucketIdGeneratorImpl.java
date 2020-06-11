package com.jiju.services.ratelimiter.strategy.impl;

import com.jiju.services.ratelimiter.interfaces.tokenbucket.TokenBucketIdGenerator;
import org.springframework.stereotype.Component;

@Component
public class TokenBucketIdGeneratorImpl implements TokenBucketIdGenerator {
    @Override
    public String generateTokenBucketId(String rulePattern) {
        return ""+rulePattern.hashCode();
    }
}
