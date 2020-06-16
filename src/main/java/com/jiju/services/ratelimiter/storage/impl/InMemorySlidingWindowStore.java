package com.jiju.services.ratelimiter.storage.impl;

import com.jiju.services.ratelimiter.beans.Rule;
import com.jiju.services.ratelimiter.beans.SlidingWindow;
import com.jiju.services.ratelimiter.interfaces.slidingwindow.SlidingWindowStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@SuppressWarnings("unused")
public class InMemorySlidingWindowStore implements SlidingWindowStore {
    private Map<String, SlidingWindow> store = new ConcurrentHashMap<>();

    @Override
    public SlidingWindow createSlidingWindow(String identifier, Rule throttlingRule) {
        SlidingWindow sw=SlidingWindow.builder().lastUpdatedTime(LocalDateTime.now())
                .tokenIdentifier(identifier)
                .build();
        store.put(identifier,sw);
        return sw;
    }

    @Override
    public SlidingWindow getSlidingWindow(String identifier) {
        return store.get(identifier);
    }

    @Override
    public void setSlidingWindow(String identifier, SlidingWindow sw) {
        store.put(identifier,sw);
    }
}
