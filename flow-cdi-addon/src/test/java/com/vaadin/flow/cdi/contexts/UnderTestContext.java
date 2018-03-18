package com.vaadin.flow.cdi.contexts;

public interface UnderTestContext {
    void activate();

    void tearDown();

    void destroy();
}
