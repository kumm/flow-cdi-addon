package com.wcs.vaadin.flow.cdi.server;

import com.vaadin.flow.server.VaadinServletService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CdiVaadinServletTest {

    @Mock
    BeanManager beanManager;

    @InjectMocks
    CdiVaadinServlet servlet;

    @Before
    public void setUp() throws ServletException {
        final ServletConfig servletConfig = Mockito.mock(ServletConfig.class);
        final ServletContext servletContext = Mockito.mock(ServletContext.class);
        Mockito.when(servletConfig.getInitParameterNames())
                .thenReturn(Collections.emptyEnumeration());
        Mockito.when(servletConfig.getServletContext())
                .thenReturn(servletContext);
        Mockito.when(servletContext.getInitParameterNames())
                .thenReturn(Collections.emptyEnumeration());

        servlet.init(servletConfig);
    }

    @After
    public void tearDown() {
        servlet.destroy();
    }

    @Test
    public void testCdiServletServiceCreated() {
        final VaadinServletService service = servlet.getService();
        assertThat(service, instanceOf(CdiVaadinServletService.class));
    }
}
