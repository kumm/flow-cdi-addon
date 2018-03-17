package com.vaadin.flow.cdi.instantiator;

import com.vaadin.flow.cdi.internal.CdiInstantiator;
import com.vaadin.flow.cdi.server.CdiVaadinServlet;
import com.vaadin.flow.cdi.server.CdiVaadinServletService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.server.VaadinServletService;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(CdiTestRunner.class)
public class InstantiatorTest {

    @Inject
    CdiVaadinServlet servlet;

    @Inject
    RouteTarget2 singleton;

    private Instantiator instantiator;

    @Before
    public void setUp() throws Exception {
        final ServletConfig servletConfig = Mockito.mock(ServletConfig.class);
        final ServletContext servletContext = Mockito.mock(ServletContext.class);
        Mockito.when(servletConfig.getInitParameterNames())
                .thenReturn(Collections.emptyEnumeration());
        Mockito.when(servletConfig.getServletContext())
                .thenReturn(servletContext);
        Mockito.when(servletContext.getInitParameterNames())
                .thenReturn(Collections.emptyEnumeration());

        servlet.init(servletConfig);
        VaadinServletService service = servlet.getService();
        assertThat(service, instanceOf(CdiVaadinServletService.class));

        instantiator = service.getInstantiator();
        assertThat(instantiator, instanceOf(CdiInstantiator.class));
    }

    @Test
    public void createRouteTarget_pojo_instanceIsCreated() {
        RouteTarget1 target1 = instantiator
                .createRouteTarget(RouteTarget1.class, null);
        Assert.assertNotNull(target1);
    }

    @Test
    public void createRouteTarget_cdiManagedBean_instanceIsCreated() {
        Assert.assertEquals(singleton,
                instantiator.createRouteTarget(RouteTarget2.class, null));
    }

    public static class RouteTarget1 extends Div {

    }

    @Singleton
    public static class RouteTarget2 extends Div {

    }
}
