package com.vaadin.flow.cdi.contexts;

import com.vaadin.flow.cdi.UIScoped;
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
    protected boolean isNormalSoped() {
        return false;
    }

    @UIScoped
    public static class UIScopedTestBean extends TestBean {
    }

}
