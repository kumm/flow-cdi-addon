package com.wcs.vaadin.flow.cdi.itest;


import com.wcs.vaadin.flow.cdi.itest.smoke.CdiUITestView;
import com.wcs.vaadin.flow.cdi.itest.smoke.CdiView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.application7.ApplicationDescriptor;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class EarPlainTest extends AbstractCDIIntegrationTest {

    public static WebArchive getWarPackagePlain(String warName) {
        PomEquippedResolveStage pom = Maven.resolver()
                .loadPomFromFile("pom.xml");
        return ShrinkWrap
                .create(WebArchive.class, warName + ".war")
                .addAsLibraries(
                        pom.resolve("com.vaadin:flow-server")
                                .withTransitivity().asFile())
                .addAsLibraries(
                        pom.resolve("com.vaadin:flow-client")
                                .withTransitivity().asFile())
                .addAsLibraries(
                        pom.resolve("com.vaadin:flow-html-components")
                                .withTransitivity().asFile())
                .addClasses(CdiView.class, CdiUITestView.class);
    }

    @Deployment(testable = false)
    public static EnterpriseArchive deploymentPlain() {
        WebArchive war1 = getWarPackagePlain("war1");
        WebArchive war2 = getWarPackagePlain("war2");
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
        click("CLICK_ME");
        assertThat(find("HELLO").getText(), equalTo("no CDI"));
    }

    @Test
    public void testWar2Up() {
        open("/war2/");
        click("CLICK_ME");
        assertThat(find("HELLO").getText(), equalTo("no CDI"));
    }
}
