package com.wcs.vaadin.flow.cdi.server;

import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.VaadinServletService;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import javax.enterprise.inject.spi.BeanManager;

import static org.mockito.Mockito.mock;

public class TestCdiVaadinServletService extends CdiVaadinServletService {

    public TestCdiVaadinServletService(BeanManager beanManager) {
        super(
                new TestCdiVaadinServlet(),
                mock(DeploymentConfiguration.class),
                beanManager);
        ((TestCdiVaadinServlet) getServlet()).setService(this);
    }

    // We have nothing to do with atmosphere,
    // and mocking is much easier without it.
    @Override
    protected boolean isAtmosphereAvailable() {
        return false;
    }

    /**
     * A crippled servlet to provide contextual storage for VaadinServiceScoped
     * without full initialization.
     */
    private static class TestCdiVaadinServlet extends CdiVaadinServlet {
        private TestCdiVaadinServletService service;

        TestCdiVaadinServlet() {
            BeanProvider.injectFields(this);
            initContextualStorage();
        }

        void setService(TestCdiVaadinServletService service) {
            this.service = service;
        }

        @Override
        public VaadinServletService getService() {
            return service;
        }
    }
}
