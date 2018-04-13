package com.wcs.vaadin.flow.cdi.itest.contexts.route;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.wcs.vaadin.flow.cdi.RouteScoped;

import javax.annotation.PostConstruct;

@Route("postpone")
@RouteScoped
public class PostponeView extends AbstractCountedView implements BeforeLeaveObserver {

    public static final String NAVIGATE = "NAVIGATE";
    public static final String POSTPONED_ROOT = "postpone";

    private BeforeLeaveEvent.ContinueNavigationAction navigationAction;

    @PostConstruct
    private void init() {
        NativeButton navBtn = new NativeButton("navigate", clickEvent
                -> navigationAction.proceed());
        navBtn.setId(NAVIGATE);

        add(
                new Div(new RouterLink(POSTPONED_ROOT, RootView.class)),
                new Div(navBtn)
        );
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        navigationAction = beforeLeaveEvent.postpone();
    }
}
