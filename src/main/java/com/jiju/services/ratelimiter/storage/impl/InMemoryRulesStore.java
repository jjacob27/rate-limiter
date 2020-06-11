package com.jiju.services.ratelimiter.storage.impl;

import com.jiju.services.ratelimiter.beans.Rule;
import com.jiju.services.ratelimiter.interfaces.RulesStore;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryRulesStore implements RulesStore {
    private Map<String, Rule> rulesStore=new ConcurrentHashMap<>();

    @Override
    public Rule getRuleForRequestPattern(String requestPattern) {
        return rulesStore.get(requestPattern);
    }

    @Override
    public void addRule(Rule r) {
        rulesStore.put(r.getRequestMatchPattern(),r);
    }

    @Override
    public void removeRule(Rule r){
        rulesStore.remove(r.getRequestMatchPattern());
    }
}
