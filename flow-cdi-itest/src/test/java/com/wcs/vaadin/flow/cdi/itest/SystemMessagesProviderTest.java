package com.wcs.vaadin.flow.cdi.itest;

import com.wcs.vaadin.flow.cdi.itest.service.SystemMessagesProviderView;
import com.wcs.vaadin.flow.cdi.itest.service.TestSystemMessagesProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SystemMessagesProviderTest extends AbstractCDIIntegrationTest {

    @Deployment
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("system-messages-provider",
                SystemMessagesProviderView.class,
                TestSystemMessagesProvider.class);
    }

    @Test
    public void testSessionExpiredMessageCustomized() {
        open("");
        click(SystemMessagesProviderView.EXPIRE);
        click(SystemMessagesProviderView.ACTION);

        WebElement message = firstWindow.findElement(
                By.cssSelector("div.v-system-error div.message"));
        Assert.assertEquals(TestSystemMessagesProvider.EXPIRED_BY_TEST,
                message.getText());
    }
}
