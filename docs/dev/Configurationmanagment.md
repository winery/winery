# Configurationmanagement

All Components are implemented in the package `org.eclipse.winery.common.configuration`.
The configuration is YAML based and called "winery.yml". The default Configuration is contained in
the resource folder.

## Adding new Features
To add a new feature to the configuration one has to simply add it hierarchically under the features tab in the winery.yml file cf. Figure 1.
This has to be done both in the winery.yml file in the filesystem and in the default configuration winery.yml file in the resources folder.

```yaml
ui:
  features:
    splitting: true
    completion: true
    patternRefinement: true
    compliance: true
    accountability: true
    nfv: true
  endpoints:
    container: http://localhost:1337
    workflowmodeler: http://localhost:8080/winery-workflowmodeler
    topologymodeler: http://localhost:8080/winery-topologymodeler
    repositoryApiUrl: http://localhost:8080/winery
    repositoryUiUrl: http://localhost:8080/#
repository:
  provider: file
  repositoryRoot: ""
  git:
    clientSecret: secret
    password: default
    clientID: id
    autocommit: false
    username: default
```
**Figure 1: Adding new feature to the YAML Configuration.**

If the feature has been added to the YAML Configuration the ```getUiConfig()``` method of the Environments class will return a UiConfigurationObject Instance
which has the added feature as a map entry in the features map attribute. This can be accessed with the getFeatures() method.
The key of the feature entry is the same name that was added to the winery.yml file.

## Accessing the configuration in the backend

The configuration is split into different objects. The `UiConfigurationObject`
contains the feature flags and endpoints. 
The `RepositoryConfigurationObject` contains all the repository settings including a 
`GitConfigurationObject`.
The `GitConfigurationObject` contains all settings associated with Git.
Each of these configuration objects can be accessed through a getter in the 
`Environments` class, e.g. `getGitConfig()`.
When the changes to a configuration object shall be persisted, the
`Environments` class offers a save method, in which the changed configuration object
has to be passed as the parameter.

## Accessing the Configuration in the Frontend
In the `org.eclipse.winery.repository.rest.resources.admin.AdminTopResource.java` are two methods implemented which
are used to send the configuration to the frontend ```getConfig()``` and get updates to the configuration from the frontend ```setConfig()```.
In the frontend the `WineryRepositoryConfigurationService` manages those resources. Injecting the service where the configuration
is needed provides the configuration as the configuration attribute of the WineryRepositoryConfigurationService. Therefore the feature
has to be added as a boolean attribute to the WineryConfiguration interface cf. Figure 2.
```typescript
export interface WineryConfiguration {
    features: {
        splitting: boolean;
        completion: boolean;
        compliance: boolean;
        patternRefinement: boolean;
        accountability: boolean;
        nfv: boolean;
        newFeature: boolean;
    };
    endpoints: {
        container: String;
        workflowmodeler: String;
        topologymodeler: String;
    };
}
``` 
**Figure 2: Adding new feature to the WineryConfiguration Interface.**


### Using feature toggles
The `FeatureToggleDirective` offers a way to use the configuration to toggle features on or off dynamically.
Before using the directive in any html file it has to be imported first into the corresponding module. 
Additionally an enum in the component where the feature toggle will be used has to be created and assigned 
the FeatureEnum which is part of the `wineryRepository.feature.direct.ts`.
If a new feature is added, it also has to be added to the FeatureEnum cf. Figure 3. The string value for each feature has to be the
same name as defined in the `WineryConfiguration` interface.
```typescript
export enum FeatureEnum {
    Splitting = 'splitting', Completion = 'completion', Compliance = 'compliance',
    PatternRefinement = 'patternRefinement', Accountability = 'accountability', NFV = 'nfv',
    newFeature = 'newFeature'
}
```
**Figure 3: Adding new feature to the Enum.**

Finally the directive can be used to toggle the feature according to the set configuration cf. Figure 4.
```html
<div *wineryRepositoryFeatureToggle="configEnum.Compliance">
```
**Figure 4: Using the directive for the pattern refinement feature.**
