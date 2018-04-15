package com.wcs.vaadin.flow.cdi.itest.contexts.route;

import com.wcs.vaadin.flow.cdi.NormalRouteScoped;
import com.wcs.vaadin.flow.cdi.RouteScopeOwner;

@NormalRouteScoped
@RouteScopeOwner(MasterView.class)
public class AssignedBean extends AbstractCountedBean {
}
