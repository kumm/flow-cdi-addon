package com.wcs.vaadin.flow.cdi.contexts;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.enterprise.context.ContextNotActiveException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public abstract class AbstractContextTest<T extends TestBean> {

    private List<UnderTestContext> contexts;

    @Before
    public void setUp() {
        T.resetCount();
        contexts = new ArrayList<>();
    }

    @After
    public void tearDown() {
        newContextUnderTest().tearDownAll();
        contexts = null;
    }

    @Test(expected = ContextNotActiveException.class)
    public void testNoActiveContextThrowException() {
        final T reference = BeanProvider.getContextualReference(getBeanType());
        reference.getState();
    }

    @Test
    public void testWithContextBeanCreatedOnce() {
        createContext().activate();
        T referenceA = BeanProvider.getContextualReference(getBeanType());
        referenceA.setState("hello");
        assertEquals("hello", referenceA.getState());
        T referenceB = BeanProvider.getContextualReference(getBeanType());
        assertEquals("hello", referenceB.getState());
        assertEquals(1, T.getBeanCount());
    }

    @Test
    public void testNewContextBeanReCreated() {
        createContext().activate();
        final T referenceA = BeanProvider.getContextualReference(getBeanType());
        referenceA.setState("hello");
        createContext().activate();
        if (isNormalScoped()) {
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
        final UnderTestContext contextUnderTestA = createContext();
        contextUnderTestA.activate();
        final T referenceA = BeanProvider.getContextualReference(getBeanType());
        referenceA.setState("hello");
        final UnderTestContext contextUnderTestB = createContext();
        contextUnderTestB.activate();
        final T referenceB = BeanProvider.getContextualReference(getBeanType());
        referenceB.setState("hello");
        assertEquals(2, T.getBeanCount());
        contextUnderTestA.destroy();
        assertEquals(1, T.getBeanCount());
        contextUnderTestB.destroy();
        assertEquals(0, T.getBeanCount());
    }

    protected UnderTestContext createContext() {
        UnderTestContext underTestContext = newContextUnderTest();
/*
        UnderTestContext implementations set fields
        to Vaadin CurrentInstance.
        Need to hold a hard reference to prevent possible GC,
        because CurrentInstance works with weak reference.
*/
        contexts.add(underTestContext);
        return underTestContext;
    }

    protected abstract UnderTestContext newContextUnderTest();

    protected abstract boolean isNormalScoped();

    protected abstract Class<T> getBeanType();

}
