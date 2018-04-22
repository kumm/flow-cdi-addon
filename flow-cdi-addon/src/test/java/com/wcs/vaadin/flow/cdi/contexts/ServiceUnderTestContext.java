package com.wcs.vaadin.flow.cdi.contexts;

import com.vaadin.flow.server.VaadinService;
import com.wcs.vaadin.flow.cdi.server.CdiVaadinServletService;
import com.wcs.vaadin.flow.cdi.server.TestCdiVaadinServletService;

import javax.enterprise.inject.spi.CDI;

public class ServiceUnderTestContext implements UnderTestContext {
    private CdiVaadinServletService service;

    @Override
    public void activate() {
        service = new TestCdiVaadinServletService(CDI.current().getBeanManager());
        VaadinService.setCurrent(service);
    }

    @Override
    public void tearDownAll() {
        VaadinService.setCurrent(null);
    }

    @Override
    public void destroy() {
        if (service != null) {
            service.getServlet().destroy();
        }
    }

    public CdiVaadinServletService getService() {
        return service;
    }
}
