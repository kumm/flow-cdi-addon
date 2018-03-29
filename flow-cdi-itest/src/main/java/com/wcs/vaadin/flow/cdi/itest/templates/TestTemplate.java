package com.wcs.vaadin.flow.cdi.itest.templates;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import com.wcs.vaadin.flow.cdi.UIScoped;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@Tag("test-template")
@HtmlImport("frontend://test-template.html")
@Route("")
public class TestTemplate extends PolymerTemplate<TemplateModel> {
    private @Id("input")
    Input input;

    private @Id("label")
    UIScopedTagLabel label;

    private @Inject
    Event<InputChangeEvent> setTextEventTrigger;

    public TestTemplate() {
        input.addChangeListener(event ->
                setTextEventTrigger.fire(new InputChangeEvent(input.getValue())));
    }

    @UIScoped
    @Tag("uiscoped-label")
    public static class UIScopedTagLabel extends Label {

        private void onSetText(@Observes InputChangeEvent event) {
            setText(event.getText());
        }

    }

    public static class InputChangeEvent {
        private final String text;

        public InputChangeEvent(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
