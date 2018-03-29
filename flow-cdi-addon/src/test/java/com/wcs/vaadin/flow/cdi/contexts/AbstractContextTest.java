package com.wcs.vaadin.flow.cdi.contexts;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.enterprise.context.ContextNotActiveException;

import static org.junit.Assert.assertEquals;

public abstract class AbstractContextTest<T extends TestBean> {

    @Before
    public void setUp() {
        T.resetCount();
    }

    @After
    public void tearDown() {
        newContextUnderTest().tearDownAll();
    }

    @Test(expected = ContextNotActiveException.class)
    public void testNoActiveContextThrowException() {
        final T reference = BeanProvider.getContextualReference(getBeanType());
        reference.getState();
    }

    @Test
    public void testWithContextBeanCreatedOnce() {
        newContextUnderTest().activate();
        T referenceA = BeanProvider.getContextualReference(getBeanType());
        referenceA.setState("hello");
        assertEquals("hello", referenceA.getState());
        T referenceB = BeanProvider.getContextualReference(getBeanType());
        assertEquals("hello", referenceB.getState());
        assertEquals(1, T.getBeanCount());
    }

    @Test
    public void testNewContextBeanReCreated() {
        newContextUnderTest().activate();
        final T referenceA = BeanProvider.getContextualReference(getBeanType());
        referenceA.setState("hello");
        newContextUnderTest().activate();
        if (isNormalSoped()) {
            // proxy delegates to the active context automatically
            assertEquals("", referenceA.getState());
        } else {
            // pseudo scoped bean ignores active context after creation
            assertEquals("hello", referenceA.getState());
        }
        final T referenceB = BeanProvider.getContextualReference(getBeanType());
        assertEquals("", referenceB.getState());
        assertEquals(2, T.getBeanCount());
    }

    @Test
    public void testContextDestroy() {
        final UnderTestContext contextUnderTestA = newContextUnderTest();
        contextUnderTestA.activate();
        final T referenceA = BeanProvider.getContextualReference(getBeanType());
        referenceA.setState("hello");
        final UnderTestContext contextUnderTestB = newContextUnderTest();
        contextUnderTestB.activate();
        final T referenceB = BeanProvider.getContextualReference(getBeanType());
        referenceB.setState("hello");
        assertEquals(2, T.getBeanCount());
        contextUnderTestA.destroy();
        assertEquals(1, T.getBeanCount());
        contextUnderTestB.destroy();
        assertEquals(0, T.getBeanCount());
    }

    protected abstract UnderTestContext newContextUnderTest();

    protected abstract boolean isNormalSoped();

    protected abstract Class<T> getBeanType();

}
