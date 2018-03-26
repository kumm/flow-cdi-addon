package com.vaadin.flow.cdi.internal;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BeanLookup<T> {
    private final BeanManager beanManager;
    private final Class<T> type;
    private final Annotation[] qualifiers;
    private UnsatisfiedHandler unsatisfiedHandler = () -> {};
    private Supplier<T> fallback = () -> null;
    private Consumer<AmbiguousResolutionException> ambiguousHandler = e -> {
        throw e;
    };

    public BeanLookup(BeanManager beanManager, Class<T> type, Annotation... qualifiers) {
        this.beanManager = beanManager;
        this.type = type;
        this.qualifiers = qualifiers;
    }

    public BeanLookup<T> ifUnsatisfied(UnsatisfiedHandler unsatisfiedHandler) {
        this.unsatisfiedHandler = unsatisfiedHandler;
        return this;
    }

    public BeanLookup<T> ifAmbiguous(Consumer<AmbiguousResolutionException> ambiguousHandler) {
        this.ambiguousHandler = ambiguousHandler;
        return this;
    }

    public BeanLookup<T> fallbackTo(Supplier<T> fallback) {
        this.fallback = fallback;
        return this;
    }

    public T getContextualReference() {
        final Set<Bean<?>> beans = beanManager.getBeans(type, qualifiers);
        if (beans == null || beans.isEmpty()) {
            unsatisfiedHandler.handle();
            return fallback.get();
        }
        final Bean<?> bean;
        try {
            bean = beanManager.resolve(beans);
        } catch (AmbiguousResolutionException e) {
            ambiguousHandler.accept(e);
            return fallback.get();
        }
        final CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
        //noinspection unchecked
        return (T) beanManager.getReference(bean, type, ctx);
    }

    @FunctionalInterface
    public interface UnsatisfiedHandler {
        void handle();
    }
}
