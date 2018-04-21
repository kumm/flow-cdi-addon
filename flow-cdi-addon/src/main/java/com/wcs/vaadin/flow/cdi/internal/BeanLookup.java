package com.wcs.vaadin.flow.cdi.internal;

import com.wcs.vaadin.flow.cdi.VaadinServiceEnabled;
import org.apache.deltaspike.core.api.literal.AnyLiteral;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility class for CDI lookup, and instantiation.
 * <p>
 * Dependent beans are instantiated without any warning,
 * but do not get destroyed properly.
 * {@link javax.annotation.PreDestroy} won't run.
 *
 * @param <T> Bean Type
 */
public class BeanLookup<T> {
    private final BeanManager beanManager;
    private final Class<T> type;
    private final Annotation[] qualifiers;
    private UnsatisfiedHandler unsatisfiedHandler = () -> {};
    private Supplier<T> fallback = () -> null;
    private Consumer<AmbiguousResolutionException> ambiguousHandler = e -> {
        throw e;
    };

    public final static Annotation SERVICE = new ServiceLiteral();
    private final static Annotation[] ANY = new Annotation[]{new AnyLiteral()};

    private static class ServiceLiteral
            extends AnnotationLiteral<VaadinServiceEnabled>
            implements VaadinServiceEnabled {

    }

    @FunctionalInterface
    public interface UnsatisfiedHandler {
        void handle();
    }

    public BeanLookup(BeanManager beanManager, Class<T> type, Annotation... qualifiers) {
        this.beanManager = beanManager;
        this.type = type;
        if (qualifiers.length > 0) {
            this.qualifiers = qualifiers;
        } else {
            this.qualifiers = ANY;
        }
    }

    public BeanLookup<T> ifUnsatisfied(UnsatisfiedHandler unsatisfiedHandler) {
        this.unsatisfiedHandler = unsatisfiedHandler;
        return this;
    }

    public BeanLookup<T> ifAmbiguous(
            Consumer<AmbiguousResolutionException> ambiguousHandler) {
        this.ambiguousHandler = ambiguousHandler;
        return this;
    }

    public BeanLookup<T> fallbackTo(Supplier<T> fallback) {
        this.fallback = fallback;
        return this;
    }

    public T get() {
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

}
