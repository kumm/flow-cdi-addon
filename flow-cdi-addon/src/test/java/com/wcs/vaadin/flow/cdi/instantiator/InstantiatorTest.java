package com.wcs.vaadin.flow.cdi.instantiator;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinService;
import com.wcs.vaadin.flow.cdi.VaadinServiceEnabled;
import com.wcs.vaadin.flow.cdi.contexts.ServiceUnderTestContext;
import com.wcs.vaadin.flow.cdi.internal.CdiInstantiator;
import com.wcs.vaadin.flow.cdi.server.CdiVaadinServletService;
import org.apache.deltaspike.core.api.exclude.Exclude;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Locale;

@RunWith(CdiTestRunner.class)
public class InstantiatorTest {

    @Inject
    private BeanManager beanManager;

    @Inject
    @VaadinServiceEnabled
    private CdiInstantiator instantiator;

    @Inject
    private SomeCdiBean singleton;

    @Inject
    private ServiceInitObserver serviceInitObserver;

    private ServiceUnderTestContext serviceUnderTestContext;

    @Before
    public void setUp() {
        serviceUnderTestContext = new ServiceUnderTestContext(beanManager);
        serviceUnderTestContext.activate();
        CdiVaadinServletService service = serviceUnderTestContext.getService();
        Assert.assertTrue(instantiator.init(service));
    }

    @After
    public void tearDown() {
        serviceUnderTestContext.tearDownAll();
    }

    @Test
    public void testI18NProviderInstantiated() {
        final I18NProvider i18NProvider = instantiator.getI18NProvider();
        Assert.assertNotNull(i18NProvider);
        Assert.assertTrue((i18NProvider instanceof I18NTestProvider));
    }

    @Test
    public void testGetServiceInitListenersContainsSPI() {
        Assert.assertTrue(instantiator.getServiceInitListeners().anyMatch(
                listener -> listener instanceof JavaSPIVaadinServiceInitListener));
    }

    @Test
    public void testServiceInitObserverCalled() {
        final VaadinService service = Mockito.mock(VaadinService.class);
        final ServiceInitEvent event = new ServiceInitEvent(service);
        instantiator.getServiceInitListeners()
                .filter(listener -> listener.getClass().getPackage()
                        .equals(CdiInstantiator.class.getPackage()))
                .forEach(listener -> listener.serviceInit(event));
        Assert.assertSame(event, serviceInitObserver.getEvent());
    }

    @Test
    public void testCdiBeanInstantiated() {
        Assert.assertEquals(singleton,
                instantiator.getOrCreate(SomeCdiBean.class));
    }

    @Test
    public void testNonCdiBeanInstantiationInjectionOccurs() {
        final NotACdiBean instance = instantiator.getOrCreate(NotACdiBean.class);
        Assert.assertNotNull(instance);
        Assert.assertNotNull(instance.getBm());
    }

    @Test
    public void testAmbiguousResolutionInstantiationInjectionOccurs() {
        final ParentBean instance = instantiator.getOrCreate(ParentBean.class);
        Assert.assertNotNull(instance);
        Assert.assertNotNull(instance.getBm());
    }

    @Singleton
    public static class SomeCdiBean extends Div {

    }

    public static class ParentBean {
        @Inject
        BeanManager bm;

        public BeanManager getBm() {
            return bm;
        }
    }


    @Exclude
    public static class NotACdiBean extends ParentBean{
    }

    public static class Ambiguous extends ParentBean {
    }

    @VaadinServiceEnabled
    public static class I18NTestProvider implements I18NProvider {

        @Override
        public List<Locale> getProvidedLocales() {
            return null;
        }

        @Override
        public String getTranslation(String key, Object... params) {
            return null;
        }

        @Override
        public String getTranslation(String key, Locale locale,
                                     Object... params) {
            return null;
        }

    }

    @RequestScoped
    public static class ServiceInitObserver {

        ServiceInitEvent event;

        public void serviceInit(@Observes  ServiceInitEvent event) {
            this.event = event;
        }

        public ServiceInitEvent getEvent() {
            return event;
        }
    }

}
