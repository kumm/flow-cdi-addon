package com.wcs.vaadin.flow.cdi;

import javax.enterprise.context.NormalScope;
import java.lang.annotation.*;

/**
 * The lifecycle of a VaadinServiceScoped bean is bound to a VaadinService.
 * <p>
 * Injecting with this annotation will create a proxy for the contextual
 * instance rather than provide the contextual instance itself.
 */
@NormalScope
@Inherited
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface VaadinServiceScoped {
}
