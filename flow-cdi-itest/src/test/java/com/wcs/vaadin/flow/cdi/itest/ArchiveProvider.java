package com.wcs.vaadin.flow.cdi.itest;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

import java.util.function.Consumer;

public class ArchiveProvider {

    public static WebArchive createWebArchive(String warName, Consumer<WebArchive> customizer) {
        WebArchive archive = base(warName);
        customizer.accept(archive);
        System.out.println(archive.toString(true));
        return archive;
    }

    public static WebArchive createWebArchive(String warName, Class... classes) {
        return createWebArchive(warName, archive -> archive.addClasses(classes));
    }

    private static WebArchive base(String warName) {
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
                .addAsLibraries(
                        pom.resolve("org.webjars.bowergithub.polymer:polymer")
                                .withTransitivity().asFile())
                .addAsLibraries(
                        pom.resolve("com.vaadin:flow-cdi-addon")
                                .withTransitivity().asFile())
                .addAsWebInfResource(EmptyAsset.INSTANCE,
                        ArchivePaths.create("beans.xml"))
                .addClasses(Counter.class, CounterFilter.class);
    }

}
