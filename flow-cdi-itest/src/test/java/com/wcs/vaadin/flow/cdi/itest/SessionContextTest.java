package com.wcs.vaadin.flow.cdi.itest;

import com.wcs.vaadin.flow.cdi.itest.contexts.SessionContextView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SessionContextTest extends AbstractCDIIntegrationTest {
    @Deployment(testable = false)
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
    public void testUIsAccessSameSession() {
        assertEquals("", find(SessionContextView.VALUELABEL_ID).getText());
        click(SessionContextView.SETVALUEBTN_ID);
        refresh();//creates new UI
        assertEquals(SessionContextView.VALUE, find(SessionContextView.VALUELABEL_ID).getText());
    }

    @Test
    public void testVaadinSessionCloseDestroysSessionContext() throws Exception {
        assertEquals(0, getCount(SessionContextView.SessionScopedBean.DESTROY_COUNT));
        click(SessionContextView.INVALIDATEBTN_ID);
        assertEquals(1, getCount(SessionContextView.SessionScopedBean.DESTROY_COUNT));
    }

    @Test
    public void testHttpSessionCloseDestroysSessionContext() throws Exception {
        assertEquals(0, getCount(SessionContextView.SessionScopedBean.DESTROY_COUNT));
        click(SessionContextView.HTTP_INVALIDATEBTN_ID);
        assertEquals(1, getCount(SessionContextView.SessionScopedBean.DESTROY_COUNT));
    }

    @Test
    @Ignore
    //ignored because it's slow, and expiration should be same as session close
    public void testHttpSessionExpirationDestroysSessionContext() throws Exception {
        assertEquals(0, getCount(SessionContextView.SessionScopedBean.DESTROY_COUNT));
        click(SessionContextView.EXPIREBTN_ID);
        boolean destroyed = false;
        for (int i=0; i<60; i++) {
            Thread.sleep(1000);
            if (getCount(SessionContextView.SessionScopedBean.DESTROY_COUNT) > 0) {
                System.out.printf("session expired after %d seconds\n", i);
                destroyed = true;
                break;
            }
        }
        assertTrue(destroyed);
    }
}
