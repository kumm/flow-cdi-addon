package com.vaadin.flow.cdi.server;

import com.vaadin.flow.cdi.internal.CdiInstantiator;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinServlet;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(CdiTestRunner.class)
public class CdiVaadinServletServiceTest {

    @Inject
    BeanManager beanManager;

    @Inject
    TestInstantiatorA testInstantiatorA;

    @Inject
    @TestQualifier
    TestInstantiatorB testInstantiatorB;

    private CdiVaadinServletService service;

    abstract static class AbstractTestInstantiator implements Instantiator {
        boolean enabled;

        @Override
        public boolean init(VaadinService service) {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public Stream<VaadinServiceInitListener> getServiceInitListeners() {
            return Stream.empty();
        }

        @Override
        public <T> T getOrCreate(Class<T> type) {
            return null;
        }
    }

    @RequestScoped
    public static class TestInstantiatorA extends AbstractTestInstantiator {
    }

    @RequestScoped
    @TestQualifier
    public static class TestInstantiatorB extends AbstractTestInstantiator {
    }

    @Before
    public void setUp() {
        VaadinServlet servlet = mock(VaadinServlet.class);
        DeploymentConfiguration configuration = mock(DeploymentConfiguration.class);
        service = new CdiVaadinServletService(servlet, configuration, beanManager) {
            // We have nothing to do with atmosphere,
            // and mocking is is much easier without it.
            @Override
            protected boolean isAtmosphereAvailable() {
                return false;
            }
        };
    }

    @Test
    public void testEnabledCdiInstantiatorCreated() throws ServiceException {
        testInstantiatorA.setEnabled(true);
        assertInstantiatorInstanceOf(TestInstantiatorA.class);
    }

    @Test
    public void testEnabledCdiInstantiatorWithQualifierCreated() throws ServiceException {
        testInstantiatorB.setEnabled(true);
        assertInstantiatorInstanceOf(TestInstantiatorB.class);
    }

    @Test
    public void testDefaultInstantiatorCreated() throws ServiceException {
        assertInstantiatorInstanceOf(CdiInstantiator.class);
    }

    @Test
    public void testSPIInstantiatorReturned() throws ServiceException {
        JavaSPIInstantiator.ENABLED = true;
        assertInstantiatorInstanceOf(JavaSPIInstantiator.class);
    }

    @Test(expected = ServiceException.class)
    public void testBothSPIandCDIInstantiatorExistThrowException() throws ServiceException {
        JavaSPIInstantiator.ENABLED = true;
        testInstantiatorA.setEnabled(true);
        service.init();
    }

    private void assertInstantiatorInstanceOf(Class<? extends Instantiator> type) throws ServiceException {
        service.init();
        final Instantiator instantiator = service.getInstantiator();
        assertThat(instantiator, instanceOf(type));
    }
}
