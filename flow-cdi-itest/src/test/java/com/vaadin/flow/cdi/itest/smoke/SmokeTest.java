package com.vaadin.flow.cdi.itest.smoke;

import com.vaadin.flow.cdi.itest.AbstractCDIIntegrationTest;
import com.vaadin.flow.cdi.itest.ArchiveProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;

public class SmokeTest extends AbstractCDIIntegrationTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ArchiveProvider.createWebArchive("smoke-test", PlainView.class);
    }

    @Test
    public void testInfrastructureWithPlainVaadin() {
        open("");
        click("CLICK_ME");
        Assert.assertEquals("hello", find("HELLO").getText());
    }

}
