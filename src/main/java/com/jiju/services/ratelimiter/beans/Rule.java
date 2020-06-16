package com.jiju.services.ratelimiter.beans;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Rule {
    private Strategy strategy;
    private LimitDuration limit;

    private String requestMatchPattern;

    @Builder.Default
    private boolean differentiateUsers = false;

    @Builder.Default
    private boolean differentiateTenants = false;
}
