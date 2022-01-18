/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/

import { Injectable } from '@angular/core';
import { CsarUpload } from '../models/container/csar-upload.model';
import { of } from 'rxjs';
import { Observable } from 'rxjs/Rx';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, concatMap, filter, map, tap , retry} from 'rxjs/operators';
import { NodeTemplateInstanceStates, PlanTypes, ServiceTemplateInstanceStates } from '../models/enums';
import { Csar } from '../models/container/csar.model';
import { ServiceTemplate } from '../models/container/service-template.model';
import { PlanResources } from '../models/container/plan-resources.model';
import { PlanInstanceResources } from '../models/container/plan-instance-resources.model';
import { ServiceTemplateInstance } from '../models/container/service-template-instance';
import { ServiceTemplateInstanceResources } from '../models/container/service-template-instance-resources.model';
import { Plan } from '../models/container/plan.model';
import { NodeTemplateResources } from '../models/container/node-template-resources.model';
import { NodeTemplateInstanceResources } from '../models/container/node-template-instance-resources.model';
import { NodeTemplateInstance } from '../models/container/node-template-instance.model';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { PlanInstance } from '../models/container/plan-instance.model';
import { PlanLogEntry } from '../models/container/plan-log-entry.model';
import { InputParameter } from '../models/container/input-parameter.model';
import { OutputParameter } from '../models/container/output-parameter.model';
import { NodeTemplate } from '../models/container/node-template.model';
import { AdaptationPayload } from '../models/container/adaptation-payload.model';

@Injectable()
export class ContainerService {
    private containerUrl: string;

    private readonly headerAcceptJSON = {
        headers: new HttpHeaders({
            'Accept': 'application/json'
        })
    };
    private readonly headerContentJSON = {
        headers: new HttpHeaders({
            'Content-Type': 'application/json'
        })
    };
    private readonly headerContentTextPlain = {
        headers: new HttpHeaders({
            'Content-Type': 'text/plain'
        })
    };

    private readonly baseInstallationPayload = [
        { 'name': 'instanceDataAPIUrl', 'type': 'String', 'required': 'true' },
        { 'name': 'csarEntrypoint', 'type': 'String', 'required': 'true' },
        { 'name': 'CorrelationID', 'type': 'String', 'required': 'true' },
        { 'name': 'containerApiAddress', 'type': 'String', 'required': 'true' }
    ];
    private readonly baseManagementPayload = [
        { 'name': 'instanceDataAPIUrl', 'type': 'String', 'required': 'true' },
        { 'name': 'OpenTOSCAContainerAPIServiceInstanceURL', 'type': 'String', 'required': 'true' },
        { 'name': 'CorrelationID', 'type': 'String', 'required': 'true' },
        { 'name': 'containerApiAddress', 'type': 'String', 'required': 'true' }
    ];
    private readonly baseTransformationPayload = [
        { 'name': 'CorrelationID', 'type': 'String', 'required': 'true' },
        { 'name': 'instanceDataAPIUrl', 'type': 'String', 'required': 'true' },
        { 'name': 'planCallbackAddress_invoker', 'type': 'String', 'required': 'true' },
        { 'name': 'csarEntrypoint', 'type': 'String', 'required': 'true' },
        { 'name': 'OpenTOSCAContainerAPIServiceInstanceURL', 'type': 'String', 'required': 'true' },
        { 'name': 'containerApiAddress', 'type': 'String', 'required': 'true' }
    ];
    private readonly hiddenInputParameters = [
        'CorrelationID',
        'csarID',
        'serviceTemplateID',
        'containerApiAddress',
        'instanceDataAPIUrl',
        'planCallbackAddress_invoker',
        'csarEntrypoint',
        'OpenTOSCAContainerAPIServiceInstanceID',
        'OpenTOSCAContainerAPIServiceInstanceURL'
    ];

    constructor(
        private ngRedux: NgRedux<IWineryState>,
        private http: HttpClient,
    ) {
        this.ngRedux.select((state) => {
            return state.liveModelingState.containerUrl;
        })
            .subscribe((containerUrl) => {
                this.containerUrl = containerUrl;
            });
    }

    public getCsar(csarId: string): Observable<Csar> {
        const csarUrl = this.combineURLs(this.combineURLs(this.containerUrl, 'csars'), csarId);
        return this.http.get<Csar>(csarUrl, this.headerAcceptJSON).pipe(map((resp) => {
            return resp;
        }));
    }

    public isApplicationInstalled(csarId: string): Observable<boolean> {
        const csarUrl = this.combineURLs(this.combineURLs(this.containerUrl, 'csars'), csarId);
        return this.http.get(csarUrl, { observe: 'response' }).pipe(
            map((resp) => {
                return resp.ok;
            }),
            catchError(() => of(false))
        );
    }

