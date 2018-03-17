package com.vaadin.flow.cdi.contexts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicInteger;

public class TestBean {
    private static AtomicInteger beanCount = new AtomicInteger();
    private String state = "";

    @PostConstruct
    private void construct() {
        beanCount.incrementAndGet();
    }

    @PreDestroy
    private void destruct() {
        beanCount.decrementAndGet();
    }

    public static int getBeanCount() {
        return beanCount.get();
    }

    public static void resetCount() {
        beanCount.set(0);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
