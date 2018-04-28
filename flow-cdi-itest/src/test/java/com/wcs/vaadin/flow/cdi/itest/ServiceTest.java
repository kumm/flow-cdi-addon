package com.wcs.vaadin.flow.cdi.itest;

import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.UIInitEvent;
import com.wcs.vaadin.flow.cdi.itest.service.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;

public class ServiceTest extends AbstractCDIIntegrationTest {

    @Deployment
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("services",
                SystemMessagesProviderView.class,
                TestSystemMessagesProvider.class,
                ErrorHandlerView.class,
                TestErrorHandler.class,
                EventObserver.class);
    }

    @Before
    public void setUp() throws Exception {
        resetCounts();
    }

    @Test
    public void testSessionExpiredMessageCustomized() {
        open("system-messages");
        click(SystemMessagesProviderView.EXPIRE);
        click(SystemMessagesProviderView.ACTION);
        assertSystemMessageEquals(TestSystemMessagesProvider.EXPIRED_BY_TEST);
    }

    @Test
    public void testSessionEventObserver() throws IOException {
        String initCounter = SessionInitEvent.class.getSimpleName();
        String destroyCounter = SessionDestroyEvent.class.getSimpleName();
        Assert.assertEquals(0, getCount(initCounter));
        Assert.assertEquals(0, getCount(destroyCounter));

        firstWindow.manage().deleteAllCookies();
        open("system-messages");
        Assert.assertEquals(1, getCount(initCounter));
        Assert.assertEquals(0, getCount(destroyCounter));

        click(SystemMessagesProviderView.EXPIRE);
        Assert.assertEquals(1, getCount(initCounter));
        Assert.assertEquals(1, getCount(destroyCounter));
    }

    @Test
    public void testUIInitEventObserved() throws IOException {
        String uiInitCounter = UIInitEvent.class.getSimpleName();
        Assert.assertEquals(0, getCount(uiInitCounter));
        open("system-messages");
        Assert.assertEquals(1, getCount(uiInitCounter));
    }

    @Test
    public void testInternalErrorMessageCustomized() throws IOException {
        open("error-handler");
        click(ErrorHandlerView.FAIL);
        assertSystemMessageEquals(TestSystemMessagesProvider.FAILED_BY_TEST);
    }

    @Test
    public void testErrorHandlerCustomized() throws IOException {
        open("error-handler");
        click(ErrorHandlerView.FAIL);
        Assert.assertEquals(1,
                getCount(TestErrorHandler.class.getSimpleName()));
    }

    private void assertSystemMessageEquals(String expected) {
        WebElement message = firstWindow.findElement(
                By.cssSelector("div.v-system-error div.message"));
        Assert.assertEquals(expected, message.getText());
    }
}
