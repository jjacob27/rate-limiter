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

    @Builder.Default
    private int numTokensPerRequest = 1;
    private String requestMatchPattern;
}
