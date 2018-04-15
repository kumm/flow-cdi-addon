package com.wcs.vaadin.flow.cdi.itest.push;

import org.junit.Test;

public class WebsocketXhrBackgroundTest extends AbstractPushTest {

    @Test
    public void testInBackgroundRequestAndSessionDoesNotActive() {
        open("websocket-xhr");
        click(PushComponent.RUN_BACKGROUND);
        waitForPush();
        assertAllExceptRequestAndSessionActive();
    }
}
