package com.wcs.vaadin.flow.cdi.internal;

import com.vaadin.flow.server.VaadinService;
import com.wcs.vaadin.flow.cdi.VaadinServiceScoped;
import com.wcs.vaadin.flow.cdi.server.CdiVaadinServletService;
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
    protected ContextualStorage getContextualStorage(Contextual<?> contextual, boolean createIfNotExist) {
        VaadinService vaadinService = VaadinService.getCurrent();
        return ((CdiVaadinServletService) vaadinService).getContextualStorage();
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return VaadinServiceScoped.class;
    }

    @Override
    public boolean isActive() {
        VaadinService service = VaadinService.getCurrent();
        return service instanceof CdiVaadinServletService;
    }

    public static void destroy(CdiVaadinServletService service) {
        AbstractContext.destroyAllActive(service.getContextualStorage());
    }
}
