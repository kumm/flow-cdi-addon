package com.wcs.vaadin.flow.cdi;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Link a RouteScoped bean to its owner.
 * <p>
 * Use it together with {@link RouteScoped}, or {@link NormalRouteScoped}.
 * Owner class have to be a {@link RouterLayout}, or a @{@link Route}.
 */
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface RouteScopeOwner {
    /**
     * Owner class of the qualified RouteScoped bean.
     * Have to be a {@link RouterLayout}, or a @{@link Route}
     *
     * @return owner class
     */
    Class<? extends HasElement> value();
}
