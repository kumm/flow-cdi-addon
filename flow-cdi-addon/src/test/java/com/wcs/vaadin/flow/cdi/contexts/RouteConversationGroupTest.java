package com.wcs.vaadin.flow.cdi.contexts;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;
import com.wcs.vaadin.flow.cdi.RouteScopeOwner;
import com.wcs.vaadin.flow.cdi.RouteScoped;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(CdiTestRunner.class)
public class RouteConversationGroupTest {

    public static final String STATE = "hello";

    private UIUnderTestContext uiUnderTestContext;

    @Inject
    private Provider<Group1> group1;

    @Inject
    @RouteScopeOwner(Group1.class)
    private Provider<MemberOfGroup1> memberOfGroup1;

    @Inject
    private Provider<Group2> group2;

    @Inject
    Event<AfterNavigationEvent> afterNavigationTrigger;

    private List<HasElement> chain;
    private AfterNavigationEvent event;

    @Before
    public void setUp() {
        uiUnderTestContext = new UIUnderTestContext();
        uiUnderTestContext.activate();

        group1.get().setState(STATE);
        group2.get().setState(STATE);
        memberOfGroup1.get().setState(STATE);

        assertEquals(STATE, group1.get().getState());
        assertEquals(STATE, memberOfGroup1.get().getState());
        assertEquals(STATE, group2.get().getState());

        chain = new ArrayList<>();
        event = Mockito.mock(AfterNavigationEvent.class);
        Mockito.when(event.getActiveChain()).thenReturn(chain);
    }

    @After
    public void tearDown() {
        uiUnderTestContext.tearDownAll();
    }

    @Test
    public void testGroupDestroyedWhenOwnerMissing() {
        chain.add(group2.get());
        chain.add(memberOfGroup1.get());
        afterNavigationTrigger.fire(event);

        assertEquals("", group1.get().getState());
        assertEquals("", memberOfGroup1.get().getState());
        assertEquals(STATE, group2.get().getState());
    }

    @Test
    public void testGroupSurvivesWhenOwnerRemains() {
        chain.add(group1.get());
        afterNavigationTrigger.fire(event);

        assertEquals(STATE, group1.get().getState());
        assertEquals(STATE, memberOfGroup1.get().getState());
        assertEquals("", group2.get().getState());
    }

    @Test
    public void testDestroyAll() {
        afterNavigationTrigger.fire(event);

        assertEquals("", group1.get().getState());
        assertEquals("", memberOfGroup1.get().getState());
        assertEquals("", group2.get().getState());
    }

    private abstract static class HasElementTestBean extends TestBean implements HasElement {
        @Override
        public Element getElement() {
            return null;
        }
    }

    @RouteScoped
    @Route("group1")
    public static class Group1 extends HasElementTestBean  {
    }

    @RouteScoped
    @RouteScopeOwner(Group1.class)
    public static class MemberOfGroup1 extends HasElementTestBean {
    }

    @RouteScoped
    @Route("group2")
    public static class Group2 extends HasElementTestBean {
    }

}
