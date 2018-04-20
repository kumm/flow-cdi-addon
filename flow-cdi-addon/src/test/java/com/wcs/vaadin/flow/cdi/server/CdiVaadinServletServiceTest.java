package com.wcs.vaadin.flow.cdi.server;

import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinService;
import com.wcs.vaadin.flow.cdi.internal.BeanLookup;
import com.wcs.vaadin.flow.cdi.internal.CdiInstantiator;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

@RunWith(CdiTestRunner.class)
public class CdiVaadinServletServiceTest {

    @Inject
    private BeanManager beanManager;

    private CdiVaadinServletService service;

    @After
    public void tearDown() {
        VaadinService.setCurrent(null);
    }

    @Test
    public void testCdiInstantiatorCreated() throws ServiceException {
        service = new TestCdiVaadinServletService(beanManager);
        VaadinService.setCurrent(service);
        service.init();
        final Instantiator instantiator = service.getInstantiator();
        assertThat(instantiator, instanceOf(CdiInstantiator.class));
    }

    @Test(expected = ServiceException.class)
    public void testAmbiguousCdiInstantiatorThrowsException() throws ServiceException {
        BeanManager mockBm = mock(BeanManager.class);
        HashSet<Bean<?>> beans = new HashSet<Bean<?>>() {{
            add(mock(Bean.class));
            add(mock(Bean.class));
        }};
        when(mockBm.getBeans(eq(Instantiator.class), same(BeanLookup.SERVICE)))
                .thenReturn(beans);
        //noinspection unchecked
        when(mockBm.resolve(same(beans)))
                .thenThrow(AmbiguousResolutionException.class);
        service = new TestCdiVaadinServletService(mockBm);
        service.init();

        verify(mockBm, times(1)).resolve(same(beans));
    }

    @Test(expected = ServiceException.class)
    public void testNoCdiInstantiatorThrowsException() throws ServiceException {
        BeanManager mockBm = mock(BeanManager.class);
        HashSet<Bean<?>> beans = new HashSet<>();
        when(mockBm.getBeans(eq(Instantiator.class), same(BeanLookup.SERVICE)))
                .thenReturn(beans);
        service = new TestCdiVaadinServletService(mockBm);
        service.init();
    }

    @Test(expected = ServiceException.class)
    public void testInitReturnsFalseThrowsException() throws ServiceException {
        BeanManager mockBm = mock(BeanManager.class);
        service = new TestCdiVaadinServletService(mockBm);

        final Bean mockBean = mock(Bean.class);
        HashSet<Bean<?>> beans = new HashSet<Bean<?>>() {{
            add(mockBean);
        }};
        when(mockBm.getBeans(eq(Instantiator.class), same(BeanLookup.SERVICE)))
                .thenReturn(beans);
        //noinspection unchecked
        when(mockBm.resolve(same(beans))).thenReturn(mockBean);
        Instantiator mockInstantiator = mock(Instantiator.class);
        when(mockBm.getReference(same(mockBean), eq(Instantiator.class), any()))
                .thenReturn(mockInstantiator);
        when(mockInstantiator.init(same(service))).thenReturn(false);
        service.init();

        verify(mockInstantiator, times(1)).init(same(service));
    }

}
