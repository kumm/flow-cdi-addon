package com.vaadin.flow.cdi.contexts;

import com.vaadin.flow.cdi.VaadinSessionScoped;
import com.vaadin.flow.cdi.internal.VaadinSessionScopedContext;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.VaadinSessionState;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.enterprise.context.ContextNotActiveException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

@RunWith(CdiTestRunner.class)
public class SessonContextTest {

    public static class TestSession extends VaadinSession {

        public TestSession() {
            super(Mockito.mock(VaadinService.class));
        }

    }

    @Before
    public void setUp() {
        VaadinSession.setCurrent(null);
        SessionScopedTestBean.resetCount();
    }

    @Test(expected = ContextNotActiveException.class)
    public void testNoCurrentSessionThrowException() {
        final SessionScopedTestBean reference = BeanProvider
                .getContextualReference(SessionScopedTestBean.class);
        reference.getState();
    }

    @Test
    public void testWithSessionBeanCreatedOnce() {
        mockSession();
        final SessionScopedTestBean reference = BeanProvider
                .getContextualReference(SessionScopedTestBean.class);
        reference.setState("hello");
        assertThat(reference.getState(), equalTo("hello"));
        assertThat(SessionScopedTestBean.getBeanCount(), equalTo(1));
    }

    @Test
    public void testNewSessionBeanReCreated() {
        mockSession();
        final SessionScopedTestBean reference = BeanProvider
                .getContextualReference(SessionScopedTestBean.class);
        reference.setState("hello");
        mockSession();
        assertThat(reference.getState(), equalTo(""));
        assertThat(SessionScopedTestBean.getBeanCount(), equalTo(2));
    }

    @Test
    public void testSessionContextDestroy() {
        mockSession();
        final SessionScopedTestBean reference = BeanProvider
                .getContextualReference(SessionScopedTestBean.class);
        reference.setState("hello");
        VaadinSessionScopedContext.destroy(VaadinSession.getCurrent());
        assertThat(SessionScopedTestBean.getBeanCount(), equalTo(0));
    }

    private VaadinSession mockSession() {
        VaadinSession session = Mockito.mock(TestSession.class,
                Mockito.withSettings().useConstructor());
        doCallRealMethod().when(session).setAttribute(Mockito.any(String.class),
                Mockito.any());
        doCallRealMethod().when(session).getAttribute(Mockito.any(String.class));
        doCallRealMethod().when(session).getService();

        when(session.getState()).thenReturn(VaadinSessionState.OPEN);

        VaadinSession.setCurrent(session);
        when(session.hasLock()).thenReturn(true);

        return session;
    }

    @VaadinSessionScoped
    public static class SessionScopedTestBean extends TestBean {
    }

}
