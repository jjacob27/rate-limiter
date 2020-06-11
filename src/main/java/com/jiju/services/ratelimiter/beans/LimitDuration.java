package com.jiju.services.ratelimiter.beans;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LimitDuration {
    private Integer limit;
    private DurationUnit timePeriod;
}
