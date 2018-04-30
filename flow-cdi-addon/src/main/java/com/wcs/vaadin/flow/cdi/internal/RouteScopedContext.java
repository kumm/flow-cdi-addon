package com.wcs.vaadin.flow.cdi.internal;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.wcs.vaadin.flow.cdi.NormalUIScoped;
import com.wcs.vaadin.flow.cdi.RouteScopeOwner;
import com.wcs.vaadin.flow.cdi.RouteScoped;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.enterprise.event.Reception.IF_EXISTS;

public class RouteScopedContext extends AbstractContext {

    private ContextualStorageManager contextManager;
    private KeyConverter keyConverter;

    public RouteScopedContext(BeanManager beanManager) {
        super(beanManager);
    }

    public void init(BeanManager beanManager) {
        contextManager = BeanProvider
                .getContextualReference(beanManager, ContextualStorageManager.class, false);
        keyConverter = new KeyConverter(beanManager);
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return RouteScoped.class;
    }

    @Override
    public boolean isActive() {
        return UI.getCurrent() != null;
    }

    @Override
    protected ContextualStorage getContextualStorage(Contextual<?> contextual,
                                                     boolean createIfNotExist) {
        Class<? extends HasElement> key = keyConverter.convertToKey(contextual);
        return contextManager.getContextualStorage(key, createIfNotExist);
    }

    @NormalUIScoped
    public static class ContextualStorageManager
            extends AbstractContextualStorageManager<Class<?>> {

        public ContextualStorageManager() {
            super(new HashMap<>());
        }

        private void onAfterNavigation(@Observes(notifyObserver = IF_EXISTS)
                                               AfterNavigationEvent event) {
            Set<? extends Class<?>> activeChain = event.getActiveChain().stream()
                    .map(Object::getClass)
                    .collect(Collectors.toSet());

            Set<Class<?>> missingFromChain = getAll().keySet().stream()
                    .filter(routeCompClass -> !activeChain.contains(routeCompClass))
                    .collect(Collectors.toSet());

            missingFromChain.forEach(this::destroy);
        }

    }

    private static class KeyConverter extends
            AbstractConversationKeyConverter<RouteScopeOwner, Class<? extends HasElement>> {

        KeyConverter(BeanManager beanManager) {
            super(beanManager, RouteScopeOwner.class);
        }

        @Override
        Class<? extends HasElement> extractKey(RouteScopeOwner qualifier) {
            return qualifier.value();
        }
    }
}
