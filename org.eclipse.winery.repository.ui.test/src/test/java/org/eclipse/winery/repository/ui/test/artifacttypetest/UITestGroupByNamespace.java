package org.eclipse.winery.repository.ui.test.artifacttypetest;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * this test groups ArtifactTypes by Namespaces
 */

public class UITestGroupByNamespace extends TestSettings {
	
	@Test
	//"Group by namespace" and change then to "show all items"
	public void testGroupByNamespace() throws Exception {
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Types')]")).click();
		driver.findElement(By.id("sectionsGroupBtn")).click();
		driver.findElement(By.id("sectionsGroupBtn")).click();
		WebElement element = driver.findElement(By.id("sectionsGroupBtn"));
		element.click();
		Assert.assertTrue("Import CSAR is successful", element.isEnabled());
	}
}
