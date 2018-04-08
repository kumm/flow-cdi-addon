package com.wcs.vaadin.flow.cdi.internal;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.PassivationCapable;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Utility base class to extract conversation key from bean.
 * <p>
 * Conversation key means the owner class in @{RouteScoped} context.
 *
 * @param <A> Qualifier holding the key
 * @param <K> Key type
 */
abstract class AbstractConversationKeyConverter<A extends Annotation, K> {
    private final BeanManager beanManager;
    private final Class<A> qualifierClass;

    AbstractConversationKeyConverter(BeanManager beanManager, Class<A> qualifierClass) {
        this.beanManager = beanManager;
        this.qualifierClass = qualifierClass;
    }

    K convertToKey(Contextual<?> contextual) {
        if (!(contextual instanceof Bean)) {
            if (contextual instanceof PassivationCapable) {
                final String id = ((PassivationCapable) contextual).getId();
                contextual = beanManager.getPassivationCapableBean(id);
            } else {
                throw new IllegalArgumentException(
                        contextual.getClass().getName()
                                + " is not of type " + Bean.class.getName());
            }
        }

        final Bean<?> bean = (Bean<?>) contextual;
        final A qualifier = findQualifier(bean);

        K key;
        if (qualifier != null) {
            key = extractKey(qualifier);
        } else {
            key = selfKey(bean);
        }

        return key;
    }

    abstract K extractKey(A qualifier);

    private A findQualifier(Bean<?> bean) {
        Set<Annotation> qualifiers = bean.getQualifiers();
        for (Annotation qualifier : qualifiers) {
            if (qualifierClass.isAssignableFrom(qualifier.annotationType())) {
                //noinspection unchecked
                return (A) qualifier;
            }
        }
        return null;
    }

    K selfKey(Bean<?> bean) {
        //noinspection unchecked
        return (K) bean.getBeanClass();
    }
}
