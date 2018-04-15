package com.wcs.vaadin.flow.cdi;

import javax.enterprise.context.NormalScope;
import java.lang.annotation.*;

/**
 * Sister annotation to RouteScoped.
 *
 * @see RouteScoped
 */
@NormalScope
@Inherited
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD,
        ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface NormalRouteScoped {
}
