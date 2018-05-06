package com.wcs.vaadin.flow.cdi.internal;

import com.vaadin.flow.server.ServiceDestroyEvent;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import com.wcs.vaadin.flow.cdi.VaadinServiceScoped;
import com.wcs.vaadin.flow.cdi.server.CdiVaadinServlet;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;

import static javax.enterprise.event.Reception.IF_EXISTS;

public class VaadinServiceScopedContext extends AbstractContext {

    private ContextualStorageManager contextManager;

    public VaadinServiceScopedContext(BeanManager beanManager) {
        super(beanManager);
    }

    public void init(BeanManager beanManager) {
        contextManager = BeanProvider
                .getContextualReference(beanManager, ContextualStorageManager.class, false);
    }

    @Override
    protected ContextualStorage getContextualStorage(
            Contextual<?> contextual, boolean createIfNotExist) {
        CdiVaadinServlet servlet = (CdiVaadinServlet) VaadinServlet.getCurrent();
        String servletName;
        if (servlet != null) {
            servletName = servlet.getServletName();
        } else {
            servletName = CdiVaadinServlet.getCurrentServletName();
        }
        return contextManager.getContextualStorage(servletName, createIfNotExist);
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return VaadinServiceScoped.class;
    }

    @Override
    public boolean isActive() {
        VaadinServlet servlet = VaadinServlet.getCurrent();
        return servlet instanceof CdiVaadinServlet
                || (servlet == null
                && CdiVaadinServlet.getCurrentServletName() != null);
    }

    @ApplicationScoped
    public static class ContextualStorageManager
            extends AbstractContextualStorageManager<String> {

        public ContextualStorageManager() {
            super(true);
        }

        /**
         * Service destroy event observer.
         *
         * During application shutdown it is container specific whether
         * this observer being called, or not.
         * Application context destroy may happen earlier, and cleanup
         * done by {@link #destroyAll()}.
         *
         * @param event service destroy event
         */
        private void onServiceDestroy(@Observes(notifyObserver = IF_EXISTS)
                                              ServiceDestroyEvent event) {
            if (!(event.getSource() instanceof VaadinServletService)) {
                return;
            }
            VaadinServletService service = (VaadinServletService) event.getSource();
            String servletName = service.getServlet().getServletName();
            destroy(servletName);
        }

    }

}
