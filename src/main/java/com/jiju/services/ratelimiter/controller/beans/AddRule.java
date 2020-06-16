package com.jiju.services.ratelimiter.controller.beans;

import com.jiju.services.ratelimiter.beans.DurationUnit;
import com.jiju.services.ratelimiter.beans.Strategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddRule {
    private int requestMaxLimit;
    private DurationUnit durationUnit;
    private String pathPattern;
    private Strategy strategy;
    private boolean differentiateUsers;
    private boolean differentiateTenants;
}
