package com.jiju.services.ratelimiter.beans;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenBucket {
    private String tokenIdentifier;
    private LocalDateTime lastUpdatedTime;
    private Integer tokensAvailable;
}
