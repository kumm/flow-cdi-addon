package com.wcs.vaadin.flow.cdi.itest.deployment;

import com.wcs.vaadin.flow.cdi.RouteScopeOwner;
import com.wcs.vaadin.flow.cdi.RouteScoped;

@RouteScoped
@RouteScopeOwner(NonRouteComponent.class)
public class OwnedByANonRouteComponent {
}
