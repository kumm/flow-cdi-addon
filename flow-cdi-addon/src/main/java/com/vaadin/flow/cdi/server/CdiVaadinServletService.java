package com.vaadin.flow.cdi.server;

import com.vaadin.flow.cdi.internal.CdiInstantiator;
import com.vaadin.flow.cdi.internal.VaadinSessionScopedContext;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CdiVaadinServletService extends VaadinServletService {

    private final BeanManager beanManager;

    public CdiVaadinServletService(VaadinServlet servlet,
                                   DeploymentConfiguration configuration,
                                   BeanManager beanManager) {
        super(servlet, configuration);
        this.beanManager = beanManager;
        addSessionDestroyListener(this::sessionDestroy);
    }

    @Override
    protected Optional<Instantiator> loadInstantiators()
            throws ServiceException {
        Optional<Instantiator> spiInstantiator = super.loadInstantiators();
        List<Instantiator> cdiInstantiators = beanManager
                .getBeans(Instantiator.class).stream()
                .map(this::getInstantiatorReference)
                .filter(instantiator -> instantiator.init(this))
                .collect(Collectors.toList());
        if (spiInstantiator.isPresent() && !cdiInstantiators.isEmpty()) {
            throw new ServiceException(
                    "Cannot init VaadinService because there are multiple eligible "
                            + "instantiator implementations: Java SPI registered instantiator "
                            + spiInstantiator.get()
                            + " and CDI instantiator beans: "
                            + cdiInstantiators);
        }
        if (!spiInstantiator.isPresent() && cdiInstantiators.isEmpty()) {
            Instantiator defaultInstantiator = new CdiInstantiator(this,
                    beanManager);
            defaultInstantiator.init(this);
            return Optional.of(defaultInstantiator);
        }
        return spiInstantiator.isPresent() ? spiInstantiator
                : cdiInstantiators.stream().findFirst();
    }

    private Instantiator getInstantiatorReference(Bean<?> bean) {
        final CreationalContext<?> creationalContext = beanManager
                .createCreationalContext(bean);
        return (Instantiator) beanManager.getReference(bean,
                Instantiator.class,
                creationalContext);
    }

    private static Logger getLogger() {
        return Logger.getLogger(CdiVaadinServletService.class
                .getCanonicalName());
    }

    private void sessionDestroy(SessionDestroyEvent event) {
        if (VaadinSessionScopedContext.guessContextIsUndeployed()) {
            // Happens on tomcat when it expires sessions upon undeploy.
            // beanManager.getPassivationCapableBean returns null for passivation id,
            // so we would get an NPE from AbstractContext.destroyAllActive
            getLogger().warning("VaadinSessionScoped context does not exist. " +
                    "Maybe application is undeployed." +
                    " Can't destroy VaadinSessionScopedContext.");
            return;
        }
        getLogger().fine("VaadinSessionScopedContext destroy");
        VaadinSessionScopedContext.destroy(event.getSession());
    }

}
