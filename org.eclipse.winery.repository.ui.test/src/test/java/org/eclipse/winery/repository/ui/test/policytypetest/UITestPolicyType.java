package org.eclipse.winery.repository.ui.test.policytypetest;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * this test adds a policy type
 * adds another policy type
 * edits a policy type
 * opens XML view in new tab
 * adds inheritance
 * exports CSAR
 * deletes policy type
 */

public class UITestPolicyType extends TestSettings {

	@Test
	public void testAddPolicyType() throws InterruptedException {
		Thread Thread = new Thread();
		String originalHandle = driver.getWindowHandle();
		//add new policy type
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Policy Types')]")).click();
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("PolicyTypeTest");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/policytypes");
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();

		Thread.sleep(2000);

		addOtherPolicyType();
		editPolicyType(originalHandle);
		addInheritance();
		deletePolicyType();
		exportCSAR();

		Assert.assertTrue(true);
	}

	private void addOtherPolicyType() throws InterruptedException {
		//add another policy type
		driver.get("http://localhost:4200/#/other");
		//driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Policy Types')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Policy Types')]")).click();
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("SecondPolicyTypeTest");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/policytypes");
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();

		Thread.sleep(2000);
	}

	private void editPolicyType(String originalHandle) throws InterruptedException {
		//edit policy type
		driver.get("http://localhost:4200/#/other");
		//driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Policy Types')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Policy Types')]")).click();
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
	}

	private void addInheritance() throws InterruptedException {
		//add inheritance
		driver.navigate().refresh();
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Policy Types')]")).click();
	//	driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Policy Types')]")).click();
		driver.findElement(By.xpath("//a[@class='editButton']")).click();
		driver.findElement(By.xpath("//div[@class='subMenu']//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(text(), 'Inheritance')]")).click();
		driver.findElement(By.xpath("//div[@class='ui-select-match']")).click();
		driver.findElement(By.xpath("//a[@class='dropdown-item']//div[contains(text(), 'PolicyTypeTest')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Save')]")).click();

		Thread.sleep(2000);
	}

	private void deletePolicyType() throws InterruptedException {
		//delete policy type
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Policy Types')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Policy Types')]")).click();
		driver.findElement(By.xpath("//a[@class='deleteButton']")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary']")).click();
		Thread.sleep(2000);
	}

	private void exportCSAR() throws InterruptedException {
		//export CSAR
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Policy Types')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Policy Types')]")).click();
		driver.findElement(By.id("sectionsImportCsarBtn")).click();
		driver.findElement(By.xpath("//input[@type='file']")).click();
	}
}

