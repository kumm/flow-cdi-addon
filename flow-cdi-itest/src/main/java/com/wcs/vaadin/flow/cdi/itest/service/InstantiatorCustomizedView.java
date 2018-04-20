package com.wcs.vaadin.flow.cdi.itest.service;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

import javax.annotation.PostConstruct;

@Route("")
public class InstantiatorCustomizedView extends Div {
    public static final String VIEW = "VIEW";
    public static final String CUSTOMIZED = "CUSTOMIZED";

    @PostConstruct
    private void init() {
        setId(VIEW);
    }

    public void customize() {
        setText(CUSTOMIZED);
    }
}
