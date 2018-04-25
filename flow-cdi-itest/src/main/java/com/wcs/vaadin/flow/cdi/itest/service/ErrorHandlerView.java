package com.wcs.vaadin.flow.cdi.itest.service;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.router.Route;

import javax.annotation.PostConstruct;

@Route("error-handler")
public class ErrorHandlerView extends Div {

    public static final String FAIL = "FAIL";

    @PostConstruct
    private void init() {
        NativeButton failBtn = new NativeButton("fail", event -> {
            if (true) {
                throw new NullPointerException();
            }
        });
        failBtn.setId(FAIL);
        add(failBtn);
    }
}
