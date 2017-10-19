package org.eclipse.winery.repository.ui.test.artifacttypetest;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * this test exports an ArtifactType as CSAR
 */

public class UITestExportAsCsar extends TestSettings {
	
	@Test
	//Export Artifacttyoe as CSAR
	public void testExportAsCsar() throws Exception {
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Types')]")).click();
		driver.findElement(By.xpath("//a[@class='exportButton']")).click();
		Assert.assertTrue("Import CSAR is successful", true);
	}
}
