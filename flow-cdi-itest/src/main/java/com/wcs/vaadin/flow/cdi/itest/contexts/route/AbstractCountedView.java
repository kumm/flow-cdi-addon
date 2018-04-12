package com.wcs.vaadin.flow.cdi.itest.contexts.route;

import com.vaadin.flow.component.html.Div;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public abstract class AbstractCountedView extends Div implements CountedPerUI {

    @PostConstruct
    private void construct() {
        countConstruct();
    }

    @PreDestroy
    private void destroy() {
        countDestroy();
    }
}
