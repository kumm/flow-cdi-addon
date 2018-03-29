package com.wcs.vaadin.flow.cdi.contexts;

import com.wcs.vaadin.flow.cdi.NormalUIScoped;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.runner.RunWith;

@RunWith(CdiTestRunner.class)
public class UiContextNormalTest extends AbstractContextTest<UiContextNormalTest.NormalUIScopedTestBean> {

    @Override
    protected Class<NormalUIScopedTestBean> getBeanType() {
        return NormalUIScopedTestBean.class;
    }

    @Override
    protected UnderTestContext newContextUnderTest() {
        return new UIUnderTestContext();
    }

    @Override
    protected boolean isNormalSoped() {
        return true;
    }

    @NormalUIScoped
    public static class NormalUIScopedTestBean extends TestBean {
    }

}
