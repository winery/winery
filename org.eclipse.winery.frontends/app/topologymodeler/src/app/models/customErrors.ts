/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

export abstract class LiveModelingError extends Error {
    protected constructor() {
        super();
        Object.setPrototypeOf(this, LiveModelingError.prototype);
    }
}

export class CreateLiveModelingTemplateError extends LiveModelingError {
    constructor() {
        super();
        Object.setPrototypeOf(this, CreateLiveModelingTemplateError.prototype);
        this.message = 'There was an error while creating the temporary service template';
    }
}

export class UploadCsarError extends LiveModelingError {
    constructor() {
        super();
        Object.setPrototypeOf(this, UploadCsarError.prototype);
        this.message = 'There was an error while uploading the csar to the container';
    }
}

export class RetrieveInputParametersError extends LiveModelingError {
    constructor() {
        super();
        Object.setPrototypeOf(this, RetrieveInputParametersError.prototype);
        this.message = 'There was an error while retrieving the input plan parameters';
    }
}

export class DeployInstanceError extends LiveModelingError {
    constructor() {
        super();
        Object.setPrototypeOf(this, DeployInstanceError.prototype);
        this.message = 'There was an error while deploying the service template instance';
    }
}

export class TransformInstanceError extends LiveModelingError {
    constructor() {
        super();
        Object.setPrototypeOf(this, TransformInstanceError.prototype);
        this.message = 'There was an error while transforming the service template instance';
    }
}

export class AdaptInstanceError extends LiveModelingError {
    constructor() {
        super();
        Object.setPrototypeOf(this, AdaptInstanceError.prototype);
        this.message = 'There was an error while adapting the service template instance';
    }
}

export class TerminateInstanceError extends LiveModelingError {
    constructor() {
        super();
        Object.setPrototypeOf(this, TerminateInstanceError.prototype);
        this.message = 'There was an error while terminating the service template instance';
    }
}

export class ServiceTemplateInstanceError extends LiveModelingError {
    constructor() {
        super();
        Object.setPrototypeOf(this, ServiceTemplateInstanceError.prototype);
        this.message = 'The service template instance encountered an error';
    }
}

export class NodeTemplateInstanceError extends LiveModelingError {
    constructor() {
        super();
        Object.setPrototypeOf(this, NodeTemplateInstanceError.prototype);
        this.message = 'The node template instance encountered an error';
    }
}

export class TimeoutError extends LiveModelingError {
    constructor() {
        super();
        Object.setPrototypeOf(this, TimeoutError.prototype);
        this.message = 'The operation has timed out';
    }
}

export class UnauthorizedActionError extends LiveModelingError {
    constructor() {
        super();
        Object.setPrototypeOf(this, UnauthorizedActionError.prototype);
        this.message = 'You are currently not allowed to perform this action';
    }
}
