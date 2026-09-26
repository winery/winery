# Angular upgrade plan (7 → 20)

Tracking PR: #821 (branch `angular-upgrade`).
Goal: current Angular, so the frontend Dependabot PRs
(#787, #788, #802, #803, #809, #813, #817, #818, #819, #820) become obsolete or mergeable.

Apps (all in this directory, one `angular.json`): `tosca-management`, `topologymodeler`, `workflowmodeler`.
`app/shared` is used by several apps.

## Status

- [x] Node 22 / npm 10 build (#814)
- [x] Remove `@angular/http` and unused deps
- [x] Angular 8 (+ Material 8, `rxjs-compat` → public rxjs API, `angular2-markdown` → `marked`)
- [x] Angular 9 (A1): Ivy on, ngcc runs on demand during `ng build` (no `postinstall` needed).
  ngx-bootstrap 6 (secondary entry points only), ng2-file-upload 1.4.0, ngx-chips 2.1.0.
  `ngcc.config.js` makes ng2-select readable for ngcc; delete it together with ng2-select (B2).
  `entryComponents` are still there (harmless, drop them in a later step).

## Work packages

`A*` is a strict sequence (each major builds on the previous one).
`B*` are independent of the Angular version and of each other: they can run in parallel with each other and with `A1`–`A7`.
`C*` need the listed prerequisites.

| ID | Work package | Depends on | Scope |
|----|--------------|------------|-------|
| A1 | Angular 9 (Ivy, ngcc for View Engine libs) | – | all |
| A2 | Angular 10 | A1 | all |
| A3 | Angular 11 | A2 | all |
| A4 | Angular 12 (webpack 5; drop `--openssl-legacy-provider` if build passes without it) | A3 | all |
| A5 | Angular 13 | A4 | all |
| A6 | Angular 14 | A5 | all |
| A7 | Angular 15 (last version with ngcc) | A6 | all |
| B1 | Replace `@angular-redux/store` with a local `NgRedux` shim | – | topologymodeler (55 files) |
| B2 | Replace `ng2-select` with `@ng-select/ng-select` | – | tosca-management (46 files), workflowmodeler (1) |
| B3 | Replace small dead libs: `ng2-table`, `ng-sidebar`, `angular2-hotkeys`, `ngx-chips`, `ng-diff-match-patch` | – | ≤ 3 files each |
| C1 | TSLint → ESLint (`ng add @angular-eslint/schematics`) | A3 | all |
| C2 | Angular 16 – 20 | A7, B1, B2, B3, C1 | all |
| C3 | Remove workarounds: `legacy-peer-deps` in `.npmrc`, `NODE_OPTIONS` in `pom.xml`, `overrides` if any | C2 | build |

Libraries that stay but must be bumped **inside each A step** to the release matching that Angular major
(check the library's peer dependency on `@angular/core`):
`ngx-bootstrap` (95 files, mainly `ModalDirective`, `BsModalService`, `BsModalRef`), `ngx-toastr`, `ng2-file-upload`,
`angular-resize-event`, `angular-resizable-element`, `@angular-slider/ngx-slider`, `ngx-pagination`, `@angular/material` + `@angular/cdk`.
From ngx-bootstrap 6 on, the root barrel import `from 'ngx-bootstrap'` is gone: use `ngx-bootstrap/modal` etc.

### B1 – `@angular-redux/store` → local shim

The API surface actually used is small: `NgRedux<T>` injected in 53 files, `configureStore` (once, `winery.module.ts`),
`dispatch` (220×), `select` (87×), `getState` (3×), `subscribe` (1×), `NgReduxModule` (8×), `DevToolsExtension` (2×).
No `@select` decorators.

Write one small `NgRedux` class plus `NgReduxModule` (e.g. `app/topologymodeler/src/app/redux/ng-redux.ts`) on top of
the existing `redux` dependency: `configureStore` → `createStore` (+ Redux DevTools via `window.__REDUX_DEVTOOLS_EXTENSION__`),
`select(selector)` → `Observable` backed by a `BehaviorSubject` of the state with `map` + `distinctUntilChanged`,
run dispatches so change detection still happens (`NgZone.run`).
Then change only the import paths. Do not rewrite call sites.

### B2 – `ng2-select` → `@ng-select/ng-select`

Used bindings: `[items]` (52), `[active]` (27), `[allowClear]` (8), `[multiple]` (2), `[disabled]`, `(selected)` (41),
`(data)` (8), `(removed)` (2). `ng2-select` items are `{ id, text }`; ng-select uses `bindLabel`/`bindValue`
(`bindLabel="text"`, `bindValue="id"` keeps the data model). `[active]` becomes `[(ngModel)]` / `[ngModel]`,
`(selected)` becomes `(change)`. Pick the `@ng-select/ng-select` major that supports the Angular version on the branch
at the time of the work (v3 for Angular 8, v4 for 9, …) and add its theme CSS to `angular.json` styles.
Compare behaviour of every converted select by hand (see "Verification").

### B3 – small dead libraries

| Library | Suggested replacement |
|---------|----------------------|
| `ng2-table` | plain `<table>` with the existing sorting/filtering code (check what features are used first) |
| `ng-sidebar` | `@angular/material` sidenav (Material is already a dependency) or plain CSS |
| `angular2-hotkeys` | `@HostListener('document:keydown', …)` |
| `ngx-chips` | `@ng-select/ng-select` with `[multiple]="true" [addTag]="true"` (coordinate with B2) |
| `ng-diff-match-patch` | `diff-match-patch` directly (the Angular wrapper is thin) |

## How to run a step

All commands from `org.eclipse.winery.frontends/`. Use the Node that Maven installs (`./node`, Node 22):

```sh
../mvnw -Pfrontend -q initialize          # once: downloads ./node (Node 22 / npm 10)
export PATH="$PWD/node:$PATH" NODE_OPTIONS=--openssl-legacy-provider
alias npm='node node/node_modules/npm/bin/npm-cli.js'
npm ci
```

`ng update` cannot be used as-is: from CLI 8.3 on it always re-runs itself as `@angular/cli@latest`,
which refuses Node 22.20. Instead, per Angular major N:

1. `npm install --save-exact` the `@angular/*` packages at N, plus the matching `@angular-devkit/build-angular`,
   `typescript`, `zone.js`, `rxjs` (see <https://angular.dev/reference/versions> and <https://angular.dev/update-guide>).
2. Run each migration listed for N in `node_modules/@angular/core/schematics/migrations.json`
   (and `@angular/cli`, `@angular/material`, `@angular/cdk`) with the schematics CLI of the same release line:

   ```sh
   npx -y @angular-devkit/schematics-cli@<devkit version> \
     ./node_modules/@angular/core/schematics/migrations.json:<migration-name> --dry-run=false
   ```
3. Work through the update guide's manual items for N.

## Verification (every work package)

```sh
for a in tosca-management topologymodeler workflowmodeler; do
  npm run lint-$a && npm run build-$a-prod || echo "FAIL $a"
done
../mvnw -Pfrontend -B package -pl org.eclipse.winery.frontends   # what CI runs
```

Additionally for B1, B2, B3 and every third A step: start the backend and click through the affected screens
(`npm run start-<app>`), because there are no frontend unit tests in CI.

## Rules for contributors (humans and agents)

- One work package = one branch off `angular-upgrade` = one PR into `angular-upgrade`.
- One commit per logical step; never commit `node/` or `node_modules/`.
- `package-lock.json` conflicts: take the target branch's version, then re-run `npm install`.
- Eclipse Foundation rules apply (<https://www.eclipse.org/projects/handbook/#genai-disclosure>): every commit carries
  `Signed-off-by:` of the responsible human, AI assistance is disclosed with an `Assisted-by:` trailer
  (not `Co-authored-by:`), PR descriptions disclose AI assistance.
- Keep diffs minimal: change imports and APIs, do not reformat or refactor unrelated code.
- Tick the checkbox in "Status" and note surprises for the next step in the PR description.
