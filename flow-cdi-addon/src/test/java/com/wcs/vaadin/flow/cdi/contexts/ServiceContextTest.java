package com.wcs.vaadin.flow.cdi.contexts;

import com.wcs.vaadin.flow.cdi.VaadinServiceScoped;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.runner.RunWith;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

@RunWith(CdiTestRunner.class)
public class ServiceContextTest extends AbstractContextTest<ServiceContextTest.ServiceScopedTestBean> {
    @Inject
    private BeanManager beanManager;

    @Override
    protected Class<ServiceScopedTestBean> getBeanType() {
        return ServiceScopedTestBean.class;
    }

    @Override
    protected UnderTestContext newContextUnderTest() {
        return new ServiceUnderTestContext(beanManager);
    }

    @Override
    protected boolean isNormalScoped() {
        return true;
    }

    @VaadinServiceScoped
    public static class ServiceScopedTestBean extends TestBean {
    }

}
