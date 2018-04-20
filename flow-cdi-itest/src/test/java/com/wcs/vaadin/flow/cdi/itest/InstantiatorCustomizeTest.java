package com.wcs.vaadin.flow.cdi.itest;

import com.wcs.vaadin.flow.cdi.itest.service.InstantiatorAlternative;
import com.wcs.vaadin.flow.cdi.itest.service.InstantiatorDecorator;
import com.wcs.vaadin.flow.cdi.itest.service.InstantiatorCustomizedView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;

import static com.wcs.vaadin.flow.cdi.itest.service.InstantiatorCustomizedView.CUSTOMIZED;
import static com.wcs.vaadin.flow.cdi.itest.service.InstantiatorCustomizedView.VIEW;
import static org.junit.Assert.assertEquals;

public class InstantiatorCustomizeTest extends AbstractCDIIntegrationTest {

    @Deployment(name = "decorator")
    public static WebArchive decoratorDeployment() {
        return ArchiveProvider.createWebArchive("instantiator-decorator-test",
                InstantiatorDecorator.class,
                InstantiatorCustomizedView.class);
    }

    @Deployment(name = "alternative")
    public static WebArchive alternativeDeployment() {
        return ArchiveProvider.createWebArchive("instantiator-alternative-test",
                InstantiatorAlternative.class,
                InstantiatorCustomizedView.class);
    }

    @Before
    public void setUp() {
        open("");
    }

    @Test
    @OperateOnDeployment("decorator")
    public void testInstantiatorDecorated() {
        assertInstantiatorCustomized();
    }

    @Test
    @OperateOnDeployment("alternative")
    public void testInstantiatorAlternative() {
        assertInstantiatorCustomized();
    }

    private void assertInstantiatorCustomized() {
        assertEquals(CUSTOMIZED, find(VIEW).getText());
    }

}
