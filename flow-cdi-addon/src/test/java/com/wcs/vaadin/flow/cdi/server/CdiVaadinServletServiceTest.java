package com.wcs.vaadin.flow.cdi.server;

import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.server.*;
import com.wcs.vaadin.flow.cdi.VaadinServiceEnabled;
import com.wcs.vaadin.flow.cdi.VaadinServiceScoped;
import com.wcs.vaadin.flow.cdi.contexts.ServiceUnderTestContext;
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
        new ServiceUnderTestContext(beanManager).tearDownAll();
    }

    @Test
    public void testInstantiatorCreated() throws ServiceException {
        initService(beanManager);
        final Instantiator instantiator = service.getInstantiator();
        assertThat(instantiator, instanceOf(CdiInstantiator.class));
    }

    @Test
    public void testSystemMessagesProviderCreated() throws ServiceException {
        initService(beanManager);
        SystemMessagesProvider systemMessagesProvider =
                service.getSystemMessagesProvider();
        assertThat(systemMessagesProvider,
                instanceOf(CdiSystemMessagesProvider.class));
    }

    @Test(expected = ServiceException.class)
    public void testAmbiguousSystemMessagesProviderThrowsException()
            throws ServiceException {
        assertAmbiguousThrowsException(SystemMessagesProvider.class);
    }

    @Test(expected = ServiceException.class)
    public void testAmbiguousInstantiatorThrowsException()
            throws ServiceException {
        assertAmbiguousThrowsException(Instantiator.class);
    }

    @Test(expected = ServiceException.class)
    public void testWithoutSystemMessagesProviderDefaultInstantiated()
            throws ServiceException {
        initServiceWithoutBeanFor(SystemMessagesProvider.class);
        assertThat(service.getSystemMessagesProvider(),
                instanceOf(DefaultSystemMessagesProvider.class));
    }

    @Test(expected = ServiceException.class)
    public void testNoInstantiatorThrowsException() throws ServiceException {
        initServiceWithoutBeanFor(Instantiator.class);
    }

    @Test(expected = ServiceException.class)
    public void testInstantiatorInitReturnsFalseThrowsException()
            throws ServiceException {
        BeanManager mockBm = mock(BeanManager.class);
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
        initService(mockBm);

        verify(mockInstantiator, times(1)).init(same(service));
    }

    private void initService(BeanManager beanManager) throws ServiceException {
        ServiceUnderTestContext serviceUnderTestContext = new ServiceUnderTestContext(beanManager);
        serviceUnderTestContext.activate();
        service = serviceUnderTestContext.getService();
        service.init();
    }

    private void initServiceWithoutBeanFor(Class<?> type)
            throws ServiceException {
        BeanManager mockBm = mock(BeanManager.class);
        HashSet<Bean<?>> beans = new HashSet<>();
        when(mockBm.getBeans(eq(type), same(BeanLookup.SERVICE)))
                .thenReturn(beans);
        initService(mockBm);
    }

    private void assertAmbiguousThrowsException(Class<?> type)
            throws ServiceException {
        BeanManager mockBm = mock(BeanManager.class);
        HashSet<Bean<?>> beans = new HashSet<Bean<?>>() {{
            add(mock(Bean.class));
            add(mock(Bean.class));
        }};
        when(mockBm.getBeans(eq(type), same(BeanLookup.SERVICE)))
                .thenReturn(beans);
        //noinspection unchecked
        when(mockBm.resolve(same(beans)))
                .thenThrow(AmbiguousResolutionException.class);
        initService(mockBm);

        verify(mockBm, times(1)).resolve(same(beans));
    }

    @VaadinServiceEnabled
    @VaadinServiceScoped
    public static class CdiSystemMessagesProvider
            implements SystemMessagesProvider {

        @Override
        public SystemMessages getSystemMessages(
                SystemMessagesInfo systemMessagesInfo) {
            return new CustomizedSystemMessages();
        }
    }

}
