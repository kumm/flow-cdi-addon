package com.vaadin.flow.cdi.itest;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

public class ArchiveProvider {

    public static WebArchive createWebArchive(String warName, Class... classes) {
        return createWebArchive(warName, true, classes);
    }

    public static WebArchive createWebArchive(String warName,
                                              boolean emptyBeansXml, Class... classes) {
        WebArchive archive = base(warName, emptyBeansXml);
        archive.addClasses(classes);
        System.out.println(archive.toString(true));
        return archive;
    }

    private static WebArchive base(String warName, boolean emptyBeansXml) {
        PomEquippedResolveStage pom = Maven.resolver()
                .loadPomFromFile("pom.xml");
        WebArchive archive = ShrinkWrap
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
                        pom.resolve("com.vaadin:flow-cdi-addon")
                                .withTransitivity().asFile());
        if (emptyBeansXml) {
            archive = archive.addAsWebInfResource(EmptyAsset.INSTANCE,
                    ArchivePaths.create("beans.xml"));
        }
        return archive;
    }

}
