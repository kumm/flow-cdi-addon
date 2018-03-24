package com.vaadin.flow.cdi.polymertemplate;

import com.vaadin.flow.cdi.NormalUIScoped;
import com.vaadin.flow.cdi.UIScoped;
import com.vaadin.flow.cdi.contexts.UIUnderTestContext;
import com.vaadin.flow.cdi.internal.CdiInstantiator;
import com.vaadin.flow.cdi.server.CdiVaadinServletService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.internal.StateNode;
import com.vaadin.flow.internal.nodefeature.ElementData;
import com.vaadin.flow.internal.nodefeature.NodeProperties;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.templatemodel.TemplateModel;
import elemental.json.Json;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(CdiTestRunner.class)
public class TemplateFieldInstantiateTest {

    @Inject
    private BeanManager beanManager;
    private UIUnderTestContext uiUnderTestContext;
    private TestTemplate template;

    @Before
    public void setUp() {
        uiUnderTestContext = new UIUnderTestContext();
        uiUnderTestContext.activate();
        UI ui = uiUnderTestContext.getUi();

        final CdiVaadinServletService service = mock(CdiVaadinServletService.class);
        Instantiator instantiator = new CdiInstantiator(service, beanManager);

        final DeploymentConfiguration conf = mock(DeploymentConfiguration.class);
        when(service.getDeploymentConfiguration()).thenReturn(conf);
        when(conf.isProductionMode()).thenReturn(false);
        VaadinService.setCurrent(service);

        when(ui.getSession().getService()).thenReturn(service);
        when(service.getInstantiator()).thenReturn(instantiator);

        template = instantiator.getOrCreate(TestTemplate.class);
    }

    @After
    public void tearDown() {
        uiUnderTestContext.tearDown();
    }

    @Test
    public void testScopedComponentInstantiated() {
        final UIScopedLabel label = BeanProvider
                .getContextualReference(UIScopedLabel.class);
        label.setText("CDI");
        assertEquals("CDI", template.pseudo.getText());
    }

    @Test
    public void testPseudoScopedComponentAttached() {
        assertComponentAttached(template.pseudo, "pseudo");
    }

    @Test
    public void testNormalScopedComponentAttached() {
        assertComponentAttached(template.normal, "normal");
    }

    private void assertComponentAttached(Component component, String id) {
        final StateNode nodeInComponent = component.getElement().getNode();
        final JsonValue payload = nodeInComponent.getFeature(ElementData.class).getPayload();
        assertNotNull(payload);
        JsonObject payloadJson = Json.createObject();
        payloadJson.put(NodeProperties.TYPE, NodeProperties.INJECT_BY_ID);
        payloadJson.put(NodeProperties.PAYLOAD, Json.create(id));
        assertTrue(payload.jsEquals(payloadJson));
    }


    @UIScoped
    @Tag("uiscoped-label")
    public static class UIScopedLabel extends Label {
    }

    @NormalUIScoped
    @Tag("normaluiscoped-label")
    public static class NormalUIScopedLabel extends Label {
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

    @Tag("test-template")
    public static class TestTemplate extends PolymerTemplate<TemplateModel> {
        @Id("pseudo")
        UIScopedLabel pseudo;

        @Id("normal")
        NormalUIScopedLabel normal;

        public TestTemplate() {
            super(new TestTemplateParser(
                    TestTemplate.class
                            .getResourceAsStream("test-template.html")));
        }
    }
}
