package com.wcs.vaadin.flow.cdi.internal;

import com.vaadin.flow.server.VaadinServlet;
import com.wcs.vaadin.flow.cdi.VaadinServiceScoped;
import com.wcs.vaadin.flow.cdi.server.CdiVaadinServlet;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;

public class VaadinServiceScopedContext extends AbstractContext {

    public VaadinServiceScopedContext(BeanManager beanManager) {
        super(beanManager);
    }

    @Override
    protected ContextualStorage getContextualStorage(
            Contextual<?> contextual, boolean createIfNotExist) {
        CdiVaadinServlet servlet = (CdiVaadinServlet) VaadinServlet.getCurrent();
        if (servlet != null) {
            return servlet.getContextualStorage();
        } else {
            return CdiVaadinServlet.getCurrentContextualStorage();
        }
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
                    && CdiVaadinServlet.getCurrentContextualStorage() != null);
    }

}
