package com.wcs.vaadin.flow.cdi.server;

import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import com.wcs.vaadin.flow.cdi.internal.BeanLookup;
import com.wcs.vaadin.flow.cdi.internal.CdiInstantiator;
import com.wcs.vaadin.flow.cdi.internal.VaadinSessionScopedContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.BeanManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wcs.vaadin.flow.cdi.internal.BeanLookup.SERVICE;

/**
 * Servlet service implementation for Vaadin CDI.
 *
 * This class automatically finds and initializes a CDI {@link Instantiator}.
 *
 * @see CdiVaadinServlet
 */
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
        final List<Instantiator> cdiInstantiators
                = new BeanLookup<>(beanManager, Instantiator.class, SERVICE)
                .all()
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

    private static Logger getLogger() {
        return LoggerFactory.getLogger(CdiVaadinServletService.class
                .getCanonicalName());
    }

    private void sessionDestroy(SessionDestroyEvent event) {
        if (VaadinSessionScopedContext.guessContextIsUndeployed()) {
            // Happens on tomcat when it expires sessions upon undeploy.
            // beanManager.getPassivationCapableBean returns null for passivation id,
            // so we would get an NPE from AbstractContext.destroyAllActive
            getLogger().warn("VaadinSessionScoped context does not exist. " +
                    "Maybe application is undeployed." +
                    " Can't destroy VaadinSessionScopedContext.");
            return;
        }
        getLogger().debug("VaadinSessionScopedContext destroy");
        VaadinSessionScopedContext.destroy(event.getSession());
    }

}
