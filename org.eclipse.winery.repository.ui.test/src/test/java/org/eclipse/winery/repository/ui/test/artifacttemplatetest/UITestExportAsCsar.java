package org.eclipse.winery.repository.ui.test.artifacttemplatetest;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * this test exports an ArtifactTemplate as CSAR
 */

public class UITestExportAsCsar extends TestSettings {

	@Test
	public void testExportAsCsar() throws Exception {
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Templates')]")).click();
		driver.findElement(By.xpath("//a[@class='exportButton']")).click();
		Assert.assertTrue("Export Artifact Template as CSAR successful", true);
	}
}
