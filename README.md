# Vaadin Flow CDI addon

## Getting started

### For inpatients
Introduce addon as a dependency, and start using @Inject in your route targets. 

### Startup
If you do not customize Vaadin Servlet in your web.xml, a CDI enabled Vaadin servlet is deployed automatically. 
Otherwise you can customize [CdiVaadinServlet](flow-cdi-addon/src/main/java/com/wcs/vaadin/flow/cdi/server/CdiVaadinServlet.java) same as VaadinServlet.

## Component instantiation and CDI

Vaadin triggered instantiation happens in a [CDI aware Vaadin Instantiator](flow-cdi-addon/src/main/java/com/wcs/vaadin/flow/cdi/internal/CdiInstantiator.java) implementation. Components created by this API:

- @Route components
- component fields injected by @Id to polymer templates

By default instantiator looks up the CDI bean, and gets a contextual reference from BeanManager. It means it is a contextual instance. All the CDI features are usable like observer, interceptor, decorator.

When the class is not a CDI bean ( for example does not have a no-arg public constructor ), instantiation falls back to the default Vaadin behavior. On success, dependency injection is performed. Injects work, but no other CDI feature. This is a not a contextual instance. 

## Vaadin Contexts

### VaadinSessionScoped

[@VaadinSessionScoped](flow-cdi-addon/src/main/java/com/wcs/vaadin/flow/cdi/VaadinSessionScoped.java) is a normal ( proxied ) scope. Every VaadinSession have a separate Context. 

### UIScoped, NormalUIScoped

Every UI have a separate Context. Practically it means there is just one instance per UI for the scoped class.

For components, use [@UIScoped](flow-cdi-addon/src/main/java/com/wcs/vaadin/flow/cdi/UIScoped.java). It is a pseudo scope, so gives a direct reference. Vaadin component tree does not work properly with CDI client proxies.

For other beans you can use [@NormalUIScoped](flow-cdi-addon/src/main/java/com/wcs/vaadin/flow/cdi/NormalUIScoped.java). Given it is normal scoped, have some benefit. For example can handle cyclic dependency. 

## Services

Some Vaadin service interface can be implemented as a CDI bean.

- I18NProvider
- Instantiator

Implementations have to be qualifed by [@VaadinServiceEnabled](flow-cdi-addon/src/main/java/com/wcs/vaadin/flow/cdi/VaadinServiceEnabled.java).

## Vaadin Events

ServiceInitEvent fired as a CDI event. You just need a CDI observer to handle it.

# Credits

- Many infrastructural knowledge, and behavior came from Flow Spring addon.
- Many code came form the official Vaadin CDI addon 3.0 ( some in turn from my AltCDI addon )