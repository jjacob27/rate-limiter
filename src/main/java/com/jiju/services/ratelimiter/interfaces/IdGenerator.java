package com.jiju.services.ratelimiter.interfaces;

@FunctionalInterface
public interface IdGenerator<T> {
    String generateId(T rulePattern);
}
