package com.wcs.vaadin.flow.cdi.itest.deployment;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@Route(value = "some", layout = ViewLayout.class)
public class ChildView extends Div {
}
