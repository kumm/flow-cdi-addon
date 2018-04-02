package com.wcs.vaadin.flow.cdi.server;

import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.VaadinServlet;

import javax.enterprise.inject.spi.BeanManager;

import static org.mockito.Mockito.mock;

public class TestCdiVaadinServletService extends CdiVaadinServletService {

    public TestCdiVaadinServletService(BeanManager beanManager) {
        super(
                mock(VaadinServlet.class),
                mock(DeploymentConfiguration.class),
                beanManager);
    }

    // We have nothing to do with atmosphere,
    // and mocking is much easier without it.
    @Override
    protected boolean isAtmosphereAvailable() {
        return false;
    }
}
