package com.jiju.services.ratelimiter.controller;

import com.jiju.services.ratelimiter.annotation.RateLimit;
import com.jiju.services.ratelimiter.beans.DurationUnit;
import com.jiju.services.ratelimiter.beans.Strategy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestApiController {
    // TODO: Move this class to the tests folder.

    @RateLimit(limit=2, duration = DurationUnit.MINUTE)
    @GetMapping("/t1")
    public ResponseEntity m1(){
        return new ResponseEntity("Success", HttpStatus.OK);
    }

    @RateLimit(limit=2, duration = DurationUnit.MINUTE, strategy = Strategy.SLIDING_WINDOW_COUNTER)
    @GetMapping("/t2")
    public ResponseEntity m2(){
        return new ResponseEntity("Success", HttpStatus.OK);
    }
}
