package com.wcs.vaadin.flow.cdi.server;

import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.*;
import com.wcs.vaadin.flow.cdi.VaadinServiceEnabled;
import com.wcs.vaadin.flow.cdi.internal.BeanLookup;
import com.wcs.vaadin.flow.cdi.internal.VaadinSessionScopedContext;
import org.apache.deltaspike.core.util.ProxyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Optional;

import static com.wcs.vaadin.flow.cdi.internal.BeanLookup.SERVICE;

/**
 * Servlet service implementation for Vaadin CDI.
 * <p>
 * This class creates and initializes a @{@link VaadinServiceEnabled}
 * {@link Instantiator}, and
 * {@link SystemMessagesProvider} contextual instance.
 *
 * @see CdiVaadinServlet
 */
public class CdiVaadinServletService extends VaadinServletService {

    private final BeanManager beanManager;

    public CdiVaadinServletService(CdiVaadinServlet servlet,
                                   DeploymentConfiguration configuration,
                                   BeanManager beanManager) {
        super(servlet, configuration);
        this.beanManager = beanManager;
    }

    /**
     * Static listener class,
     * to avoid registering the whole service instance.
     */
    private static class Listener
            implements SessionInitListener, SessionDestroyListener, UIInitListener {

        private final BeanManager beanManager;

        Listener(BeanManager beanManager) {
            this.beanManager = beanManager;
        }

        @Override
        public void sessionInit(SessionInitEvent sessionInitEvent)
                throws ServiceException {
            VaadinSession session = sessionInitEvent.getSession();
            lookup(beanManager, ErrorHandler.class)
                    .ifPresent(session::setErrorHandler);
            beanManager.fireEvent(sessionInitEvent);
        }

        @Override
        public void sessionDestroy(SessionDestroyEvent sessionDestroyEvent) {
            beanManager.fireEvent(sessionDestroyEvent);
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
            VaadinSessionScopedContext.destroy(sessionDestroyEvent.getSession());
        }

        @Override
        public void uiInit(UIInitEvent event) {
            beanManager.fireEvent(event);
        }
    }

    @Override
    public void init() throws ServiceException {
        lookup(beanManager, SystemMessagesProvider.class)
                .ifPresent(this::setSystemMessagesProvider);
        Listener listener = new Listener(beanManager);
        addSessionInitListener(listener);
        addSessionDestroyListener(listener);
        addUIInitListener(listener);
        super.init();
    }

    @Override
    public CdiVaadinServlet getServlet() {
        return (CdiVaadinServlet) super.getServlet();
    }

    @Override
    protected Optional<Instantiator> loadInstantiators()
            throws ServiceException {
        Optional<Instantiator> instantiatorOptional =
                lookup(beanManager, Instantiator.class);
        if (instantiatorOptional.isPresent()) {
            Instantiator instantiator = instantiatorOptional.get();
            if (!instantiator.init(this)) {
                Class unproxiedClass =
                        ProxyUtils.getUnproxiedClass(instantiator.getClass());
                throw new ServiceException(
                        "Cannot init VaadinService because "
                                + unproxiedClass.getName() + " CDI bean init()"
                                + " returned false.");
            }
        } else {
            throw new ServiceException(
                    "Cannot init VaadinService "
                            + "because no CDI instantiator bean found."
            );
        }
        return instantiatorOptional;
    }

    protected static <T> Optional<T> lookup(BeanManager beanManager,
                                            Class<T> type) throws ServiceException {
        try {
            T instance = new BeanLookup<>(beanManager, type, SERVICE).get();
            return Optional.ofNullable(instance);
        } catch (AmbiguousResolutionException e) {
            throw new ServiceException(
                    "There are multiple eligible CDI " + type.getSimpleName()
                            + " beans.", e);
        }
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(CdiVaadinServletService.class
                .getCanonicalName());
    }

}
