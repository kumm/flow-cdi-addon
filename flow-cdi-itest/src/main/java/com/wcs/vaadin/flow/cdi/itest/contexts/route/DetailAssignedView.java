package com.wcs.vaadin.flow.cdi.itest.contexts.route;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.wcs.vaadin.flow.cdi.RouteScopeOwner;
import com.wcs.vaadin.flow.cdi.RouteScoped;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@RouteScoped
@RouteScopeOwner(MasterView.class)
@Route(value = "assigned", layout = MasterView.class)
public class DetailAssignedView extends AbstractCountedView implements AfterNavigationObserver {
    public static final String MASTER = "master";
    public static final String BEAN_LABEL = "BEAN_LABEL";

    @Inject
    @RouteScopeOwner(MasterView.class)
    private AssignedBean assignedBean;

    private Label assignedLabel;

    @PostConstruct
    private void init() {
        assignedLabel = new Label();
        assignedLabel.setId(BEAN_LABEL);
        assignedBean.setData("ASSIGNED");
        add(
                assignedLabel,
                new Div(new RouterLink(MASTER, MasterView.class))
        );
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        assignedLabel.setText(assignedBean.getData());
    }

}

