package com.wcs.vaadin.flow.cdi.itest.contexts.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.wcs.vaadin.flow.cdi.UIScoped;

import javax.annotation.PostConstruct;

@Route("uiscoped")
@UIScoped
public class UIScopedView extends Div {

    public static final String VIEWSTATE_LABEL = "VIEWSTATE_LABEL";
    public static final String SETSTATE_BTN = "SETSTATE_BTN";
    public static final String ROOT_LINK = "root view";

    @PostConstruct
    private void init() {
        final Label state = new Label("");
        state.setId(VIEWSTATE_LABEL);

        final String uiIdStr = UI.getCurrent().getUIId() + "";

        final NativeButton button =
                new NativeButton("set state", event -> state.setText(uiIdStr));
        button.setId(SETSTATE_BTN);

        add(button, state,
                new RouterLink(ROOT_LINK, UIContextRootView.class));
    }
}
