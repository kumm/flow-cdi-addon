package com.wcs.vaadin.flow.cdi.itest.service;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import javax.annotation.PostConstruct;

@Route("")
public class SystemMessagesProviderView extends Div {

    public static final String EXPIRE = "EXPIRE";
    public static final String ACTION = "ACTION";

    @PostConstruct
    private void init() {
        NativeButton expireBtn = new NativeButton("expire", event ->
                VaadinSession.getCurrent().getSession().invalidate());
        expireBtn.setId(EXPIRE);

        NativeButton button = new NativeButton("an action", event -> {
        });
        button.setId(ACTION);

        add(expireBtn, button);
    }
}
