package com.wcs.vaadin.flow.cdi;

import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import javax.inject.Scope;
import java.lang.annotation.*;

/**
 * The lifecycle of a RouteScoped component is controlled by route navigation.
 * <p>
 * Every RouteScoped bean belongs to one router component owner.
 * It can be a @{@link Route}, a {@link RouterLayout},
 * or a @{@link HasErrorParameter}.
 * Beans are qualified by @{@link RouteScopeOwner} to link with their owner.
 * <p>
 * Until owner remains active, all beans owned by it remain in the scope.
 * <p>
 * When a RouteScoped bean is a router component,
 * an owner can be any ancestor {@link RouterLayout}, or the bean itself.
 * Omitting the RouteScopeOwner annotation means owner is the bean itself.
 * <p>
 * Injection with this annotation will create a direct reference to the object
 * rather than a proxy.
 * <p>
 * There are some limitations when not using proxies. Circular referencing (that
 * is, injecting A to B and B to A) will not work.
 * <p>
 * The sister annotation to this is the {@link NormalRouteScoped}. Both annotations
 * reference the same underlying scope, so it is possible to get both a proxy
 * and a direct reference to the same object by using different annotations.
 */
@Scope
@Inherited
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD,
        ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface RouteScoped {
}
