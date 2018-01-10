package org.eclipse.winery.repository.ui.test.policytemplatetest;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * this test adds a policy template
 * adds another policy template
 * edits a policy template
 * opens XML view in new tab
 * exports CSAR
 * deletes policy template
 */

public class UITestPolicyTemplate extends TestSettings {
	
	@Test
	public void testAddAPolicyTemplate() throws InterruptedException {
		
		String originalHandle = driver.getWindowHandle();
		//add new policy type
		driver.get("http://localhost:4200/#/policytemplates");
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("PolicyTypeTest");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/policytemplates");
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();

		Thread.sleep(2000);

		addOtherPolicyTemplate();
		editPolicyTemplate(originalHandle);
		deletePolicyTemplate();
		exportCSAR();
		Assert.assertTrue(true);
	}

	private void addOtherPolicyTemplate() throws InterruptedException {
		//add another policy type
		
		//driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Policy Templates')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Policy Templates')]")).click();
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("SecondPolicyTypeTest");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/policytemplates");
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();

		Thread.sleep(2000);
	}

	private void editPolicyTemplate(String originalHandle) throws InterruptedException {
		//edit policy template
		//driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Policy Templates')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Policy Templates')]")).click();
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

	private void deletePolicyTemplate() throws InterruptedException {
		//delete policy template
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Policy Templates')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Policy Templates')]")).click();
		driver.findElement(By.xpath("//a[@class='deleteButton']")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary']")).click();
		Thread.sleep(2000);
	}

	private void exportCSAR() throws InterruptedException {
		//export CSAR
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Policy Templates')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Policy Templates')]")).click();
		driver.findElement(By.id("sectionsImportCsarBtn")).click();
		driver.findElement(By.xpath("//input[@type='file']")).click();
	}
}

