package com.jiju.services.ratelimiter.interfaces;

import com.jiju.services.ratelimiter.beans.Strategy;

public interface RateLimiter {
    boolean allowRequest(String requestUrl);
    Strategy getStrategy();
}
