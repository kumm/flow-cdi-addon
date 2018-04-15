package com.wcs.vaadin.flow.cdi.itest.push;

import com.wcs.vaadin.flow.cdi.UIScoped;
import com.wcs.vaadin.flow.cdi.VaadinServiceScoped;
import com.wcs.vaadin.flow.cdi.VaadinSessionScoped;
import com.wcs.vaadin.flow.cdi.itest.AbstractCDIIntegrationTest;
import com.wcs.vaadin.flow.cdi.itest.ArchiveProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import java.lang.annotation.Annotation;

/**
 * Tried push tests in a single test, but this way looks reliable.
 */
public abstract class AbstractPushTest extends AbstractCDIIntegrationTest {

    @Deployment
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("push", webArchive
                -> webArchive.addPackage(PushComponent.class.getPackage()));
    }

    protected void assertAllExceptRequestAndSessionActive() {
        assertContextActive(RequestScoped.class, false);
        assertContextActive(SessionScoped.class, false);
        assertContextActive(ApplicationScoped.class, true);
        assertVaadinContextsActive();
    }

    protected void assertVaadinContextsActive() {
        assertContextActive(UIScoped.class, true);
        assertContextActive(VaadinSessionScoped.class, true);
        assertContextActive(VaadinServiceScoped.class, true);
    }

    protected void assertContextActive(Class<? extends Annotation> scope,
                                       boolean active) {
        Assert.assertEquals(active + "", find(scope.getName()).getText());
    }

    protected void waitForPush() {
        new WebDriverWait(firstWindow, 10).until(webDriver
                -> firstWindow.findElements(
                By.id(RequestScoped.class.getName())).size() == 0);
    }
}
