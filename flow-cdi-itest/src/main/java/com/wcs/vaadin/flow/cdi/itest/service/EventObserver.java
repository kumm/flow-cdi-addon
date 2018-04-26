package com.wcs.vaadin.flow.cdi.itest.service;

import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.SessionInitEvent;
import com.wcs.vaadin.flow.cdi.itest.Counter;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

public class EventObserver {
    @Inject
    Counter counter;

    private void onSessionInit(@Observes SessionInitEvent sessionInitEvent) {
        counter.increment(SessionInitEvent.class.getSimpleName());
    }

    private void onSessionDestroy(@Observes SessionDestroyEvent sessionDestroyEvent) {
        counter.increment(SessionDestroyEvent.class.getSimpleName());
    }
}
