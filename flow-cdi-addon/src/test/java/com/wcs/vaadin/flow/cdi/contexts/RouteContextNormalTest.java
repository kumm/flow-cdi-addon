package com.wcs.vaadin.flow.cdi.contexts;

import com.wcs.vaadin.flow.cdi.NormalRouteScoped;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.runner.RunWith;

@RunWith(CdiTestRunner.class)
public class RouteContextNormalTest extends AbstractContextTest<RouteContextNormalTest.RouteScopedTestBean> {

    @Override
    protected Class<RouteScopedTestBean> getBeanType() {
        return RouteScopedTestBean.class;
    }

    @Override
    protected UnderTestContext newContextUnderTest() {
        // Intentionally UI Under Test Context. Nothing else need.
        return new UIUnderTestContext();
    }

    @Override
    protected boolean isNormalScoped() {
        return true;
    }

    @NormalRouteScoped
    public static class RouteScopedTestBean extends TestBean {
    }

}
