package com.wcs.vaadin.flow.cdi.itest;

import com.wcs.vaadin.flow.cdi.UIScoped;
import com.wcs.vaadin.flow.cdi.VaadinServiceScoped;
import com.wcs.vaadin.flow.cdi.VaadinSessionScoped;
import com.wcs.vaadin.flow.cdi.itest.push.PushComponent;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import java.lang.annotation.Annotation;

public class PushTest extends AbstractCDIIntegrationTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("push", webArchive
                -> webArchive.addPackage(PushComponent.class.getPackage()));
    }

    @Test
    @InSequence(1)
    public void testWsWithXhrBackgroundRequestAndSessionDoesNotActive() {
        open("websocket-xhr");
        click(PushComponent.RUN_BACKGROUND);
        waitForPush();
        assertAllExceptRequestAndSessionActive();
    }

    @Test
    @InSequence(2)
    public void testWsWithXhrForegroundAllContextsActive() {
        open("websocket-xhr");
        click(PushComponent.RUN_FOREGROUND);
        assertContextActive(RequestScoped.class, true);
        assertContextActive(SessionScoped.class, true);
        assertContextActive(ApplicationScoped.class, true);
        assertVaadinContextsActive();
    }

    @Test
    @InSequence(3)
    public void testWsNoXhrBackgroundRequestAndSessionDoesNotActive() {
        open("websocket");
        click(PushComponent.RUN_BACKGROUND);
        waitForPush();
        assertAllExceptRequestAndSessionActive();
    }

    @Test
    @InSequence(4)
    public void testWsNoXhrForegroundRequestAndSessionDoesNotActive() {
        open("websocket");
        click(PushComponent.RUN_FOREGROUND);
        assertAllExceptRequestAndSessionActive();
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
                By.id(RequestScoped.class.getName())).size() != 0);
    }
}
