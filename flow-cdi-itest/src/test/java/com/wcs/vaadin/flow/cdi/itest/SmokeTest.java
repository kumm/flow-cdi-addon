package com.wcs.vaadin.flow.cdi.itest;

import com.vaadin.flow.router.NavigationTrigger;
import com.wcs.vaadin.flow.cdi.itest.smoke.CdiUITestView;
import com.wcs.vaadin.flow.cdi.itest.smoke.CdiView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class SmokeTest extends AbstractCDIIntegrationTest {

    @Deployment(name = "noncdi")
    public static WebArchive createCdiServletDisabledDeployment() {
        return ArchiveProvider.createWebArchive("noncdi-test",
                CdiView.class, CdiUITestView.class)
                .addAsWebInfResource(ArchiveProvider.class.getClassLoader()
                        .getResource("disablecdi-web.xml"), "web.xml");
    }

    @Deployment(name = "cdi")
    public static WebArchive createCdiServletEnabledDeployment() {
        return ArchiveProvider.createWebArchive("cdi-test",
                CdiView.class, CdiUITestView.class);
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

    @Test
    @OperateOnDeployment("cdi")
    public void testCdiUINavigationEvents() {
        follow(CdiView.UI_TEST_VIEW);
        click(CdiUITestView.SHOW_NAVIGATION_EVENTS);
        assertThat(find(CdiUITestView.AFTER_NAVIGATION_PATH).getText(),
                equalTo("uitest"));
        assertThat(find(CdiUITestView.BEFORE_ENTER_TRIGGER).getText(),
                equalTo(NavigationTrigger.ROUTER_LINK.name()));
        assertThat(find(CdiUITestView.BEFORE_LEAVE_POSTPONED).getText(),
                equalTo("false"));
    }

    @Test
    @OperateOnDeployment("cdi")
    public void testCdiUIPollEvent() throws InterruptedException {
        follow(CdiView.UI_TEST_VIEW);
        new WebDriverWait(firstWindow, 10).until(webDriver -> {
            click(CdiUITestView.SHOW_POLL_EVENT);
            return firstWindow.findElements(
                    By.id(CdiUITestView.POLL_IS_FROM_CLIENT)).size() != 0;
        });
        assertThat(find(CdiUITestView.POLL_IS_FROM_CLIENT).getText(),
                equalTo("true"));
    }

}
