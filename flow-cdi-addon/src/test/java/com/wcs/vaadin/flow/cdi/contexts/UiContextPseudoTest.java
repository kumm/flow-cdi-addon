package com.wcs.vaadin.flow.cdi.contexts;

import com.wcs.vaadin.flow.cdi.UIScoped;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.runner.RunWith;

@RunWith(CdiTestRunner.class)
public class UiContextPseudoTest extends AbstractContextTest<UiContextPseudoTest.UIScopedTestBean> {

    @Override
    protected Class<UIScopedTestBean> getBeanType() {
        return UIScopedTestBean.class;
    }

    @Override
    protected UnderTestContext newContextUnderTest() {
        return new UIUnderTestContext();
    }

    @Override
    protected boolean isNormalScoped() {
        return false;
    }

    @UIScoped
    public static class UIScopedTestBean extends TestBean {
    }

}
