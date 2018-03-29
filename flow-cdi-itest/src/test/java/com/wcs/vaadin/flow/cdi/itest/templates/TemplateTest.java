package com.wcs.vaadin.flow.cdi.itest.templates;

import com.wcs.vaadin.flow.cdi.itest.AbstractCDIIntegrationTest;
import com.wcs.vaadin.flow.cdi.itest.ArchiveProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

public class TemplateTest extends AbstractCDIIntegrationTest {

    @Deployment
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("templates",
                archive -> archive
                        .addClasses(TestTemplate.class)
                        .addAsWebResource(
                                "frontend/test-template.html", "frontend/test-template.html")
        );
    }

    @Before
    public void setUp() {
        open("");
    }

    @Test
    public void testScopedComponentInjectedToTemplate() {
        final WebElement label = findInShadow("test-template", "label");
        assertEquals("", label.getText());
        findInShadow("test-template", "input").sendKeys("CDI\t");
        waitForClient();
        assertEquals("CDI", label.getText());
    }
}
