package com.wcs.vaadin.flow.cdi.contexts;

import com.wcs.vaadin.flow.cdi.RouteScoped;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.runner.RunWith;

@RunWith(CdiTestRunner.class)
public class RouteContextPseudoTest extends AbstractContextTest<RouteContextPseudoTest.RouteScopedTestBean> {

    @Override
    protected Class<RouteScopedTestBean> getBeanType() {
        return RouteScopedTestBean.class;
    }

    @Override
    protected UnderTestContext newContextUnderTest() {
        // Intentionally UI Under Test Context. Nothing else needed.
        return new UIUnderTestContext();
    }

    @Override
    protected boolean isNormalScoped() {
        return false;
    }

    @RouteScoped
    public static class RouteScopedTestBean extends TestBean {
    }

}
