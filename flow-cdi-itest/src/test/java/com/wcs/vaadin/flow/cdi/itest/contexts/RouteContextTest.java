package com.wcs.vaadin.flow.cdi.itest.contexts;

import com.wcs.vaadin.flow.cdi.itest.AbstractCDIIntegrationTest;
import com.wcs.vaadin.flow.cdi.itest.ArchiveProvider;
import com.wcs.vaadin.flow.cdi.itest.contexts.route.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class RouteContextTest extends AbstractCDIIntegrationTest {
    private String uiId;

    @Deployment
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("route-context", webArchive ->
                webArchive.addPackage(MasterView.class.getPackage()));
    }

    @Before
    public void setUp() throws Exception {
        resetCounts();
        open("");
        uiId = find(RootView.UIID).getText();
        assertConstructed(RootView.class,1);
        assertDestroyed(RootView.class,0);
        assertConstructed(RerouteView.class,0);
        assertConstructed(MasterView.class,0);
        assertConstructed(AssignedBean.class,0);
        assertConstructed(ApartBean.class,0);
        assertConstructed(DetailApartView.class,0);
        assertConstructed(DetailAssignedView.class,0);
    }

    @Test
    public void testOpenMaster() throws IOException {
        follow(RootView.MASTER);
        assertEquals("", find(MasterView.ASSIGNED_BEAN_LABEL).getText());
        assertEquals("", find(MasterView.APART_BEAN_LABEL).getText());

        assertConstructed(RootView.class,1);
        assertDestroyed(RootView.class,1);
        assertConstructed(MasterView.class,1);
        assertDestroyed(MasterView.class,0);
        assertConstructed(DetailApartView.class,0);
        assertConstructed(DetailAssignedView.class,0);
    }

    @Test
    public void testNavigationFromAssignedToMaserHoldsGroup() throws IOException {
        follow(RootView.MASTER);
        follow(MasterView.ASSIGNED);
        assertEquals("ASSIGNED", find(DetailAssignedView.BEAN_LABEL).getText());
        assertEquals("", find(MasterView.APART_BEAN_LABEL).getText());

        follow(DetailAssignedView.MASTER);
        assertConstructed(MasterView.class,1);
        assertDestroyed(MasterView.class,0);
        assertConstructed(DetailAssignedView.class,1);
        assertDestroyed(DetailAssignedView.class,0);
        assertConstructed(DetailApartView.class,0);

        assertEquals("ASSIGNED", find(MasterView.ASSIGNED_BEAN_LABEL).getText());
        assertEquals("", find(MasterView.APART_BEAN_LABEL).getText());
    }

    @Test
    public void testNavigationFromApartToMaserReleaseGroup() throws IOException {
        follow(RootView.MASTER);
        follow(MasterView.APART);
        assertEquals("", find(MasterView.ASSIGNED_BEAN_LABEL).getText());
        assertEquals("APART", find(DetailApartView.BEAN_LABEL).getText());

        follow(DetailApartView.MASTER);
        assertConstructed(MasterView.class,1);
        assertDestroyed(MasterView.class,0);
        assertConstructed(DetailAssignedView.class,0);
        assertDestroyed(DetailAssignedView.class,0);
        assertConstructed(DetailApartView.class,1);
        assertDestroyed(DetailApartView.class,1);

        assertEquals("", find(MasterView.ASSIGNED_BEAN_LABEL).getText());
        assertEquals("", find(MasterView.APART_BEAN_LABEL).getText());
    }

    @Test
    public void testRerouteReleasesSource() throws IOException {
        follow(RootView.REROUTE);
        assertConstructed(RerouteView.class,1);
        assertDestroyed(RerouteView.class,1);

        assertRootViewIsDisplayed();
    }

    @Test
    public void testPostponedNavigationDoesNotCreateTarget() throws IOException {
        follow(RootView.POSTPONE);
        assertConstructed(RootView.class,1);

        follow(PostponeView.POSTPONED_ROOT);
        assertConstructed(RootView.class,1);
        assertDestroyed(RootView.class,1);

        click(PostponeView.NAVIGATE);
        assertConstructed(RootView.class,2);
        assertDestroyed(RootView.class,1);
        assertRootViewIsDisplayed();
    }

    private void assertRootViewIsDisplayed() {
        assertEquals(uiId, find(RootView.UIID).getText());
    }

    private void assertConstructed(Class beanClass, int count) throws IOException {
        assertEquals(count, getCount(beanClass.getSimpleName() + "C" + uiId));
    }
    private void assertDestroyed(Class beanClass, int count) throws IOException {
        assertEquals(count, getCount(beanClass.getSimpleName() + "D" + uiId));
    }
}
