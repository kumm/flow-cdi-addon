package com.wcs.vaadin.flow.cdi.itest;

import com.wcs.vaadin.flow.cdi.itest.service.ErrorHandlerView;
import com.wcs.vaadin.flow.cdi.itest.service.SystemMessagesProviderView;
import com.wcs.vaadin.flow.cdi.itest.service.TestErrorHandler;
import com.wcs.vaadin.flow.cdi.itest.service.TestSystemMessagesProvider;
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
                TestErrorHandler.class);
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
