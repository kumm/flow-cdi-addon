package com.wcs.vaadin.flow.cdi.itest.contexts.route;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.wcs.vaadin.flow.cdi.RouteScoped;

import javax.annotation.PostConstruct;

@RouteScoped
@Route("error-layout")
public class ErrorParentView extends AbstractCountedView
        implements RouterLayout {

    public static final String ROOT = "root";

    @PostConstruct
    private void init() {
        add(new RouterLink(ROOT, RootView.class));
    }
}
