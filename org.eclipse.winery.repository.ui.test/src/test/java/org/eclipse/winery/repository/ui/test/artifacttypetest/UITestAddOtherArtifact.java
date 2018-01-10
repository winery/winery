package org.eclipse.winery.repository.ui.test.artifacttypetest;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * this test adds an ArtifactType with a short documentation
 */

public class UITestAddOtherArtifact extends TestSettings {
	
	@Test
	public void testAddOtherArtifact() throws Exception {
		//Add artifact type
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Types')]")).click();
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("ArtifactTypeTest");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/artifacttypes");
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();
		driver.navigate().refresh();
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Documentation')]")).click();
		//driver.findElement(By.xpath("//div[@class='subMenu']//a//div[@class='center'][contains(text(), 'Documentation')]")).click();
		driver.findElement(By.xpath("//div[@class='documentationField']//textarea[@class='texDoc ng-untouched ng-pristine ng-valid']")).sendKeys("Hallo, Ich bin eine Dokumentation!");
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Save')]")).click();
		Thread.sleep(1000);
		//Delete 
		driver.findElement(By.xpath("//button[@class='btn btn-danger'][contains(.,'Delete')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Delete')]")).click();
		Assert.assertTrue(true);
	}
}
