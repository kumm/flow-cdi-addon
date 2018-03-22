package com.vaadin.flow.cdi.contexts;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

public class UIUnderTestContext implements UnderTestContext {

    private VaadinSession session;
    private UI ui;
    private static int uiIdNdx = 0;
    private static SessionUnderTestContext sessionContextUnderTest;


    private void mockUI() {
        if (session == null) {
            mockSession();
        }

        ui = new UI();
        ui.getInternals().setSession(session);
        ui.doInit(null, ++uiIdNdx);
    }

    private void mockSession() {
        if (sessionContextUnderTest == null) {
            sessionContextUnderTest = new SessionUnderTestContext();
            sessionContextUnderTest.activate();
        }
        session = sessionContextUnderTest.getSession();
    }

    @Override
    public void activate() {
        if (ui == null) {
            mockUI();
        }
        UI.setCurrent(ui);
    }

    @Override
    public void tearDown() {
        UI.setCurrent(null);
        uiIdNdx = 0;
        if (sessionContextUnderTest != null) {
            sessionContextUnderTest.tearDown();
            sessionContextUnderTest = null;
        }
    }

    @Override
    public void destroy() {
        ComponentUtil.onComponentDetach(ui);
    }
}
