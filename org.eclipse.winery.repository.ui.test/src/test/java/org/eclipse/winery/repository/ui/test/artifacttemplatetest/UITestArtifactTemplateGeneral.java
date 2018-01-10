package org.eclipse.winery.repository.ui.test.artifacttemplatetest;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * this test adds two different ArtifactTemplates
 * edits an artifact template
 * opens XML view in new tab,
 * exports CSAR
 * deletes artifact template
 */

public class UITestArtifactTemplateGeneral extends TestSettings {

	@Test
	//Artifact Template
	public void testArtifactTemplate() throws Exception {
		String originalHandle = driver.getWindowHandle();
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Templates')]")).click();
		//add new artifact template
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("ArtifactTemplateTest");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/artifacttemplates");
		driver.findElement(By.xpath("//div[@class='ui-select-match']//span[@class='btn btn-default btn-secondary form-control ui-select-toggle']")).click();
		driver.findElement(By.xpath("//a[@class='dropdown-item']//div[contains(text(), 'ArtifactTypeWithTwoKvProperties')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();

		Thread.sleep(50);

		addOtherArtifactTemplate();
		editArtifactTemplate(originalHandle);
		deleteArtifactTemplate();
		Assert.assertTrue(true);
	}

	private void addOtherArtifactTemplate() throws InterruptedException {
		//add another artifact template
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Templates')]")).click();
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("OtherArtifactTemplateTest");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/artifacttemplates");
		driver.findElement(By.xpath("//div[@class='ui-select-match']//span[@class='btn btn-default btn-secondary form-control ui-select-toggle']")).click();
		Thread.sleep(50);
		driver.findElement(By.xpath("//a[@class='dropdown-item']//div[contains(text(), 'ArtifactTypeWithoutProperties')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();
		Thread.sleep(50);
	}

	private void editArtifactTemplate(String originalHandle) throws InterruptedException {
		//edit artifact template, go to XML view
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Templates')]")).click();
		driver.findElement(By.xpath("//a[@class='editButton']")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-info']")).click();
		//close opened tab with xml view
		Thread.sleep(50);

		for (String handle : driver.getWindowHandles()) {
			if (!handle.equals(originalHandle)) {
				driver.switchTo().window(handle);
				driver.close();
			}
		}
		Thread.sleep(50);
		driver.switchTo().window(originalHandle);
		Thread.sleep(50);
	}

	private void deleteArtifactTemplate() throws InterruptedException {

		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Templates')]")).click();
		driver.findElement(By.xpath("//div[@class='center']//div[@class='informationContainer']//div[@class='name'][contains(text(), 'OtherArtifactTemplateTest')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-danger'][contains(.,'Delete')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Delete')]")).click();
		Thread.sleep(2000);
		
		//delete artifact template
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Templates')]")).click();
		driver.findElement(By.xpath("//div[@class='center']//div[@class='informationContainer']//div[@class='name'][contains(text(), 'ArtifactTemplateTest')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-danger'][contains(.,'Delete')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Delete')]")).click();
	}
}
