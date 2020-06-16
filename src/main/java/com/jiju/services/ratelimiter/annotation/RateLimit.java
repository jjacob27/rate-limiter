package com.jiju.services.ratelimiter.annotation;

import com.jiju.services.ratelimiter.beans.DurationUnit;
import com.jiju.services.ratelimiter.beans.Strategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {
    Strategy strategy() default Strategy.TOKEN_BUCKET;
    int limit() default 5;
    DurationUnit duration() default DurationUnit.MINUTE;
    int numTokensPerRequest() default 1;
    boolean differentiateByUser() default false;
    boolean differentiateByTenant() default false;
}
