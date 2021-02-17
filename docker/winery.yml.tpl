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
    radon: {{ .Env.WINERY_FEATURE_RADON }}
    splitting: {{ .Env.WINERY_FEATURE_SPLITTING }}
    testRefinement: {{ .Env.WINERY_FEATURE_TEST_REFINEMENT }}
    edmmModeling: {{ .Env.WINERY_FEATURE_EDMM_MODELING }}
    updateTemplates: {{ .Env.WINERY_FEATURE_UPDATE_TEMPLATES }}
  endpoints:
    container: http://{{ .Env.CONTAINER_HOSTNAME }}:{{ .Env.CONTAINER_PORT }}
    workflowmodeler: http://{{ .Env.WORKFLOWMODELER_HOSTNAME }}:{{ .Env.WORKFLOWMODELER_PORT }}/winery-workflowmodeler
    topologymodeler: http://{{ .Env.TOPOLOGYMODELER_HOSTNAME }}:{{ .Env.TOPOLOGYMODELER_PORT }}/winery-topologymodeler
    repositoryApiUrl: http://{{ .Env.WINERY_HOSTNAME }}:{{ .Env.WINERY_PORT }}/winery
    repositoryUiUrl: http://{{ .Env.WINERY_HOSTNAME }}:{{ .Env.WINERY_PORT }}/#
    edmmTransformationTool: http://{{ .Env.EDMM_TRANSFORMATION_HOSTNAME }}:{{ .Env.EDMM_TRANSFORMATION_PORT }}/plugins/check-model-support
    eclipseChe: {{ .Env.CHE_URL_PROTOCOL }}://{{ .Env.CHE_URL }}
repository:
  provider: {{ .Env.WINERY_REPOSITORY_PROVIDER }}
  repositoryRoot: {{ .Env.WINERY_REPOSITORY_PATH }}
  csarOutputPath: {{ .Env.WINERY_CSAR_OUTPUT_PATH }}
  git:
    clientSecret: secret
    password: default
    clientID: id
    autocommit: false
    username: default
