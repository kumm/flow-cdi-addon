package com.wcs.vaadin.flow.cdi.itest.contexts.route;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.*;
import com.wcs.vaadin.flow.cdi.RouteScopeOwner;
import com.wcs.vaadin.flow.cdi.RouteScoped;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@RouteScoped
@Route("")
@RoutePrefix("master")
public class MasterView extends AbstractCountedView
        implements RouterLayout, AfterNavigationObserver {

    public static final String ASSIGNED = "assigned";
    public static final String ASSIGNED_BEAN_LABEL = "ASSIGNED";
    public static final String APART = "apart";
    public static final String APART_BEAN_LABEL = "APART";

    @Inject
    @RouteScopeOwner(MasterView.class)
    private AssignedBean assignedBean;

    @Inject
    @RouteScopeOwner(DetailApartView.class)
    private ApartBean apartBean;

    private Label assignedLabel;
    private Label apartLabel;

    @PostConstruct
    private void init() {
        assignedLabel = new Label();
        assignedLabel.setId(ASSIGNED_BEAN_LABEL);
        apartLabel = new Label();
        apartLabel.setId(APART_BEAN_LABEL);
        add(
                new Label("MASTER"),
                new Div(assignedLabel),
                new Div(apartLabel),
                new Div(new RouterLink(ASSIGNED, DetailAssignedView.class)),
                new Div(new RouterLink(APART, DetailApartView.class))
        );
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        assignedLabel.setText(assignedBean.getData());
        apartLabel.setText(apartBean.getData());
    }

}
