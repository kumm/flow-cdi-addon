package com.vaadin.flow.cdi.it;


import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.enterprise.inject.New;
import java.net.URL;
import java.util.function.Function;

@RunWith(Arquillian.class)
@RunAsClient
abstract public class AbstractCDIIntegrationTest {

    @Drone
    @New
    protected WebDriver firstWindow;

    @ArquillianResource
    protected URL contextPath;


    protected void open(String url) {
        firstWindow.get(contextPath + url);
        waitForClient();
    }

    protected WebElement find(String id) {
        return firstWindow.findElement(By.id(id));
    }

    protected void click(String id) {
        find(id).click();
        waitForClient();
    }

    protected void waitForClient() {
        new WebDriverWait(firstWindow, 10).until(new ClientIsReadyPredicate());
    }

    private class ClientIsReadyPredicate implements Function<WebDriver, Boolean> {
        @Override
        public Boolean apply(WebDriver input) {
            return (Boolean) ((JavascriptExecutor) firstWindow)
                    .executeScript("return " +
                            "typeof Vaadin === 'object' " +
                            "&& Vaadin !== null " +
                            "&& !Vaadin.Flow.clients[Object.keys(Vaadin.Flow.clients)[0]].isActive()");
        }
    }

}
