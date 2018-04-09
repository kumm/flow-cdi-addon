package com.wcs.vaadin.flow.cdi.itest.service;

import com.wcs.vaadin.flow.cdi.itest.AbstractCDIIntegrationTest;
import com.wcs.vaadin.flow.cdi.itest.ArchiveProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans11.BeansDescriptor;
import org.junit.Before;
import org.junit.Test;

import static com.wcs.vaadin.flow.cdi.itest.service.InstantiatorDecoratorView.DECORATED;
import static com.wcs.vaadin.flow.cdi.itest.service.InstantiatorDecoratorView.VIEW;
import static org.junit.Assert.assertEquals;

public class InstantiatorDecoratorTest extends AbstractCDIIntegrationTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ArchiveProvider.createWebArchive("instantiator-decorator-test", webArchive -> {
            webArchive.addClasses(
                    InstantiatorDecorator.class,
                    InstantiatorDecoratorView.class);
            webArchive.delete("WEB-INF/beans.xml");

            BeansDescriptor beans = Descriptors.create(BeansDescriptor.class)
                    .createDecorators()
                    .clazz(InstantiatorDecorator.class.getName())
                    .up();

            webArchive.addAsWebInfResource(new StringAsset(beans.exportAsString()),
                    ArchivePaths.create("beans.xml"));
        });
    }

    @Before
    public void setUp() {
        open("");
    }

    @Test
    public void testInstantiatorDecorated() {
        assertEquals(DECORATED, find(VIEW).getText());
    }

}
