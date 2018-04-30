package com.wcs.vaadin.flow.cdi.server;

import com.vaadin.flow.server.VaadinServletService;
import com.wcs.vaadin.flow.cdi.contexts.ServiceUnderTestContext;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(CdiTestRunner.class)
public class CdiVaadinServletTest {

    @Inject
    private BeanManager beanManager;

    private CdiVaadinServlet servlet;

    @Before
    public void setUp() throws ServletException {
        final ServletConfig servletConfig = Mockito.mock(ServletConfig.class);
        final ServletContext servletContext = Mockito.mock(ServletContext.class);
        Mockito.when(servletConfig.getInitParameterNames())
                .thenReturn(Collections.emptyEnumeration());
        Mockito.when(servletConfig.getServletContext())
                .thenReturn(servletContext);
        Mockito.when(servletConfig.getServletName())
                .thenReturn("test");
        Mockito.when(servletContext.getInitParameterNames())
                .thenReturn(Collections.emptyEnumeration());
        servlet = new CdiVaadinServlet();
        BeanProvider.injectFields(servlet);
        servlet.init(servletConfig);
    }

    @After
    public void tearDown() {
        new ServiceUnderTestContext(beanManager).tearDownAll();
        servlet.destroy();
    }

    @Test
    public void testCdiServletServiceCreated() {
        final VaadinServletService service = servlet.getService();
        assertThat(service, instanceOf(CdiVaadinServletService.class));
    }
}
