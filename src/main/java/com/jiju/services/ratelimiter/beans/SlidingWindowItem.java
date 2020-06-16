package com.jiju.services.ratelimiter.beans;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlidingWindowItem {
    private long epochTime;
    private long counter;
}
