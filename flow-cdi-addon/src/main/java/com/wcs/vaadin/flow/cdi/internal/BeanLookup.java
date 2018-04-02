package com.wcs.vaadin.flow.cdi.internal;

import com.wcs.vaadin.flow.cdi.VaadinServiceEnabled;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

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

    public final static Annotation SERVICE = new ServiceLiteral();

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
        this.qualifiers = qualifiers;
    }

    public Single single() {
        return new Single();
    }

    public Stream<T> select(Predicate<? super Bean<?>> predicate) {
        return getBeans().stream()
                .filter(predicate)
                .map(BeanLookup.this::getReference);
    }

    public class Single {
        private UnsatisfiedHandler unsatisfiedHandler = () -> {};
        private Supplier<T> fallback = () -> null;
        private Consumer<AmbiguousResolutionException> ambiguousHandler = e -> {};

        private Single() {
        }

        public Single ifUnsatisfied(UnsatisfiedHandler unsatisfiedHandler) {
            this.unsatisfiedHandler = unsatisfiedHandler;
            return this;
        }

        public Single ifAmbiguous(Consumer<AmbiguousResolutionException> ambiguousHandler) {
            this.ambiguousHandler = ambiguousHandler;
            return this;
        }

        public Single fallbackTo(Supplier<T> fallback) {
            this.fallback = fallback;
            return this;
        }

        public T get() {
            final Set<Bean<?>> beans = getBeans();
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
            return getReference(bean);
        }

    }

    private Set<Bean<?>> getBeans() {
        return beanManager.getBeans(type, qualifiers);
    }

    private T getReference(Bean<?> bean) {
        final CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
        //noinspection unchecked
        return (T) beanManager.getReference(bean, type, ctx);
    }

}
