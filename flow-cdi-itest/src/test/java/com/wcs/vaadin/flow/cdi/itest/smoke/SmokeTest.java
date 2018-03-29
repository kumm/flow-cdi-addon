package com.wcs.vaadin.flow.cdi.itest.smoke;

import com.wcs.vaadin.flow.cdi.itest.AbstractCDIIntegrationTest;
import com.wcs.vaadin.flow.cdi.itest.ArchiveProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class SmokeTest extends AbstractCDIIntegrationTest {

    @Deployment(name = "noncdi")
    public static WebArchive createCdiServletDisabledDeployment() {
        return ArchiveProvider.createWebArchive("noncdi-test", CdiView.class)
                .addAsWebInfResource(ArchiveProvider.class.getClassLoader()
                        .getResource("disablecdi-web.xml"), "web.xml");
    }

    @Deployment(name = "cdi")
    public static WebArchive createCdiServletEnabledDeployment() {
        return ArchiveProvider.createWebArchive("cdi-test", CdiView.class);
    }

    @Before
    public void setUp() throws Exception {
        open("");
        click("CLICK_ME");
    }

    @Test
    @OperateOnDeployment("noncdi")
    public void testCdiDisabled() {
        assertThat(find("HELLO").getText(), equalTo("no CDI"));
    }

    @Test
    @OperateOnDeployment("cdi")
    public void testCdiEnabled() {
        assertThat(find("HELLO").getText(), equalTo("hello CDI"));
    }

}
