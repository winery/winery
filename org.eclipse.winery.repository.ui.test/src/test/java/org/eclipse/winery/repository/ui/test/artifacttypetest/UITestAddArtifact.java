package org.eclipse.winery.repository.ui.test.artifacttypetest;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * this test adds an ArtifactType and then add another with the same name
 */

public class UITestAddArtifact extends TestSettings {
	
	@Test
	public void testAddArtifact() throws Exception {
		Thread thread = new Thread();

		//Add new artifact type
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Types')]")).click();
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("Hallo");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/capabilitytypes");
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();

		thread.sleep(2000);

		//add another artifact type with same name 
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Artifact Types')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Types')]")).click();
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("Hallo");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/artifacttypes");

		WebElement element = driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]"));
		Assert.assertFalse("Can't add same NodeName", element.isEnabled());
	}
}
