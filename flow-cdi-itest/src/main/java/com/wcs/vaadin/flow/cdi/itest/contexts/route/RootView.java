package com.wcs.vaadin.flow.cdi.itest.contexts.route;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.wcs.vaadin.flow.cdi.RouteScoped;

import javax.annotation.PostConstruct;

@Route("")
@RouteScoped
public class RootView extends AbstractCountedView {

    public static final String MASTER = "master";
    public static final String UIID = "UIID";

    @PostConstruct
    private void init() {
        Label uiIdLabel = new Label(UI.getCurrent().getUIId() + "");
        uiIdLabel.setId(UIID);
        add(
                new Div(uiIdLabel),
                new Div(new Label("ROOT")),
                new Div(new RouterLink(MASTER, MasterView.class))
        );
    }

}