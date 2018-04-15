package com.wcs.vaadin.flow.cdi.itest.push;

import org.junit.Test;

public class WebsocketForegroundTest extends AbstractPushTest {

    @Test
    public void testWsNoXhrForegroundRequestAndSessionDoesNotActive() {
        open("websocket");
        click(PushComponent.RUN_FOREGROUND);
        assertAllExceptRequestAndSessionActive();
    }
}
