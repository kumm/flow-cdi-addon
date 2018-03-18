package com.vaadin.flow.cdi.itest;


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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    protected void refresh() {
        firstWindow.navigate().refresh();
        waitForClient();
    }

    protected WebElement find(String id) {
        return firstWindow.findElement(By.id(id));
    }

    protected WebElement findLink(String linkText) {
        return firstWindow.findElement(By.linkText(linkText));
    }

    protected void click(String id) {
        find(id).click();
        waitForClient();
    }

    protected void follow(String linkText) {
        findLink(linkText).click();
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

    public void resetCounts() throws IOException {
        slurp("?resetCounts");
    }

    public int getCount(String id) throws IOException {
        String line = slurp("?getCount=" + id);
        return Integer.parseInt(line);
    }

    private String slurp(String uri) throws IOException {
        URL url = new URL(contextPath.toString()+uri);
        InputStream is = url.openConnection().getInputStream();
        BufferedReader reader = new BufferedReader( new InputStreamReader( is )  );
        String line = reader.readLine();
        reader.close();
        return line;
    }

}
