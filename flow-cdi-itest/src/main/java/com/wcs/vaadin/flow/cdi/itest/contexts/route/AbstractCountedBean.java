package com.wcs.vaadin.flow.cdi.itest.contexts.route;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class AbstractCountedBean implements CountedPerUI {
    private String data = "";

    @PostConstruct
    private void construct() {
        countConstruct();
    }

    @PreDestroy
    private void destroy() {
        countDestroy();
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
