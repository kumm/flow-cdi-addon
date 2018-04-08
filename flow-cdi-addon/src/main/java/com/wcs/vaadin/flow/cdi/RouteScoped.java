package com.wcs.vaadin.flow.cdi;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import javax.inject.Scope;
import java.lang.annotation.*;

/**
 * The lifecycle of a RouteScoped component is controlled by route navigation.
 * <p>
 * Every RouteScoped bean belongs to one {@link RouterLayout}, or @{@link Route} owner.
 * Beans are qualified by @{@link RouteScopeOwner} to link with their owner.
 * <p>
 * Until owner remains in the active route chain after navigation,
 * all beans owned by it remain in the scope.
 * <p>
 * When a RouteScoped bean is a router layout, or route target
 * an owner must be any ancestor {@link RouterLayout}, or the bean itself.
 * In this case omitting the RouteScopeOwner annotation is possible,
 * it means it is an owner of self.
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
