package com.wcs.vaadin.flow.cdi.itest;

import com.wcs.vaadin.flow.cdi.itest.service.InstantiatorDecorator;
import com.wcs.vaadin.flow.cdi.itest.service.InstantiatorDecoratorView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.wcs.vaadin.flow.cdi.itest.service.InstantiatorDecoratorView.DECORATED;
import static com.wcs.vaadin.flow.cdi.itest.service.InstantiatorDecoratorView.VIEW;
import static org.junit.Assert.assertEquals;

public class InstantiatorDecoratorTest extends AbstractCDIIntegrationTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ArchiveProvider.createWebArchive("instantiator-decorator-test",
                InstantiatorDecorator.class,
                InstantiatorDecoratorView.class);
    }

    @Before
    public void setUp() {
        open("");
    }

    @Test
    @Ignore("it needs further investigation")
    // Fails on payara 4, 5 and wildfly 11, 12.
    // Succeeds on wildfly 10, and TomEE 7 ...
    public void testInstantiatorDecorated() {
        assertEquals(DECORATED, find(VIEW).getText());
    }

}
