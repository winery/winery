package org.eclipse.winery.repository.ui.test.artifacttemplatetest;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * this test tests the search field in ArtifactTemplate area
 */

public class UITestSearchField extends TestSettings {
	
	@Test
	public void testSearchField() throws Exception {
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Types')]")).click();

		WebElement element = driver.findElement(By.xpath("//input[@id='searchBox']"));
		element.sendKeys("Hallo");
		Assert.assertTrue("Search Field works!", element.isEnabled());
	}
}
