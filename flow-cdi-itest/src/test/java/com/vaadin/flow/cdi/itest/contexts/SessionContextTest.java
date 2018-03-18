package com.vaadin.flow.cdi.itest.contexts;

import com.vaadin.flow.cdi.itest.AbstractCDIIntegrationTest;
import com.vaadin.flow.cdi.itest.ArchiveProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.vaadin.flow.cdi.itest.contexts.SessionContextView.*;
import static com.vaadin.flow.cdi.itest.contexts.SessionContextView.SessionScopedBean.DESTROY_COUNT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SessionContextTest extends AbstractCDIIntegrationTest {
    @Deployment
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("session-context",
                SessionContextView.class);
    }

    @Before
    public void setUp() throws Exception {
        resetCounts();
        open("");
    }

    @Test
    public void testUIsAccessSameSession() throws Exception {
        assertEquals("", find(VALUELABEL_ID).getText());
        click(SessionContextView.SETVALUEBTN_ID);
        refresh();//creates new UI
        assertEquals(SessionContextView.VALUE, find(VALUELABEL_ID).getText());
    }

    @Test
    public void testVaadinSessionCloseDestroysSessionContext() throws Exception {
        assertEquals(0, getCount(DESTROY_COUNT));
        click(INVALIDATEBTN_ID);
        assertEquals(1, getCount(DESTROY_COUNT));
    }

    @Test
    public void testHttpSessionCloseDestroysSessionContext() throws Exception {
        assertEquals(0, getCount(DESTROY_COUNT));
        click(HTTP_INVALIDATEBTN_ID);
        assertEquals(1, getCount(DESTROY_COUNT));
    }

    @Test
    @Ignore
    //ignored because it's slow, and expiration should be same as session close
    public void testHttpSessionExpirationDestroysSessionContext() throws Exception {
        assertEquals(0, getCount(DESTROY_COUNT));
        click(EXPIREBTN_ID);
        boolean destroyed = false;
        for (int i=0; i<60; i++) {
            Thread.sleep(1000);
            if (getCount(DESTROY_COUNT) > 0) {
                System.out.printf("session expired after %d seconds\n", i);
                destroyed = true;
                break;
            }
        }
        assertTrue(destroyed);
    }
}
