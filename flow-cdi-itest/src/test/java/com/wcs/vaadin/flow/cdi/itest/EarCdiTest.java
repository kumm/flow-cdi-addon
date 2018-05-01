package com.wcs.vaadin.flow.cdi.itest;


import com.wcs.vaadin.flow.cdi.itest.contexts.route.MasterView;
import com.wcs.vaadin.flow.cdi.itest.contexts.route.RootView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.application7.ApplicationDescriptor;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

public class EarCdiTest extends AbstractCDIIntegrationTest {

    public static WebArchive getWarPackageCdi(String warName) {
        return ArchiveProvider.createWebArchive(warName, webArchive ->
                webArchive.addPackage(MasterView.class.getPackage()));
    }

    @Deployment(testable = false)
    public static EnterpriseArchive deploymentCdi() {
        WebArchive war1 = getWarPackageCdi("war1");
        WebArchive war2 = getWarPackageCdi("war2");
        ApplicationDescriptor app = Descriptors.create(ApplicationDescriptor.class);
        app.version("7");
        app.createModule().getOrCreateWeb().webUri("war1.war").contextRoot("war1");
        app.createModule().getOrCreateWeb().webUri("war2.war").contextRoot("war2");
        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class)
                .setApplicationXML(new StringAsset(app.exportAsString()))
                .addAsModule(war1)
                .addAsModule(war2);
        return ear;
    }

    @Test
    public void testWar1Up() {
        open("/war1/");
        assertNotEquals("", find(RootView.UIID).getText());
    }

    @Test
    public void testWar2Up() {
        open("/war2/");
        assertNotEquals("", find(RootView.UIID).getText());
    }

}
