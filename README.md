# Vaadin Flow CDI addon

## Getting started

### For inpatients
Introduce addon as a dependency, and start using @Inject in your route targets, or layouts. 

### Startup
If you do not customize Vaadin Servlet in your web.xml, 
a CDI enabled Vaadin servlet is deployed automatically. 

As UI class [CdiUI](flow-cdi-addon/src/main/java/com/wcs/vaadin/flow/cdi/internal/CdiUI.java) is configured. If you need a custom UI class, you have to extend this class. It is not a CDI contextual instance, but injects work.

Otherwise you can customize 
[CdiVaadinServlet](flow-cdi-addon/src/main/java/com/wcs/vaadin/flow/cdi/server/CdiVaadinServlet.java) 
just like VaadinServlet.

## Component instantiation and CDI

Vaadin triggered instantiation happens in a 
[CDI aware Vaadin Instantiator](flow-cdi-addon/src/main/java/com/wcs/vaadin/flow/cdi/internal/CdiInstantiator.java) 
implementation. 
Components created by this API:

- @Route, RouteLayout, HasErrorParameter components
- component fields injected by @Id to polymer templates

By default instantiator looks up the CDI bean by type ( component class ), 
and gets a contextual reference from BeanManager. 
All the CDI features are usable like observer, interceptor, decorator.

When type is not found as a CDI bean 
( for example ambiguous, or does not have a no-arg public constructor ), 
instantiation falls back to the default Vaadin behavior. 
On success, dependency injection is performed. 
Injects work, but other CDI features not. It is a not a contextual instance. 

## Vaadin Contexts

### VaadinServiceScoped

[@VaadinServiceScoped](flow-cdi-addon/src/main/java/com/wcs/vaadin/flow/cdi/VaadinServiceScoped.java) 
is a normal ( proxied ) scope. Its purpose to define a scope for the beans used by VaadinService. Like an Instantiator, or a I18NProvider.   

### VaadinSessionScoped

[@VaadinSessionScoped](flow-cdi-addon/src/main/java/com/wcs/vaadin/flow/cdi/VaadinSessionScoped.java) 
is a normal ( proxied ) scope. Every VaadinSession have a separate Context. 

### UIScoped, NormalUIScoped

Every UI have a separate Context. 
Practically it means there is just one instance per UI for the scoped class.

For components, use [@UIScoped](flow-cdi-addon/src/main/java/com/wcs/vaadin/flow/cdi/UIScoped.java). 
It is a pseudo scope, so gives a direct reference. 
Vaadin component tree does not work properly with CDI client proxies.

For other beans you can use 
[@NormalUIScoped](flow-cdi-addon/src/main/java/com/wcs/vaadin/flow/cdi/NormalUIScoped.java). 
Given it is normal scoped, have some benefit. 
For example can handle cyclic dependency.

### RouteScoped, NormalRouteScoped 

[@RouteScoped](flow-cdi-addon/src/main/java/com/wcs/vaadin/flow/cdi/RouteScoped.java) context lifecycle on its own is same as UI context's. 
Together with the concept of [@RouteScopeOwner](flow-cdi-addon/src/main/java/com/wcs/vaadin/flow/cdi/RouteScopeOwner.java) it can be used to bind beans to router components (target/layout/exceptionhandlers).
Until owner remains in the route, all beans owned by it remain in the scope.
 
Normal, and non-normal meaning can be found at UI scopes.
 

## Services

Some Vaadin service interfaces can be implemented as a CDI bean.

- I18NProvider
- Instantiator
- SystemMessagesProvider
- ErrorHandler

Beans have to be qualifed by 
[@VaadinServiceEnabled](flow-cdi-addon/src/main/java/com/wcs/vaadin/flow/cdi/VaadinServiceEnabled.java) 
to be picked up automatically.

## Vaadin Events

Following events fired as a CDI event:

- ServiceInitEvent
- PollEvent
- BeforeEnterEvent
- BeforeLeaveEvent
- AfterNavigationEvent
- UIInitEvent
- SessionInitEvent
- SessionDestroyEvent
- ServiceDestroyEvent

You just need a CDI observer to handle them.

## Known issues and limitations

### ServiceDestroyEvent

During application shutdown it is implementation specific, 
whether it works with CDI or not. 
But according to servlet specs, 
a servlet destroy ( it means a service destroy too ) can happen in 
other circumstances too.

### Push with CDI

An incoming websocket message does not count as a request in CDI. 
Need a http request to have request, session, and conversation context. 

So you should use WEBSOCKET_XHR (it is the default), or LONG_POLLING 
transport, otherwise you lost these contexts in event handlers.

In background threads these contexts are not active regardless of push.

### Router and CDI

Vaadin scans router classes (targets, layouts) without any clue about CDI beans. 
Using producers, or excluding the bean class from types with ```@Typed``` causes issues with these kind of beans.

### Instantiator and CDI Qualifiers

As you can see at component instantiation, beans looked up by bean type. 
The API can not provide qualifiers, so lookup is done with ```@Any```.

# Credits

- Many infrastructural knowledge, and behavior came from Flow Spring addon.
- Many code came form the official Vaadin CDI addon 3.0 ( some in turn from my AltCDI addon )