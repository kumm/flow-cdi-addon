package com.vaadin.flow.cdi.contexts;

import com.vaadin.flow.cdi.VaadinSessionScoped;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.runner.RunWith;

@RunWith(CdiTestRunner.class)
public class SessionContextTest extends AbstractContextTest<SessionContextTest.SessionScopedTestBean> {

    @Override
    protected UnderTestContext newContextUnderTest() {
        return new SessionUnderTestContext();
    }

    @Override
    protected boolean isNormalSoped() {
        return true;
    }

    @Override
    protected Class<SessionScopedTestBean> getBeanType() {
        return SessionScopedTestBean.class;
    }

    @VaadinSessionScoped
    public static class SessionScopedTestBean extends TestBean {
    }

}
