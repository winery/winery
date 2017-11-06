package org.eclipse.winery.repository.datatypes.ids.elements;

import org.eclipse.winery.common.ids.IdNames;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;

public class ServiceTemplateSelfServiceFilesDirectoryId extends DirectoryId {

	public ServiceTemplateSelfServiceFilesDirectoryId(ServiceTemplateId id) {
		super(id, IdNames.SELF_SERVICE_PORTAL_FILES, true);
	}
}
