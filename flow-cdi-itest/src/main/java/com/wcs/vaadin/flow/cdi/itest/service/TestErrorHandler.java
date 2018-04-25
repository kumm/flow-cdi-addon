package com.wcs.vaadin.flow.cdi.itest.service;


import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;
import com.wcs.vaadin.flow.cdi.VaadinServiceEnabled;
import com.wcs.vaadin.flow.cdi.VaadinServiceScoped;
import com.wcs.vaadin.flow.cdi.itest.Counter;

import javax.inject.Inject;

@VaadinServiceEnabled
@VaadinServiceScoped
public class TestErrorHandler implements ErrorHandler {

    @Inject
    Counter counter;

    @Override
    public void error(ErrorEvent event) {
        counter.increment(TestErrorHandler.class.getSimpleName());
    }
}
