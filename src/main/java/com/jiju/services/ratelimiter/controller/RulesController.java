package com.jiju.services.ratelimiter.controller;

import com.jiju.services.ratelimiter.beans.LimitDuration;
import com.jiju.services.ratelimiter.beans.Rule;
import com.jiju.services.ratelimiter.beans.Strategy;
import com.jiju.services.ratelimiter.controller.beans.AddRule;
import com.jiju.services.ratelimiter.interfaces.RulesStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/rule")
public class RulesController {

    @Autowired
    RulesStore rulesStore;

    @PostMapping(path="/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addRule(@RequestBody AddRule ar){
        LimitDuration ld = LimitDuration.builder()
                    .limit(ar.getRequestMaxLimit())
                    .timePeriod(ar.getDurationUnit())
                    .build();
        Rule r = Rule.builder().requestMatchPattern(ar.getPathPattern())
                    .numTokensPerRequest(ar.getNumTokensPerRequest())
                    .strategy(Strategy.TOKEN_BUCKET)
                    .limit(ld).build();
        rulesStore.addRule(r);
    }

    @GetMapping("/getAll")
    public List<Rule> getAllRules()
}
