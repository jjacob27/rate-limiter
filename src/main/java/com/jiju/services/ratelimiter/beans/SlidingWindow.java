package com.jiju.services.ratelimiter.beans;

import lombok.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlidingWindow {
    private String tokenIdentifier;
    private LocalDateTime lastUpdatedTime;

    @Builder.Default
    private Queue<SlidingWindowItem> requests = new LinkedList<>();

    public void purgeAllRequests(){
        requests = new LinkedList<>();
    }
}
