# Routes in the Repository Angular App

## Considered Alternatives
* Using Wildcards for the `ToscaTypes`
* Explicitly Define the Routes for Each `ToscaType`

## Decision Outcome
* Chosen Alternative: *Explicitly Define the Routes for Each `ToscaType`*
* By choosing this alternative, the whole project gets more type save by the (lightweight) trade-off
 of maintaining a list of all available `MainRoutes` in the `ToscaTypes` enum. It is now harder to add
 new main routes, because you need to add extra `Modules` and `RoutingModules` for each type. However,
 because of this decision, it is easier to define invalid routes which lead to a `404 - Not Found` error page.

## Pros and Cons of the Alternatives

### Using Wildcards for the `ToscaTypes`
* `+` Easy to add new `MainRoutes` and `SubRoutes` for multiple `ToscaTypes` at once
* `+` All available sub-routes come "for free" in each component
* `-` Invalid routes can be reached
* `-` More difficult to understand

### Explicitly Define the Routes for Each `ToscaType`
* `+` Easier to understand and therefore eases the start for new developers
* `+` Clear responsibilities
* `+` Implicit better type safety
* `+` Only valid routes are available for each `ToscaType`
* `-` More files need to be extended/created in order to add new routes

## License

Copyright (c) 2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
