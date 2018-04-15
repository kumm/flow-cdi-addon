package com.wcs.vaadin.flow.cdi.itest.push;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Route;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Route("websocket")
@Push
public class WebsocketPushView extends Div {
    @Inject
    PushComponent pushComponent;

    @PostConstruct
    private void init() {
        add(pushComponent);
    }
}
