package com.wcs.vaadin.flow.cdi.server;

import com.vaadin.flow.function.DeploymentConfiguration;

import javax.enterprise.inject.spi.BeanManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestCdiVaadinServletService extends CdiVaadinServletService {

    public TestCdiVaadinServletService(BeanManager beanManager, String servletName) {
        super(
                mock(CdiVaadinServlet.class),
                mock(DeploymentConfiguration.class),
                beanManager);
        when(getServlet().getServletName()).thenReturn(servletName);
        when(getServlet().getService()).thenReturn(this);
    }

    // We have nothing to do with atmosphere,
    // and mocking is much easier without it.
    @Override
    protected boolean isAtmosphereAvailable() {
        return false;
    }

}
