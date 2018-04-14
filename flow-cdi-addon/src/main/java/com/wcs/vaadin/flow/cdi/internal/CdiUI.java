package com.wcs.vaadin.flow.cdi.internal;

import com.vaadin.flow.component.PollEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.server.VaadinRequest;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;

@Vetoed
public class CdiUI extends UI {

    @Inject
    private Event<AfterNavigationEvent> afterNavigationTrigger;

    @Inject
    private Event<BeforeLeaveEvent> beforeLeaveTrigger;

    @Inject
    private Event<BeforeEnterEvent> beforeEnterTrigger;

    @Inject
    private Event<PollEvent> pollTrigger;

    @Override
    protected void init(VaadinRequest request) {
        BeanProvider.injectFields(this);
        addAfterNavigationListener(afterNavigationTrigger::fire);
        addBeforeLeaveListener(beforeLeaveTrigger::fire);
        addBeforeEnterListener(beforeEnterTrigger::fire);
        addPollListener(pollTrigger::fire);
        super.init(request);
    }

}
