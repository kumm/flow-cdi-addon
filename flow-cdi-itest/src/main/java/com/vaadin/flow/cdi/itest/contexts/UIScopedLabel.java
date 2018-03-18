package com.vaadin.flow.cdi.itest.contexts;

import com.vaadin.flow.cdi.UIScoped;
import com.vaadin.flow.cdi.itest.Counter;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;

import javax.annotation.PreDestroy;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@UIScoped
public class UIScopedLabel extends Label {

    public static final String DESTROY_COUNT = "UIScopedLabelDestroy";

    @Inject
    Counter counter;

    private int uiId;

    public static final String ID = "UISCOPED_LABEL";

    public UIScopedLabel() {
        setId(ID);
        uiId = UI.getCurrent().getUIId();
    }

    @PreDestroy
    private void destroy() {
        counter.increment(DESTROY_COUNT + uiId);
    }

    private void onSetText(@Observes SetTextEvent event) {
        setText(event.getText());
    }

    public static class SetTextEvent {
        private final String text;

        public SetTextEvent(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
