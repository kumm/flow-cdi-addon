package com.wcs.vaadin.flow.cdi.internal;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.internal.AnnotationReader;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.internal.RouterUtil;
import com.wcs.vaadin.flow.cdi.RouteScopeOwner;

import javax.enterprise.inject.spi.Bean;
import java.lang.annotation.Annotation;
import java.util.List;

import static com.wcs.vaadin.flow.cdi.internal.InconsistentDeploymentException.ID.*;

/**
 * Validator for route scoped bean owner.
 *
 * This class depends on some internal helper method of vaadin framework.
 * It use the methods what are used by the framework while parsing route
 * metadata to be consistent.
 */
public class RouteScopedBeanValidator {

    public void validate(Bean<?> bean) {
        Class<? extends HasElement> ownerClass = getOwnerClass(bean);
        if (ownerClass != null && !isRouteComponentClass(ownerClass)) {
            throw new InconsistentDeploymentException(
                    ROUTE_SCOPE_OWNER_NOT_ROUTE_COMPONENT,
                    "@RouteScopeOwner of bean from " + bean.getBeanClass()
                            + " have to be a route component");
        }
        if (isRouteComponent(bean)) {
            if (ownerClass != null && !isAncestorOrSelf(bean, ownerClass)) {
                throw new InconsistentDeploymentException(
                        ROUTE_SCOPE_OWNER_UNREACHABLE,
                        "@RouteScopeOwner of bean from " + bean.getBeanClass()
                            + " have to be an ancestor in the route chain");
            }
        } else {
            if (ownerClass == null) {
                throw new InconsistentDeploymentException(
                        ROUTE_SCOPE_OWNER_MISSING,
                        "@RouteScopeOwner of bean from " + bean.getBeanClass()
                                + " is missing");
            }
        }
    }

    private boolean isRouteComponent(Bean<?> bean) {
        Class<?> beanClass = bean.getBeanClass();
        if (!bean.getTypes().contains(beanClass)) {
            // It is a bean of a producer,
            // or the bean class excluded from bean types with @Type.
            // Both case is unsupported for Route Components.
            return false;
        }
        return isRouteComponentClass(beanClass);
    }

    private boolean isRouteComponentClass(Class<?> clazz) {
        return AnnotationReader.getAnnotationFor(clazz, Route.class).isPresent()
                || HasErrorParameter.class.isAssignableFrom(clazz)
                || RouterLayout.class.isAssignableFrom(clazz);
    }

    private Class<? extends HasElement> getOwnerClass(Bean<?> bean) {
        for (Annotation annotation : bean.getQualifiers()) {
            if (annotation.annotationType().equals(RouteScopeOwner.class)) {
                return ((RouteScopeOwner) annotation).value();
            }
        }
        return null;
    }

    private boolean isAncestorOrSelf(Bean<?> bean,
                                     Class<? extends HasElement> ownerClass) {
        Class<?> beanClass = bean.getBeanClass();
        if (ownerClass.equals(beanClass)) {
            return true;
        }
        if (!RouterLayout.class.isAssignableFrom(ownerClass)) {
            return false;
        }
        List<Class<? extends RouterLayout>> path;
        if (HasErrorParameter.class.isAssignableFrom(beanClass)) {
            path = RouterUtil.getParentLayoutsForNonRouteTarget(beanClass);
        } else {
            path = RouterUtil.getParentLayouts(beanClass);
        }
        //noinspection SuspiciousMethodCalls
        return path.contains(ownerClass);
    }

}
