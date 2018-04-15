package com.wcs.vaadin.flow.cdi.itest.push;

import org.junit.Test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;

public class WebsocketXhrForegroundTest extends AbstractPushTest {

    @Test
    public void testWsWithXhrForegroundAllContextsActive() {
        open("websocket-xhr");
        click(PushComponent.RUN_FOREGROUND);
        assertContextActive(RequestScoped.class, true);
        assertContextActive(SessionScoped.class, true);
        assertContextActive(ApplicationScoped.class, true);
        assertVaadinContextsActive();
    }
}
