package com.wcs.vaadin.flow.cdi.itest.contexts.route;


import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.router.Route;
import com.wcs.vaadin.flow.cdi.RouteScopeOwner;
import com.wcs.vaadin.flow.cdi.RouteScoped;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@Route("event")
@RouteScoped
public class EventView extends Div {

    public static final String FIRE = "FIRE";
    public static final String OBSERVER_LABEL = "OBSERVER_LABEL";

    @Inject
    @RouteScopeOwner(EventView.class)
    Label label;

    @Inject
    Event<PrintEvent> printEventTrigger;

    @PostConstruct
    private void init() {
        label.setId(OBSERVER_LABEL);
        NativeButton fireBtn = new NativeButton("fire event", clickEvent
                -> printEventTrigger.fire(new PrintEvent("HELLO")));
        fireBtn.setId(FIRE);

        add(fireBtn, label);
    }


    @RouteScoped
    @RouteScopeOwner(EventView.class)
    public static class ObserverLabel extends Label {
        private void onPrintEvent(@Observes PrintEvent printEvent) {
            setText(printEvent.getMessage());
        }
    }

    public static class PrintEvent {
        private final String message;

        public PrintEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
