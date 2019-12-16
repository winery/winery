export const WineryReducer = function(
  lastState: WineryState = INITIAL_WINERY_STATE,
  action: Action
): WineryState {
  switch (action.type) {

    // ............................................
    // ............................................
    // ............................................
    // ............................................
    // .............MANY CASES HERE................
    // ............................................
    // ............................................
    // ............................................
    // ............................................
    // ............................................

    // Example for changing a nodeTemplates name attribute
    case WineryActions.CHANGE_NODE_NAME:
      const newNodeName: any = (<SidebarNodeNamechange>action).nodeNames
      const indexChangeNodeName = lastState.currentJsonTopology.nodeTemplates
        .map(el => el.id)
        .indexOf(newNodeName.id)

      return <WineryState> {
        // Spread Operator avoids using Object.assign({})
        ...lastState,
        currentJsonTopology: {
          ...lastState.currentJsonTopology,
          nodeTemplates: lastState.currentJsonTopology.nodeTemplates.map(
            nodeTemplate =>
              nodeTemplate.id === newNodeName.id
                ? nodeTemplate.generateNewNodeTemplateWithUpdatedAttribute(
                    'name',
                    newNodeName.newNodeName
                  )
                : nodeTemplate
          )
        }
      }
  }
}
