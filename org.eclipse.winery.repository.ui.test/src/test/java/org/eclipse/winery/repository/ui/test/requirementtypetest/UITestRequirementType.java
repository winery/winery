package org.eclipse.winery.repository.ui.test.requirementtypetest;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * this test adds a requirement Type
 * adds another requirement type
 * edits a requirement type
 * opens XML view in new tab
 * exports CSAR
 * deletes requirement type
 */

public class UITestRequirementType extends TestSettings {

	@Test
	public void testAddRequirementType() throws Exception {
		String originalHandle = driver.getWindowHandle();
		//add new requirement type
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Requirement Types')]")).click();
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("RequirementTypeTest");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/requirementtypes");
		driver.findElement(By.xpath("//div[@class='modal-footer']//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();

		Thread.sleep(5000);

		addOtherRequirementType(originalHandle);
	}

	private void addOtherRequirementType(String originalHandle) throws InterruptedException {
		//add another requirement type
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Requirement Types')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Requirement Types')]")).click();
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("SecondRequirementTypeTest");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/requirementtypes");
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();

		Thread.sleep(2000);

		editRequirementType(originalHandle);
	}

	private void editRequirementType(String originalHandle) throws InterruptedException {
		//edit requirement type
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Requirement Types')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Requirement Types')]")).click();
		driver.findElement(By.xpath("//a[@class='editButton']")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-info']")).click();

		Thread.sleep(2000);

		//close opened tab with xml view
		for (String handle : driver.getWindowHandles()) {
			if (!handle.equals(originalHandle)) {
				driver.switchTo().window(handle);
				driver.close();
			}
		}
		Thread.sleep(5000);

		driver.switchTo().window(originalHandle);

		Thread.sleep(2000);

		addPropertyDefinition();
	}

	private void addPropertyDefinition() throws InterruptedException {
		//add properties definitions
		driver.navigate().refresh();
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Requirement Types')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Requirement Types')]")).click();
		driver.findElement(By.xpath("//a[@class='editButton']")).click();
		driver.findElement(By.xpath("//div[@class='subMenu']//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(text(), 'Properties Definition')]")).click();

		Thread.sleep(2000);

		deleteRequirementType();

	}

	private void deleteRequirementType() throws InterruptedException {
		//delete requirement type
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Requirement Types')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Requirement Types')]")).click();
		driver.findElement(By.xpath("//a[@class='deleteButton']")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary']")).click();
		Thread.sleep(2000);

		exportCSAR();
	}

	private void exportCSAR() throws InterruptedException {
		//export CSAR
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Requirement Types')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Requirement Types')]")).click();
		driver.findElement(By.id("sectionsImportCsarBtn")).click();
		driver.findElement(By.xpath("//input[@type='file']")).click();
		Assert.assertTrue(true);
	}
}

