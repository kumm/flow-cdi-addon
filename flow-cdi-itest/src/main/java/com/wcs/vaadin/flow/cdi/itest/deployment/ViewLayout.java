package com.wcs.vaadin.flow.cdi.itest.deployment;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.RouterLayout;
import com.wcs.vaadin.flow.cdi.RouteScopeOwner;
import com.wcs.vaadin.flow.cdi.RouteScoped;

@RouteScoped
@RouteScopeOwner(ChildView.class)
public class ViewLayout extends Div implements RouterLayout {
}
