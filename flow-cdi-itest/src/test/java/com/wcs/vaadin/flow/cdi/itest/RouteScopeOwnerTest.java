package com.wcs.vaadin.flow.cdi.itest;

import com.wcs.vaadin.flow.cdi.itest.deployment.*;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.fail;

@RunWith(Arquillian.class)
@RunAsClient
public class RouteScopeOwnerTest {

    @ArquillianResource
    Deployer deployer;

    @Deployment(name = "missingOwnerFromNonRouteComponent", testable = false, managed = false)
    public static WebArchive missingOwnerFromNonRouteComponent() {
        return ArchiveProvider
                .createWebArchive("missingOwnerFromNonRouteComponent",
                        MissingRouteScopeOwner.class);
    }

    @Test(expected = Exception.class)
    public void testOwnerForNonRouteComponentIsMandatory() {
        deployer.deploy("missingOwnerFromNonRouteComponent");
        fail("A non route component bean without owner should break deployment!");
    }

    @Deployment(name = "nonRouteComponentOwner", testable = false, managed = false)
    public static WebArchive nonRouteComponentOwner() {
        return ArchiveProvider
                .createWebArchive("nonRouteComponentOwner",
                        NonRouteComponent.class,
                        OwnedByANonRouteComponent.class);
    }

    @Test(expected = Exception.class)
    public void testOwnerMustBeRouteComponent() {
        deployer.deploy("nonRouteComponentOwner");
        fail("A bean with non route component owner should break deployment!");
    }

    @Deployment(name = "ownerNotAnAncestor", testable = false, managed = false)
    public static WebArchive ownerNotAnAncestor() {
        return ArchiveProvider
                .createWebArchive("ownerNotAnAncestor",
                        ChildView.class,
                        ViewLayout.class);
    }

    @Test(expected = Exception.class)
    public void testOwnerMustBeAnAncestorInRouteChain() {
        deployer.deploy("ownerNotAnAncestor");
        fail("A route component bean with a non ancestor route component " +
                "owner should break deployment!");
    }

    @Deployment(name = "missingRouteScoped", testable = false, managed = false)
    public static WebArchive missingRouteScoped() {
        return ArchiveProvider
                .createWebArchive("missingRouteScoped",
                        MissingRouteScoped.class,
                        RouteTarget.class);
    }

    @Test(expected = Exception.class)
    public void testRouteScopedBesideOwnerIsMandatory() {
        deployer.deploy("missingRouteScoped");
        fail("A non route scoped bean with @RouteScopeOwner " +
                "should break deployment!");
    }
}
