package com.wcs.vaadin.flow.cdi.itest.contexts.route;

import com.vaadin.flow.component.UI;
import com.wcs.vaadin.flow.cdi.itest.Counter;
import org.apache.deltaspike.core.api.provider.BeanProvider;

public interface CountedPerUI {
    default int getUiId() {
        return UI.getCurrent().getUIId();
    }

    default Counter getCounter() {
        return BeanProvider.getContextualReference(Counter.class);
    }

    default void countConstruct() {
        getCounter().increment(getClass().getSimpleName() + "C" + getUiId());
    }

    default void countDestroy() {
        getCounter().increment(getClass().getSimpleName() + "D" + getUiId());
    }
}
