package com.jiju.services.ratelimiter.strategy.impl;

import com.jiju.services.ratelimiter.beans.LimitDuration;
import com.jiju.services.ratelimiter.beans.Rule;
import com.jiju.services.ratelimiter.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public abstract class AbstractRateLimiter implements RateLimiter {
    @Autowired
    IdGenerator<String> idGenerator;

    @Autowired
    RulesStore rulesStore;

    @Autowired(required = false)
    TenantDifferentiator tenantDifferentiator;

    @Autowired(required = false)
    UserDifferentiator userDifferentiator;

    protected String getIdentifier(Rule rule) {
        String input = rule.getRequestMatchPattern();
        if (rule.isDifferentiateTenants() && tenantDifferentiator != null)
            input = input + "##" + tenantDifferentiator.getTenantId();

        if (rule.isDifferentiateUsers() && userDifferentiator != null)
            input = input + "##" + userDifferentiator.getUserId();

        return idGenerator
                .generateId(input);
    }

    protected boolean determineIfThisRequestIsInSameWindowAsLastRequest(LimitDuration limitDuration, LocalDateTime lastUpdatedTime, LocalDateTime now) {
        boolean requestInSameWindowAsLastRequest = false;
        switch (limitDuration.getTimePeriod()) {
            case MINUTE:
                if (lastUpdatedTime.getYear() == now.getYear() &&
                        lastUpdatedTime.getDayOfYear() == now.getDayOfYear() &&
                        lastUpdatedTime.getHour() == now.getHour() &&
                        lastUpdatedTime.getMinute() == now.getMinute())
                    requestInSameWindowAsLastRequest = true;
                break;

            case HOUR:
                if (lastUpdatedTime.getYear() == now.getYear() &&
                        lastUpdatedTime.getDayOfYear() == now.getDayOfYear() &&
                        lastUpdatedTime.getHour() == now.getHour())
                    requestInSameWindowAsLastRequest = true;
                break;

            case DAY:
                if (lastUpdatedTime.getYear() == now.getYear() &&
                        lastUpdatedTime.getDayOfYear() == now.getDayOfYear())
                    requestInSameWindowAsLastRequest = true;
                break;

            case MONTH:
                if (lastUpdatedTime.getYear() == now.getYear() &&
                        lastUpdatedTime.getMonth() == now.getMonth())
                    requestInSameWindowAsLastRequest = true;
                break;

            case YEAR:
                if (lastUpdatedTime.getYear() == now.getYear())
                    requestInSameWindowAsLastRequest = true;
                break;
        }
        return requestInSameWindowAsLastRequest;
    }
}
