#!/bin/bash
# mvn clean org.eclipse.dash:license-tool-plugin:license-check -Ddash.summary=DEPENDENCIES.mvn
# mise exec java@24 -- java -jar ../dash-licenses/shaded/target/org.eclipse.dash.licenses-1.1.1-SNAPSHOT-shaded.jar -summary DEPENDENCIES.npm_modeler org.eclipse.winery.frontends/package-lock.json
mise exec java@24 -- java -jar ../dash-licenses/shaded/target/org.eclipse.dash.licenses-1.1.1-SNAPSHOT-shaded.jar -summary DEPENDENCIES.npm_lsp org.eclipse.winery.lsp.client/package-lock.json

# cat DEPENDENCIES.mvn DEPENDENCIES.npm_modeler DEPENDENCIES.npm_lsp > DEPENDENCIES
