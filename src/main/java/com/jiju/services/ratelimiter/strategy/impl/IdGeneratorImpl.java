package com.jiju.services.ratelimiter.strategy.impl;

import com.jiju.services.ratelimiter.interfaces.IdGenerator;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class IdGeneratorImpl<String> implements IdGenerator<String> {
    @Override
    public java.lang.String generateId(String rulePattern) {
        return ""+ rulePattern.hashCode();
    }
}
