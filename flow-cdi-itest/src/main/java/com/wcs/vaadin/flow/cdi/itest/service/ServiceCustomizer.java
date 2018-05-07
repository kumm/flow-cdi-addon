package com.wcs.vaadin.flow.cdi.itest.service;

import com.vaadin.flow.server.BootstrapPageResponse;
import com.vaadin.flow.server.ServiceInitEvent;

import javax.enterprise.event.Observes;

public class ServiceCustomizer {

    public static final String APPENDED_ID = "TEST_ID";
    public static final String APPENDED_TXT = "By Test";

    private void onServiceInit(@Observes ServiceInitEvent serviceInitEvent) {
        serviceInitEvent.addBootstrapListener(this::modifyBootstrapPage);
    }

    private void modifyBootstrapPage(BootstrapPageResponse response) {
        response.getDocument().body().append(
                "<p id='" + APPENDED_ID + "'>" + APPENDED_TXT + "</p>");
    }
}
