package com.vaadin.flow.cdi.instantiator;

import com.vaadin.flow.cdi.internal.CdiInstantiator;
import com.vaadin.flow.cdi.server.CdiVaadinServletService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.apache.deltaspike.core.api.exclude.Exclude;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@RunWith(CdiTestRunner.class)
public class InstantiatorTest {

    @Inject
    BeanManager beanManager;

    @Inject
    RouteTarget2 singleton;

    private Instantiator instantiator;

    @Before
    public void setUp() {
        instantiator = new CdiInstantiator(
                Mockito.mock(CdiVaadinServletService.class),
                beanManager);
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

    @Test
    public void getI18NProvider_i18nProviderIsABean_i18nProviderIsAvailable() {
        Assert.assertNotNull(instantiator.getI18NProvider());
        Assert.assertEquals(I18NTestProvider.class,
                instantiator.getI18NProvider().getClass());
    }

    @Test
    public void getServiceInitListeners_springManagedBeanAndJavaSPI_bothClassesAreInStream() {
        Set<?> set = instantiator.getServiceInitListeners()
                .map(Object::getClass).collect(Collectors.toSet());

        Assert.assertTrue(set.contains(TestVaadinServiceInitListener.class));
        Assert.assertTrue(set.contains(JavaSPIVaadinServiceInitListener.class));
    }

    @Test
    public void testNonCdiBeanInstatiationFallback() {
        final NotACdiBean instance = instantiator.getOrCreate(NotACdiBean.class);
        Assert.assertNotNull(instance);
        Assert.assertNull(instance.getBm());
    }

    @Test
    public void testAmbiguousResoulitionInstantiationFallback() {
        final ParentBean instance = instantiator.getOrCreate(ParentBean.class);
        Assert.assertNotNull(instance);
        Assert.assertNull(instance.getBm());
    }

    public static class RouteTarget1 extends Div {

    }

    @Singleton
    public static class RouteTarget2 extends Div {

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

    public static class TestVaadinServiceInitListener
            implements VaadinServiceInitListener {

        @Override
        public void serviceInit(ServiceInitEvent event) {
        }

    }

}
