package com.vaadin.flow.cdi.itest.contexts;

import com.vaadin.flow.cdi.itest.AbstractCDIIntegrationTest;
import com.vaadin.flow.cdi.itest.ArchiveProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class UIContextTest extends AbstractCDIIntegrationTest {

    private String uiId;

    @Deployment
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("ui-context",
                UIContextRootView.class,
                UIScopedView.class,
                UIScopeInjecterView.class,
                UINormalScopedBeanView.class,
                UIScopedLabel.class);
    }

    @Before
    public void setUp() throws Exception {
        resetCounts();
        open("");
        uiId = find(UIContextRootView.UIID_LABEL).getText();
    }

    @Test
    public void testDestroyOnUIClose() throws IOException {
        click(UIContextRootView.CLOSE_UI_BTN);
        assertEquals(1, getCount(UIScopedLabel.DESTROY_COUNT + uiId));
    }

    @Test
    public void testDestroyOnSessionClose() throws IOException {
        click(UIContextRootView.CLOSE_SESSION_BTN);
        assertEquals(1, getCount(UIScopedLabel.DESTROY_COUNT + uiId));
    }

    @Test
    public void testUIScopedViewSurviveNavigation() {
        follow(UIContextRootView.UISCOPED_LINK);
        assertEquals("", find(UIScopedView.VIEWSTATE_LABEL).getText());
        click(UIScopedView.SETSTATE_BTN);
        assertEquals(uiId, find(UIScopedView.VIEWSTATE_LABEL).getText());
        follow(UIScopedView.ROOT_LINK);
        follow(UIContextRootView.UISCOPED_LINK);
        assertEquals(uiId, find(UIScopedView.VIEWSTATE_LABEL).getText());
    }

    @Test
    public void testInjectComponent() {
        assertEquals(uiId, find(UIScopedLabel.ID).getText());
        follow(UIContextRootView.INJECTER_LINK);
        assertEquals(uiId, find(UIScopedLabel.ID).getText());
    }

    @Test
    public void testObserver() {
        click(UIContextRootView.TRIGGER_EVENT_BTN);
        assertEquals(uiId, find(UIScopedLabel.ID).getText());
    }

    @Test
    public void testNormalScopedBean() {
        follow(UIContextRootView.NORMALSCOPED_LINK);
        assertEquals(uiId, find(UINormalScopedBeanView.UIID_LABEL).getText());
        open("");
        uiId = find(UIContextRootView.UIID_LABEL).getText();
        follow(UIContextRootView.NORMALSCOPED_LINK);
        assertEquals(uiId, find(UINormalScopedBeanView.UIID_LABEL).getText());
    }
}
