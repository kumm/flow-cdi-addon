package com.wcs.vaadin.flow.cdi.itest.service;

import com.vaadin.flow.server.CustomizedSystemMessages;
import com.vaadin.flow.server.SystemMessages;
import com.vaadin.flow.server.SystemMessagesInfo;
import com.vaadin.flow.server.SystemMessagesProvider;
import com.wcs.vaadin.flow.cdi.VaadinServiceEnabled;
import com.wcs.vaadin.flow.cdi.VaadinServiceScoped;

@VaadinServiceEnabled
@VaadinServiceScoped
public class TestSystemMessagesProvider implements SystemMessagesProvider {

    public static final String EXPIRED_BY_TEST = "EXPIRED BY TEST";

    @Override
    public SystemMessages getSystemMessages(SystemMessagesInfo systemMessagesInfo) {
        CustomizedSystemMessages messages = new CustomizedSystemMessages();
        messages.setSessionExpiredNotificationEnabled(true);
        messages.setSessionExpiredMessage(EXPIRED_BY_TEST);
        return messages;
    }
}
