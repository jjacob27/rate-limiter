package com.jiju.services.ratelimiter.interfaces;

import com.jiju.services.ratelimiter.beans.Rule;

import java.util.List;

public interface RulesStore {
    Rule getRuleForRequestPattern(String requestPattern);
    void addRule(Rule r);
    void removeRule(Rule r);
    List<Rule> getAllRules();
}
