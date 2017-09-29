/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { ToscaTypes } from './enums';

export interface SectionResolverData {
    section: ToscaTypes;
    namespace: string;
    path: string;
    xsdSchemaType: string;
}

export interface InstanceResolverData extends SectionResolverData {
    localName: string;
}

export interface PropertiesDefinitionResolverData {
    propertiesSelected: boolean;
    wrappingSelected: boolean;
}
