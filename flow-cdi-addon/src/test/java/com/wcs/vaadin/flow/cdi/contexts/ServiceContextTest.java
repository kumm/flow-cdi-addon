package com.wcs.vaadin.flow.cdi.contexts;

import com.wcs.vaadin.flow.cdi.VaadinServiceScoped;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.runner.RunWith;

@RunWith(CdiTestRunner.class)
public class ServiceContextTest extends AbstractContextTest<ServiceContextTest.ServiceScopedTestBean> {

    @Override
    protected Class<ServiceScopedTestBean> getBeanType() {
        return ServiceScopedTestBean.class;
    }

    @Override
    protected UnderTestContext newContextUnderTest() {
        return new ServiceUnderTestContext();
    }

    @Override
    protected boolean isNormalScoped() {
        return true;
    }

    @VaadinServiceScoped
    public static class ServiceScopedTestBean extends TestBean {
    }

}
