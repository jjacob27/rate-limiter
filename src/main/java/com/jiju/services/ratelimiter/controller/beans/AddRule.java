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
    int requestMaxLimit;
    DurationUnit durationUnit;
    String pathPattern;
    int numTokensPerRequest;
    Strategy strategy;
}
