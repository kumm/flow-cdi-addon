package com.wcs.vaadin.flow.cdi.internal;

import com.vaadin.flow.component.Component;
import com.wcs.vaadin.flow.cdi.NormalUIScoped;
import com.wcs.vaadin.flow.cdi.UIScoped;
import com.wcs.vaadin.flow.cdi.VaadinServiceScoped;
import com.wcs.vaadin.flow.cdi.VaadinSessionScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

/**
 * CDI Extension needed to register Vaadin scopes to the runtime.
 */
public class VaadinExtension implements Extension {

    private UIScopedContext uiScopedContext;
    private List<String> normalScopedComponentWarnings = new LinkedList<String>();

    public void initializeContexts(@Observes AfterDeploymentValidation adv, BeanManager beanManager) {
        uiScopedContext.init(beanManager);
    }

    void processManagedBean(@Observes ProcessManagedBean pmb,
                            final BeanManager beanManager) {
        Bean<?> bean = pmb.getBean();
        Class<?> beanClass = bean.getBeanClass();
        Class<? extends Annotation> beanScope = bean.getScope();

        if (Component.class.isAssignableFrom(beanClass)
                && beanManager.isNormalScope(beanScope)) {
            normalScopedComponentWarnings.add("@"
                    + String.format("%-20s", beanScope.getSimpleName()) + " "
                    + beanClass.getCanonicalName());
        }
    }

    void afterBeanDiscovery(
            @Observes final AfterBeanDiscovery afterBeanDiscovery,
            final BeanManager beanManager) {

        if (normalScopedComponentWarnings.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("The following Vaadin components are injected into "
                    + "normal scoped contexts:\n");
            for (String proxiedComponent : normalScopedComponentWarnings) {
                sb.append("   ");
                sb.append(proxiedComponent);
                sb.append("\n");
            }
            sb.append("This approach uses proxy objects and is "
                    + "not supported in Vaadin framework.");
            getLogger().error(sb.toString());
        }

        VaadinServiceScopedContext vaadinServiceScopedContext =
                new VaadinServiceScopedContext(beanManager);
        afterBeanDiscovery.addContext(
                new ContextWrapper(vaadinServiceScopedContext, VaadinServiceScoped.class));
        getLogger().info("VaadinServiceScopedContext registered for Vaadin CDI");

        VaadinSessionScopedContext vaadinSessionScopedContext =
                new VaadinSessionScopedContext(beanManager);
        afterBeanDiscovery.addContext(
                new ContextWrapper(vaadinSessionScopedContext, VaadinSessionScoped.class));
        getLogger().info("VaadinSessionScopedContext registered for Vaadin CDI");

        uiScopedContext = new UIScopedContext(beanManager);
        afterBeanDiscovery.addContext(new ContextWrapper(uiScopedContext,
                UIScoped.class));
        afterBeanDiscovery.addContext(new ContextWrapper(uiScopedContext,
                NormalUIScoped.class));
        getLogger().info("UIScopedContext registered for Vaadin CDI");
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(VaadinExtension.class.getCanonicalName());
    }
}
