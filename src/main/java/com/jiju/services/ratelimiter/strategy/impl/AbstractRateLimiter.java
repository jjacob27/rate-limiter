package com.jiju.services.ratelimiter.strategy.impl;

import com.jiju.services.ratelimiter.beans.LimitDuration;
import com.jiju.services.ratelimiter.beans.Rule;
import com.jiju.services.ratelimiter.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public abstract class AbstractRateLimiter implements RateLimiter {
    @Autowired
    private IdGenerator<String> idGenerator;

    @Autowired
    protected RulesStore rulesStore;

    @Autowired(required = false)
    private TenantDifferentiator tenantDifferentiator;

    @Autowired(required = false)
    private UserDifferentiator userDifferentiator;

    String getIdentifier(Rule rule) {
        String input = rule.getRequestMatchPattern();
        if (rule.isDifferentiateTenants() && tenantDifferentiator != null)
            input = input + "##" + tenantDifferentiator.getTenantId();

        if (rule.isDifferentiateUsers() && userDifferentiator != null)
            input = input + "##" + userDifferentiator.getUserId();

        return idGenerator
                .generateId(input);
    }

    boolean isRequestInSameWindowAsLastOne(LimitDuration limitDuration, LocalDateTime lastUpdatedTime, LocalDateTime now) {
        switch (limitDuration.getTimePeriod()) {
            case MINUTE:
                return (lastUpdatedTime.getYear() == now.getYear() &&
                        lastUpdatedTime.getDayOfYear() == now.getDayOfYear() &&
                        lastUpdatedTime.getHour() == now.getHour() &&
                        lastUpdatedTime.getMinute() == now.getMinute());

            case HOUR:
                return (lastUpdatedTime.getYear() == now.getYear() &&
                        lastUpdatedTime.getDayOfYear() == now.getDayOfYear() &&
                        lastUpdatedTime.getHour() == now.getHour());

            case DAY:
                return (lastUpdatedTime.getYear() == now.getYear() &&
                        lastUpdatedTime.getDayOfYear() == now.getDayOfYear());

            case MONTH:
                return (lastUpdatedTime.getYear() == now.getYear() &&
                        lastUpdatedTime.getMonth() == now.getMonth());

            case YEAR:
                return (lastUpdatedTime.getYear() == now.getYear());
        }
        return false;
    }
}
