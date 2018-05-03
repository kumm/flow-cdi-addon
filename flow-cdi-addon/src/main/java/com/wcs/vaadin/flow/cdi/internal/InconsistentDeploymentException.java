package com.wcs.vaadin.flow.cdi.internal;

public class InconsistentDeploymentException extends RuntimeException {
    enum ID {
        ROUTE_SCOPE_OWNER_UNREACHABLE,
        ROUTE_SCOPE_OWNER_MISSING,
        ROUTE_SCOPE_OWNER_NOT_ROUTE_COMPONENT,
        ROUTE_SCOPE_MISSING_BESIDE_OWNER
    }

    private ID id;

    public InconsistentDeploymentException(ID id, String message) {
        super(message);
        this.id = id;
    }

    public InconsistentDeploymentException(ID id, String message, Throwable cause) {
        super(message, cause);
        this.id = id;
    }
}
