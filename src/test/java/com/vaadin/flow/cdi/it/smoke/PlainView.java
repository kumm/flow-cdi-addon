package com.vaadin.flow.cdi.it.smoke;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.router.Route;

@BodySize(height = "100vh", width = "100vw")
@Route("")
public class PlainView extends Div {

    public PlainView() {
        NativeButton button = new NativeButton("Click me",
                event -> {
                    final Span hello = new Span("hello");
                    hello.setId("HELLO");
                    add(hello);
                });
        button.setId("CLICK_ME");
        add(button);
    }
}
