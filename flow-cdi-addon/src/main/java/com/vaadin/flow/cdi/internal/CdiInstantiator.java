package com.vaadin.flow.cdi.internal;

import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.apache.deltaspike.core.api.literal.AnyLiteral;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.stream.Stream;

public class CdiInstantiator extends DefaultInstantiator {

    private final BeanManager beanManager;

    public CdiInstantiator(VaadinService service, BeanManager beanManager) {
        super(service);
        this.beanManager = beanManager;
    }

    @Override
    public <T> T getOrCreate(Class<T> type) {
        final T reference = BeanProvider
                .getContextualReference(beanManager, type, true);
        if (reference != null) {
            return reference;
        } else {
            return super.getOrCreate(type);
        }
    }

    @Override
    public I18NProvider getI18NProvider() {
        try {
            return BeanProvider.getContextualReference(beanManager, I18NProvider.class, false);
        } catch (AmbiguousResolutionException  e) {
            logI18NFallback("Found more beans implementing '{}'.");
        } catch (IllegalStateException e) {
            logI18NFallback("Can't find any bean implementing '{}'.");
        }
        return super.getI18NProvider();
    }

    private void logI18NFallback(String s) {
        LoggerFactory.getLogger(CdiInstantiator.class.getName()).info(
                s + " Cannot use CDI beans for I18N, falling back to the default behavior",
                I18NProvider.class.getSimpleName());
    }

    @Override
    public Stream<VaadinServiceInitListener> getServiceInitListeners() {
        Stream<VaadinServiceInitListener> cdiListeners = beanManager
                .getBeans(VaadinServiceInitListener.class, new AnyLiteral())
                .stream().map(this::getServiceInitListenerReference);
        return Stream.concat(super.getServiceInitListeners(), cdiListeners);
    }

    private VaadinServiceInitListener getServiceInitListenerReference(Bean<?> bean) {
        final CreationalContext<?> creationalContext = beanManager
                .createCreationalContext(bean);
        return (VaadinServiceInitListener) beanManager.getReference(bean,
                VaadinServiceInitListener.class,
                creationalContext);
    }
}
