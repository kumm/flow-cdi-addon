package com.vaadin.flow.cdi.contexts;

import com.vaadin.flow.cdi.internal.VaadinSessionScopedContext;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.VaadinSessionState;
import org.mockito.Mockito;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

public class SessionUnderTestContext implements UnderTestContext {

    private VaadinSession session;

    private void mockSession() {
        session = Mockito.mock(TestSession.class,
                Mockito.withSettings().useConstructor());
        doCallRealMethod().when(session).setAttribute(Mockito.any(String.class),
                Mockito.any());
        doCallRealMethod().when(session).getAttribute(Mockito.any(String.class));
        doCallRealMethod().when(session).getService();

        when(session.getState()).thenReturn(VaadinSessionState.OPEN);

        when(session.hasLock()).thenReturn(true);
    }

    @Override
    public void activate() {
        if (session == null) {
            mockSession();
        }
        VaadinSession.setCurrent(session);
    }

    @Override
    public void tearDownAll() {
        VaadinSession.setCurrent(null);
    }

    @Override
    public void destroy() {
        VaadinSessionScopedContext.destroy(session);
    }

    public VaadinSession getSession() {
        return session;
    }

    public static class TestSession extends VaadinSession {

        public TestSession() {
            super(Mockito.mock(VaadinService.class));
        }

    }
}
