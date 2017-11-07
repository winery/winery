package org.eclipse.winery.repository.rest.resources.servicetemplates.selfserviceportal;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.Test;

public class FilesTest extends AbstractResourceTest {

	@Test
	public void addFileTest() throws Exception {
		this.setRevisionTo("770b3b6b86620fac4c8ff1947e84e872e9dcd970");
		this.assertNoContentPost("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateWithFourPolicies/selfserviceportal/files/text.txt",
			"servicetemplates/selfserviceportal/addFirstFile.json");
		this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateWithFourPolicies/selfserviceportal/files",
			"servicetemplates/selfserviceportal/firstFileInList.json");
	}

	@Test
	public void getFileTest() throws Exception {
		this.setRevisionTo("d2fe6f518d732d9f93bcd8e4053c089a4eab4c96");
		this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateWithOneFileInSelfServicePortal/selfserviceportal/files",
			"servicetemplates/selfserviceportal/oneFileInList.json");
	}

	@Test
	public void deleteFileTest() throws Exception {
		this.setRevisionTo("d2fe6f518d732d9f93bcd8e4053c089a4eab4c96");
		this.assertDelete("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateWithOneFileInSelfServicePortal/selfserviceportal/files/myFirstFile.txt?path=");
	}
}
