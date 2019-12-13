ui:
  features:
    accountability: true
    completion: true
    compliance: true
    freezeAndDefrost: true
    managementFeatureEnrichment: true
    nfv: true
    patternRefinement: true
    problemDetection: true
    splitting: true
    testRefinement: true
  endpoints:
    container: http://{{ .Env.CONTAINER_HOSTNAME }}:{{ .Env.CONTAINER_PORT }}
    workflowmodeler: http://{{ .Env.WORKFLOWMODELER_HOSTNAME }}:{{ .Env.WORKFLOWMODELER_PORT }}/winery-workflowmodeler
    topologymodeler: http://{{ .Env.TOPOLOGYMODELER_HOSTNAME }}:{{ .Env.TOPOLOGYMODELER_PORT }}/winery-topologymodeler
    repositoryApiUrl: http://{{ .Env.WINERY_HOSTNAME }}:{{ .Env.WINERY_PORT }}/winery
    repositoryUiUrl: http://{{ .Env.WINERY_HOSTNAME }}:{{ .Env.WINERY_PORT }}/#
    edmmTransformationTool: http://{{ .Env.EDMM_TRANSFORMATION_HOSTNAME }}:{{ .Env.EDMM_TRANSFORMATION_PORT }}/check_transformation
repository:
  provider: file
  repositoryRoot: {{ .Env.WINERY_REPOSITORY_PATH }}
  git:
    clientSecret: secret
    password: default
    clientID: id
    autocommit: false
    username: default
