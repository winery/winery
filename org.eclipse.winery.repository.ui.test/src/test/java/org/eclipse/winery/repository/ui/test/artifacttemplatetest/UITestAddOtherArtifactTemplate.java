package org.eclipse.winery.repository.ui.test.artifacttemplatetest;

import javax.validation.constraints.AssertTrue;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * this test adds an ArtifactTemplate with a short documentation
 */

public class UITestAddOtherArtifactTemplate extends TestSettings {
	@Test
	//Add other artifact template
	public void testAddOtherArtifactTemplate() throws Exception {
		Thread thread = new Thread();

		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Templates')]")).click();
		//add new artifact template
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("Hallo");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/artifacttemplates");
		driver.findElement(By.xpath("//div[@class='ui-select-match']//span[@class='btn btn-default btn-secondary form-control ui-select-toggle']")).click();
		driver.findElement(By.xpath("//a[@class='dropdown-item']//div[contains(text(), 'ArtifactTypeWithoutProperties')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();

		thread.sleep(2000);

		//add documentation
		driver.findElement(By.xpath("//div[@class='subMenu']//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(text(), 'Documentation')]")).click();
		WebElement element = driver.findElement(By.xpath("//div[@class='documentationField']//textarea[@class='texDoc ng-untouched ng-pristine ng-valid']"));
		element.click();
		element.sendKeys("Hallo, Ich bin eine Dokumentation!");
		driver.findElement(By.xpath("//div[@class='floatButton']//button[contains(text(), 'Save')]")).click();
		Assert.assertTrue(true);
	}
}

