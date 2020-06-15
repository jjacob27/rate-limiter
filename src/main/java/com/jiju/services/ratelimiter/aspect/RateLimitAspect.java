package com.jiju.services.ratelimiter.aspect;

import com.jiju.services.ratelimiter.annotation.RateLimit;
import com.jiju.services.ratelimiter.beans.LimitDuration;
import com.jiju.services.ratelimiter.beans.Rule;
import com.jiju.services.ratelimiter.beans.Strategy;
import com.jiju.services.ratelimiter.interfaces.RateLimiter;
import com.jiju.services.ratelimiter.interfaces.RulesStore;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    RulesStore rulesStore;

    @Autowired
    RateLimiter rateLimiter;

    @Pointcut("execution(public * *(..))")
    public void anyPublicOperation() {}

    @Around(value="@annotation(rateLimit)")
    public Object limitRequests(ProceedingJoinPoint jp, RateLimit rateLimit) throws Throwable {
        Object returnObject=null;

        LimitDuration ld = LimitDuration.builder()
                .limit(rateLimit.limit())
                .timePeriod(rateLimit.duration())
                .build();

        String methodName="method://"+jp.getSignature().toLongString();
        Rule addRule = Rule.builder().requestMatchPattern(methodName)
                .numTokensPerRequest(rateLimit.numTokensPerRequest())
                .strategy(rateLimit.strategy())
                .limit(ld).build();

        Rule existingRule=rulesStore.getRuleForRequestPattern(methodName);

        if(existingRule==null) {
            rulesStore.addRule(addRule);
        }

        if(rateLimiter.allowRequest(methodName)) {
            returnObject = jp.proceed();
        }
        else {
            returnObject = new ResponseEntity<>("Too many Requests!", HttpStatus.TOO_MANY_REQUESTS);
        }
        return returnObject;
    }
}
