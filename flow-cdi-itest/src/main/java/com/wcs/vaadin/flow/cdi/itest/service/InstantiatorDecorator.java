package com.wcs.vaadin.flow.cdi.itest.service;

import com.vaadin.flow.di.Instantiator;
import com.wcs.vaadin.flow.cdi.VaadinServiceEnabled;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

@Decorator
public abstract class InstantiatorDecorator implements Instantiator {
    @Inject
    @Delegate
    @VaadinServiceEnabled
    Instantiator delegate;

    @Override
    public <T> T getOrCreate(Class<T> type) {
        T instance = delegate.getOrCreate(type);
        if (InstantiatorDecoratorView.class.equals(type)) {
            ((InstantiatorDecoratorView) instance).decorate();
        }
        return instance;
    }
}
