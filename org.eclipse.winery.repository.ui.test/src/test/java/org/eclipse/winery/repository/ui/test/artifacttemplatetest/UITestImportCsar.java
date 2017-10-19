package org.eclipse.winery.repository.ui.test.artifacttemplatetest;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * this test imports a CSAR
 */

public class UITestImportCsar extends TestSettings {
	
	@Test
	//check if CSAR import works, e.g. by adding a .txt file 
	public void testImportCSAR() throws Exception {
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Templates')]")).click();
		driver.findElement(By.id("sectionsImportCsarBtn")).click();

		WebElement element = driver.findElement(By.xpath("//input[@type='file']"));
		element.click();
		Assert.assertTrue("Import CSAR is successful", element.isEnabled());
	}
}
