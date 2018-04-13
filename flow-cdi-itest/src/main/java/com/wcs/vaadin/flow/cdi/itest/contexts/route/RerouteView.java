package com.wcs.vaadin.flow.cdi.itest.contexts.route;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.wcs.vaadin.flow.cdi.RouteScoped;

@Route("reroute")
@RouteScoped
public class RerouteView extends AbstractCountedView implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.rerouteTo(RootView.class);
    }
}
