ui:
  features:
    accountability: {{ .Env.WINERY_FEATURE_ACCOUNTABILITY }}
    completion: {{ .Env.WINERY_FEATURE_TEST_COMPLETION }}
    compliance: {{ .Env.WINERY_FEATURE_TEST_COMPLIANCE }}
    freezeAndDefrost: {{ .Env.WINERY_FEATURE_FREEZE_DEFROST }}
    managementFeatureEnrichment: {{ .Env.WINERY_FEATURE_MANAGEMENT_ENRICHMENT }}
    nfv: {{ .Env.WINERY_FEATURE_NFV }}
    patternRefinement: {{ .Env.WINERY_FEATURE_PATTERN_REFINEMENT }}
    problemDetection: {{ .Env.WINERY_FEATURE_PROBLEM_DETECTION }}
    splitting: {{ .Env.WINERY_FEATURE_SPLITTING }}
    testRefinement: {{ .Env.WINERY_FEATURE_TEST_REFINEMENT }}
    edmmModeling: {{ .Env.WINERY_FEATURE_EDMM_MODELING }}
  endpoints:
    container: http://{{ .Env.CONTAINER_HOSTNAME }}:{{ .Env.CONTAINER_PORT }}
    workflowmodeler: http://{{ .Env.WORKFLOWMODELER_HOSTNAME }}:{{ .Env.WORKFLOWMODELER_PORT }}/winery-workflowmodeler
    topologymodeler: http://{{ .Env.TOPOLOGYMODELER_HOSTNAME }}:{{ .Env.TOPOLOGYMODELER_PORT }}/winery-topologymodeler
    repositoryApiUrl: http://{{ .Env.WINERY_HOSTNAME }}:{{ .Env.WINERY_PORT }}/winery
    repositoryUiUrl: http://{{ .Env.WINERY_HOSTNAME }}:{{ .Env.WINERY_PORT }}/#
    edmmTransformationTool: http://{{ .Env.EDMM_TRANSFORMATION_HOSTNAME }}:{{ .Env.EDMM_TRANSFORMATION_PORT }}/plugins/check-model-support
repository:
  provider: file
  repositoryRoot: {{ .Env.WINERY_REPOSITORY_PATH }}
  git:
    clientSecret: secret
    password: default
    clientID: id
    autocommit: false
    username: default
