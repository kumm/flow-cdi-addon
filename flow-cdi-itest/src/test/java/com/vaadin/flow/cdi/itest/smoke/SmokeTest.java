package com.vaadin.flow.cdi.itest.smoke;

import com.vaadin.flow.cdi.itest.AbstractCDIIntegrationTest;
import com.vaadin.flow.cdi.itest.ArchiveProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class SmokeTest extends AbstractCDIIntegrationTest {

    @Deployment(name = "plain")
    public static WebArchive createPlainDeployment() {
        return ArchiveProvider.createPlainWebArchive("plain-test", PlainView.class);
    }

    @Deployment(name = "cdi")
    public static WebArchive createCDIDeployment() {
        return ArchiveProvider.createWebArchive("cdi-test", CdiView.class);
    }

    @Before
    public void setUp() throws Exception {
        open("");
        click("CLICK_ME");
    }

    @Test
    @OperateOnDeployment("plain")
    public void testInfrastructureWithPlainVaadin() {
        assertThat(find("HELLO").getText(), equalTo("hello"));
    }

    @Test
    @OperateOnDeployment("cdi")
    public void testCDIInstantiator() {
        assertThat(find("HELLO").getText(), equalTo("hello CDI"));
    }

}
