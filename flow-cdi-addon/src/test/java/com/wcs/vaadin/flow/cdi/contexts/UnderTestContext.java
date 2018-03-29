package com.wcs.vaadin.flow.cdi.contexts;

public interface UnderTestContext {
    void activate();

    void tearDownAll();

    void destroy();
}
