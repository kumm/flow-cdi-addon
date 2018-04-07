package com.wcs.vaadin.flow.cdi.itest.smoke;

import com.vaadin.flow.component.PollEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.Serializable;

@Route("uitest")
public class CdiUITestView extends Div {
    public static final String AFTER_NAVIGATION_PATH = "AFTER_NAVIGATION_PATH";
    public static final String BEFORE_ENTER_TRIGGER = "BEFORE_ENTER_TRIGGER";
    public static final String BEFORE_LEAVE_POSTPONED = "BEFORE_LEAVE_POSTPONED";
    public static final String SHOW_NAVIGATION_EVENTS = "SHOW_NAVIGATION_EVENTS";
    public static final String SHOW_POLL_EVENT = "SHOW_POLL_EVENT";
    public static final String POLL_IS_FROM_CLIENT = "POLL_IS_FROM_CLIENT";

    @Inject
    private UIObserver uiObserver;

    public CdiUITestView() {
        NativeButton navEventBtn = new NativeButton("show navigation events",
                event -> showNavigationEvents());
        navEventBtn.setId(SHOW_NAVIGATION_EVENTS);
        add(navEventBtn);

        UI.getCurrent().setPollInterval(500);

        NativeButton pollEventBtn = new NativeButton("show poll event",
                event -> showPollEvent());
        pollEventBtn.setId(SHOW_POLL_EVENT);
        add(pollEventBtn);
    }

    private void showNavigationEvents() {
        final Label pathWithParams = new Label(uiObserver
                .getAfterNavigationEvent().getLocation().getPathWithQueryParameters());
        pathWithParams.setId(AFTER_NAVIGATION_PATH);
        add(new Div(pathWithParams));

        final Label beforeEnter = new Label(uiObserver
                .getBeforeEnterEvent().getTrigger().name());
        beforeEnter.setId(BEFORE_ENTER_TRIGGER);
        add(new Div(beforeEnter));

        final Label beforeLeave = new Label(uiObserver
                .getBeforeLeaveEvent().isPostponed() + "");
        beforeLeave.setId(BEFORE_LEAVE_POSTPONED);
        add(new Div(beforeLeave));
    }

    private void showPollEvent() {
        final Label poll = new Label(uiObserver
                .getPollEvent().isFromClient() + "");
        poll.setId(POLL_IS_FROM_CLIENT);
        add(new Div(poll));
    }


    @SessionScoped
    public static class UIObserver implements Serializable {
        private AfterNavigationEvent afterNavigationEvent;
        private BeforeEnterEvent beforeEnterEvent;
        private BeforeLeaveEvent beforeLeaveEvent;
        private PollEvent pollEvent;

        private void onAfterNavigation(@Observes AfterNavigationEvent event) {
            afterNavigationEvent = event;
        }
        private void onBeforeEnter(@Observes BeforeEnterEvent event) {
            beforeEnterEvent = event;
        }
        private void onBeforeLeave(@Observes BeforeLeaveEvent event) {
            beforeLeaveEvent = event;
        }
        private void onPoll(@Observes PollEvent event) {
            pollEvent = event;
        }

        public AfterNavigationEvent getAfterNavigationEvent() {
            return afterNavigationEvent;
        }

        public BeforeEnterEvent getBeforeEnterEvent() {
            return beforeEnterEvent;
        }

        public BeforeLeaveEvent getBeforeLeaveEvent() {
            return beforeLeaveEvent;
        }

        public PollEvent getPollEvent() {
            return pollEvent;
        }
    }
}
