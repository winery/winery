# *Routes in the Repository Angular App*

## Considered Alternatives
* *Using Wildcards for the `ToscaTypes`*
* *Explicitly Define the Routes for Each `ToscaType`*

## Decision Outcome
* Chosen Alternative: *Explicitly Define the Routes for Each `ToscaType`*
* By choosing this alternative, the whole project gets more type save by the (lightweight) trade-off
 of maintaining a list of all available `MainRoutes` in the `ToscaTypes` enum. It is now harder to add
 new main routes, because you need to add extra `Modules` and `RoutingModules` for each type. However,
 because of this decision, it is easier to define invalid routes which lead to a `404 - Not Found` error page.

## Pros and Cons of the Alternatives

### *Using Wildcards for the `ToscaTypes`*
* `+` *Easy to add new `MainRoutes` and `SubRoutes` for multiple `ToscaTypes` at once*
* `+` *All available sub-routes come "for free" in each component*
* `-` *Invalid routes can be reached*
* `-` *More difficult to understand*

### *Explicitly Define the Routes for Each `ToscaType`*
* `+` *Easier to understand and therefore eases the start for new developers*
* `+` *Clear responsibilities*
* `+` *Implicit better type safety*
* `+` *Only valid routes are available for each `ToscaType`*
* `-` *More files need to be extended/created in order to add new routes*
