package com.vaadin.flow.cdi.itest.contexts;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Route("injecter")
public class UIScopeInjecterView extends Div {
    @Inject
    UIScopedLabel label;

    @PostConstruct
    private void init() {
        add(label);
    }
}