    public installApplication(uploadPayload: CsarUpload): Observable<any> {
        return this.http.post(this.combineURLs(this.containerUrl, 'csars'), uploadPayload, this.headerContentJSON);
    }

    public deleteApplication(csarId: string): Observable<any> {
        const url = this.combineURLs(this.combineURLs(this.containerUrl, 'csars'), csarId);
        return this.http.delete(url);
    }

    public getRequiredBuildPlanInputParameters(csarId: string): Observable<Array<InputParameter>> {
        return this.getAllBuildPlanInputParameters(csarId).pipe(
            map((resp) => {
                return resp.filter((input) => {
                    return this.hiddenInputParameters.indexOf(input.name) === -1;
                });
            })
        );
    }

    public deployServiceTemplateInstance(csarId: string, buildPlanInputParameters: InputParameter[]): Observable<string> {
        const payload = [...buildPlanInputParameters, ...this.baseInstallationPayload];

        return this.getBuildPlan(csarId).pipe(
            concatMap((resp) => {
                return this.http.post(resp._links['instances'].href, payload, {
                headers: new HttpHeaders({
                    'Content-Type': 'application/json'
                }),
                responseType: 'text'
            });
            })
        );
    }

    public initializeServiceTemplateInstance(csarId: string, correlationId: string): Observable<string> {
        return this.getServiceTemplate(csarId).pipe(
            concatMap((resp) => {
                return this.http.post(resp._links['instances'].href, { 'correlation_id': correlationId }, {
                headers: new HttpHeaders({
                    'Content-Type': 'application/json'
                }),
                responseType: 'text'
            });
            }),
            map((resp) => {
                return resp.replace(/"|%22/g, '');
            }),
            concatMap((resp) => {
                return this.http.get<ServiceTemplateInstance>(resp);
            }),
            map((resp) => {
                return resp.id.toString();
            })
        );
    }

    public getServiceTemplateInstanceIdAfterDeployment(csarId: string, correlationId: string): Observable<string> {
        return this.getBuildPlanInstance(csarId, correlationId).pipe(retry(10),
            map((resp) => {
                return resp ? resp.service_template_instance_id.toString() : '';
            }),
        );
    }

    public getServiceTemplateInstanceState(csarId: string, serviceTemplateInstanceId: string): Observable<ServiceTemplateInstanceStates> {
        return this.getServiceTemplateInstance(csarId, serviceTemplateInstanceId).pipe(
            map((resp) => {
                return ServiceTemplateInstanceStates[resp.state];
            }),
        );
    }

    public getServiceTemplateInstanceBuildPlanInstance(csarId: string, serviceTemplateInstanceId: string): Observable<PlanInstance> {
        return this.getServiceTemplateInstance(csarId, serviceTemplateInstanceId).pipe(
            concatMap((resp) => {
                return this.http.get<PlanInstance>(resp._links['build_plan_instance'].href, this.headerAcceptJSON);
            }),
            map((resp) => {
                resp.inputs = resp.inputs.filter((input) => {
                    return this.hiddenInputParameters.indexOf(input.name) === -1;
                });
                return resp;
            })
        );
    }

    public getBuildPlanOutputParameters(csarId: string): Observable<Array<OutputParameter>> {
        return this.getBuildPlan(csarId).pipe(
            map((resp) => {
                return resp.output_parameters;
            })
        );
    }

    public getBuildPlanLogs(csarId: string, correlationId: string): Observable<Array<PlanLogEntry>> {
        return this.getBuildPlanInstance(csarId, correlationId).pipe(retry(10),
            map((resp) => {
                return resp.logs;
            })
        );
    }

    public getNodeTemplates(csarId: string): Observable<Array<NodeTemplate>> {
        return this.getServiceTemplate(csarId).pipe(
            concatMap((resp) => {
                return this.http.get<NodeTemplateResources>(resp._links['nodetemplates'].href, this.headerAcceptJSON);
            }),
            map((resp) => {
                return resp.node_templates;
            })
        );
    }

    public getNodeTemplateInstance(csarId: string, serviceTemplateInstanceId: string, nodeTemplateId: string): Observable<NodeTemplateInstance> {
        return this.getNodeTemplates(csarId).pipe(
            // TODO: temporary until bug in container fixed (see https://github.com/OpenTOSCA/container/issues/133)
            concatMap((resp) => {
                return this.http.get<NodeTemplateInstanceResources>(
                        resp.find((template) => {
                            return template.id.toString() === nodeTemplateId;
                        })._links['self'].href + '/instances', this.headerAcceptJSON);
                }
            ),
            map((resp) => {
                return resp.node_template_instances.filter((n) => {
                    return n.service_template_instance_id.toString() === serviceTemplateInstanceId;
                });
            }),
            concatMap((resp) => {
                return this.http.get<NodeTemplateInstance>(
                        resp[resp.length - 1]._links['self'].href, this.headerAcceptJSON);
                }
            )
        );
    }

    public getNodeTemplateInstanceState(csarId: string, serviceTemplateInstanceId: string, nodeTemplateId: string): Observable<NodeTemplateInstanceStates> {
        return this.getNodeTemplateInstance(csarId, serviceTemplateInstanceId, nodeTemplateId).pipe(
            map((resp) => {
                return NodeTemplateInstanceStates[resp.state];
            }),
            catchError(() => of(NodeTemplateInstanceStates.NOT_AVAILABLE))
        );
    }

    public generateTransformationPlan(sourceCsarId: string, targetCsarId: string): Observable<string> {
        const transformPayload = {
            'source_csar_name': sourceCsarId,
            'target_csar_name': targetCsarId
        };

        try {
        const endpoint = this.combineURLs(this.containerUrl, 'csars/transform');
        return this.http.post<Plan>(endpoint, transformPayload, this.headerContentJSON).pipe(map((resp) => {
            return resp.id.toString();
        }));
        } catch (error) {
            console.log(error);
        }
    }

    public executeTransformationPlan(
        serviceTemplateInstanceId: string,
        planId: string,
        sourceCsarId: string,
        targetCsarId: string,
        inputParameters: InputParameter[]
    ): Observable<string> {
        const payload = [...inputParameters, ...this.baseTransformationPayload];
        try {
            return this.getManagementPlan(sourceCsarId, serviceTemplateInstanceId, planId).pipe(
                concatMap((resp) => {
                    return this.http.post(resp._links['instances'].href,
                        payload,
                        {
                            headers: new HttpHeaders({
                                'Content-Type': 'application/json'
                            }),
                            responseType: 'text'
                        });
                })
            );
        } catch (error) {
            console.log(error);
        }
    }

    public getServiceTemplateInstanceIdAfterTransformation(
        csarId: string,
        serviceTemplateInstanceId: string,
        correlationId: string,
        planId: string): Observable<string> {
        return this.getManagementPlans(csarId, serviceTemplateInstanceId).pipe(
            concatMap((resp) => {
                return this.http.get<PlanInstanceResources>(
                        resp.find((plan) => {
                            return plan.id === planId && plan.plan_type === PlanTypes.TransformationPlan;
                        })._links['instances'].href, this.headerAcceptJSON);
                }
            ),
            map((resp) => {
                return resp.plan_instances.find(
                    (plan) => {
                        return plan.correlation_id.toString() === correlationId;
                    }).outputs.find((output) => {
                        return output.name === 'instanceId';
                }).value;
            }),
            catchError(() => of(''))
        );
    }

    public getTransformationPlanLogs(
        csarId: string,
        serviceTemplateInstanceId: string,
        planId: string,
        correlationId: string,
    ): Observable<Array<PlanLogEntry>> {
        return this.getManagementPlans(csarId, serviceTemplateInstanceId).pipe(
            concatMap((resp) => {
                return this.http.get<PlanInstanceResources>(
                        resp.find((plan) => {
                            return plan.id === planId && plan.plan_type === PlanTypes.TransformationPlan;
                        })._links['instances'].href, this.headerAcceptJSON);
                }
            ),
            map((resp) => {
                return resp.plan_instances.find((plan) => {
                    return plan.correlation_id.toString() === correlationId;
                }).logs;
            })
        );
    }

    public generateAdaptationPlan(csarId: string, payload: AdaptationPayload): Observable<Plan> {
        return this.getCsar(csarId).pipe(
            concatMap((resp) => {
                return this.http.post<Plan>(this.combineURLs(resp._links['servicetemplate'].href, 'transform'), payload, this.headerContentJSON);
            }),
            map((resp) => {
                return {
                    ...resp,
                    input_parameters: resp.input_parameters.filter((input) => {
                        return this.hiddenInputParameters.indexOf(input.name) === -1;
                    })
                };
            })
        );
    }

    public getManagementPlanInputParameters(csarId: string, serviceTemplateInstanceId: string, planId: string): Observable<InputParameter[]> {
        return this.getManagementPlan(csarId, serviceTemplateInstanceId, planId).pipe(
            map((resp) => {
                return resp.input_parameters.filter((input) => {
                    return this.hiddenInputParameters.indexOf(input.name) === -1;
                });
            })
        );
    }

    public executeManagementPlan(
        csarId: string,
        serviceTemplateInstanceId: string,
        planId: string,
        inputParameters: InputParameter[]
    ): Observable<string> {
        const payload = [...inputParameters, ...this.baseManagementPayload];

        return this.getManagementPlan(csarId, serviceTemplateInstanceId, planId).pipe(
            concatMap((resp) => {
                return this.http.post(resp._links['instances'].href,
                    payload,
                    {
                        headers: new HttpHeaders({
                            'Content-Type': 'application/json'
                        }),
                        responseType: 'text'
                    });
            })
        );
    }

    public updateNodeTemplateInstanceState(
        csarId: string,
        serviceTemplateInstanceId: string,
        nodeTemplateId: string,
        state: NodeTemplateInstanceStates
    ): Observable<any> {
        return this.getNodeTemplateInstance(csarId, serviceTemplateInstanceId, nodeTemplateId).pipe(
            concatMap((resp) => {
                return this.http.put(resp._links['state'].href, state.toString(), this.headerContentTextPlain);
            })
        );
    }

    public executeTerminationPlan(csarId: string, serviceTemplateInstanceId: string): Observable<string> {
        return this.getTerminationPlan(csarId, serviceTemplateInstanceId).pipe(
            concatMap((resp) => {
                return this.http.post(resp._links['instances'].href, this.baseManagementPayload, {
                    headers: new HttpHeaders({
                        'Content-Type': 'application/json'
                    }),
                    responseType: 'text'
                });
            })
        );
    }

    private getServiceTemplate(csarId: string): Observable<ServiceTemplate> {
        return this.getCsar(csarId).pipe(
            concatMap((resp) => {
                return this.http.get<ServiceTemplate>(resp._links['servicetemplate'].href, this.headerAcceptJSON);
            })
        );
    }

    private getServiceTemplateInstance(csarId: string, serviceTemplateInstanceId: string): Observable<ServiceTemplateInstance> {
        return this.getServiceTemplate(csarId).pipe(
            concatMap((resp) => {
                return this.http.get<ServiceTemplateInstanceResources>(resp._links['instances'].href, this.headerAcceptJSON);
            }),
            concatMap((resp) => {
                return this.http.get<ServiceTemplateInstance>(
                        resp.service_template_instances.find((instance) => {
                            return instance.id.toString() === serviceTemplateInstanceId;
                        })._links['self'].href, this.headerAcceptJSON);
                }
            )
        );
    }

    private getAllBuildPlanInputParameters(csarId: string): Observable<Array<InputParameter>> {
        return this.getBuildPlan(csarId).pipe(
            map((resp) => {
                return resp.input_parameters;
            })
        );
    }

    private getBuildPlan(csarId: string): Observable<Plan> {
        const result = this.getServiceTemplate(csarId).pipe(
            concatMap((resp) => {
                return this.http.get<PlanResources>(resp._links['buildplans'].href, this.headerAcceptJSON);
            }),
            map((resp) => {
                return resp.plans.find((plan) => {
                    return plan.plan_type === PlanTypes.BuildPlan;
                });
            })
        );
        return result;
    }

    private getBuildPlanInstance(csarId: string, correlationId: string): Observable<PlanInstance> {
        return this.getBuildPlan(csarId).pipe(
            concatMap((resp) => {
                return this.http.get<PlanInstanceResources>(resp._links['instances'].href, this.headerAcceptJSON);
            }),
            map((resp) => {
                return resp.plan_instances.find((planInstance) => {
                    return planInstance.correlation_id.toString() === correlationId;
                });
            })
        );
    }

    private getManagementPlans(csarId: string, serviceTemplateInstanceId: string): Observable<Array<Plan>> {
        return this.getServiceTemplateInstance(csarId, serviceTemplateInstanceId).pipe(
            concatMap((resp) => {
                return this.http.get<PlanResources>(resp._links['managementplans'].href, this.headerAcceptJSON);
            }),
            map((resp) => {
                return resp.plans;
            })
        );
    }

    private getManagementPlan(csarId: string, serviceTemplateInstanceId: string, planId: string): Observable<Plan> {
        return this.getManagementPlans(csarId, serviceTemplateInstanceId).pipe(
            map((resp) => {
                return resp.find((plan) => {
                    return plan.id.toString() === planId;
                });
            })
        );
    }

    private getTerminationPlan(csarId: string, serviceTemplateInstanceId: string): Observable<Plan> {
        return this.getManagementPlans(csarId, serviceTemplateInstanceId).pipe(
            map((resp) => {
                return resp.find((plan) => {
                    return plan.plan_type === PlanTypes.TerminationPlan;
                });
            })
        );
    }

    private combineURLs(baseURL: string, relativeURL: string) {
        return relativeURL
            ? baseURL.replace(/\/+$/, '') + '/' + relativeURL.replace(/^\/+/, '')
            : baseURL;
    }

    private stripCsarSuffix(csarId: string) {
        const csarEnding = '.csar';
        return csarId.endsWith(csarEnding) ? csarId.slice(0, -csarEnding.length) : csarId;
    }
}
