package com.jiju.services.ratelimiter.interfaces.slidingwindow;

import com.jiju.services.ratelimiter.beans.Rule;
import com.jiju.services.ratelimiter.beans.SlidingWindow;

public interface SlidingWindowStore {
    SlidingWindow createSlidingWindow(String identifier, Rule throttlingRule);
    SlidingWindow getSlidingWindow(String identifier);
    void setSlidingWindow(String identifier, SlidingWindow bucket);
}
