package com.jiju.services.ratelimiter.controller;

import com.jiju.services.ratelimiter.beans.LimitDuration;
import com.jiju.services.ratelimiter.beans.Rule;
import com.jiju.services.ratelimiter.beans.Strategy;
import com.jiju.services.ratelimiter.controller.beans.AddRule;
import com.jiju.services.ratelimiter.controller.beans.DeleteRule;
import com.jiju.services.ratelimiter.interfaces.RulesStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/rule")
@SuppressWarnings("unused")
public class RulesController {

    @Autowired
    RulesStore rulesStore;

    @PostMapping(path="/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addRule(@RequestBody AddRule ar){
        try {
            LimitDuration ld = LimitDuration.builder()
                    .limit(ar.getRequestMaxLimit())
                    .timePeriod(ar.getDurationUnit())
                    .build();
            Rule r = Rule.builder().requestMatchPattern(ar.getPathPattern())
                    .numTokensPerRequest(ar.getNumTokensPerRequest())
                    .strategy(Strategy.TOKEN_BUCKET)
                    .limit(ld).build();
            rulesStore.addRule(r);
        } catch (Exception e){
            return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping(path="/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteRule(@RequestBody DeleteRule dr){
        Rule r = rulesStore.getRuleForRequestPattern(dr.getRequestPattern());
        if(r==null)
            return new ResponseEntity("Rule not found",HttpStatus.NOT_FOUND);
        try {
            rulesStore.removeRule(r);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity getAllRules(){
        return new ResponseEntity(rulesStore.getAllRules(),HttpStatus.OK);
    }
}
