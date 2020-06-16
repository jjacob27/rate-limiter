package com.jiju.services.ratelimiter.aspect;

import com.jiju.services.ratelimiter.annotation.RateLimit;
import com.jiju.services.ratelimiter.beans.LimitDuration;
import com.jiju.services.ratelimiter.beans.Rule;
import com.jiju.services.ratelimiter.interfaces.RateLimiter;
import com.jiju.services.ratelimiter.interfaces.RulesStore;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    RulesStore rulesStore;

    @Autowired
    List<RateLimiter> rateLimiters;

    @Around(value="@annotation(rateLimit)")
    public Object limitRequests(ProceedingJoinPoint jp, RateLimit rateLimit) throws Throwable {
        Object returnObject;

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

        Optional<RateLimiter> rateLimiterOptional = rateLimiters
                .stream()
                .filter(a->a.getStrategy()==rateLimit.strategy()).findFirst();

        if(rateLimiterOptional.isEmpty()) {
           returnObject = jp.proceed();
        }
        else if (rateLimiterOptional.get().allowRequest(methodName)) {
            returnObject = jp.proceed();
        }
        else {
            returnObject = new ResponseEntity<>("Too many Requests!", HttpStatus.TOO_MANY_REQUESTS);
        }
        return returnObject;
    }
}
