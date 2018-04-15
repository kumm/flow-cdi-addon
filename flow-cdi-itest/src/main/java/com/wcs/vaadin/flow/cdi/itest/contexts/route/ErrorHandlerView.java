package com.wcs.vaadin.flow.cdi.itest.contexts.route;

import com.vaadin.flow.router.*;
import com.wcs.vaadin.flow.cdi.RouteScopeOwner;
import com.wcs.vaadin.flow.cdi.RouteScoped;

import javax.servlet.http.HttpServletResponse;

@RouteScoped
@RouteScopeOwner(ErrorParentView.class)
@ParentLayout(ErrorParentView.class)
public class ErrorHandlerView extends AbstractCountedView
        implements HasErrorParameter<NullPointerException> {

    public static final String PARENT = "parent";

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
                                 ErrorParameter<NullPointerException> parameter) {
        add(
                new RouterLink(PARENT, ErrorParentView.class)
        );
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }
}
