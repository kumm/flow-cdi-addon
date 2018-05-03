package com.wcs.vaadin.flow.cdi.internal;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinRequest;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

@Vetoed
public class CdiUI extends UI {

    @Inject
    private BeanManager beanManager;

    @ListenerPriority(-100)
    private static class NavigationListener
            implements AfterNavigationListener, BeforeEnterListener, BeforeLeaveListener {

        final BeanManager beanManager;

        private NavigationListener(BeanManager beanManager) {
            this.beanManager = beanManager;
        }

        @Override
        public void afterNavigation(AfterNavigationEvent event) {
            beanManager.fireEvent(event);
        }

        @Override
        public void beforeEnter(BeforeEnterEvent event) {
            beanManager.fireEvent(event);
        }

        @Override
        public void beforeLeave(BeforeLeaveEvent event) {
            beanManager.fireEvent(event);
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        BeanProvider.injectFields(this);
        NavigationListener listener = new NavigationListener(beanManager);
        addAfterNavigationListener(listener);
        addBeforeLeaveListener(listener);
        addBeforeEnterListener(listener);
        addPollListener(beanManager::fireEvent);
        super.init(request);
    }

}
