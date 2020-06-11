package com.jiju.services.ratelimiter.beans;

public enum Strategy {
    TOKEN_BUCKET,
    LEAKY_BUCKET,
    SLIDING_WINDOW_COUNTER
}
