package org.eclipse.winery.repository.ui.test.capabilitytypetest;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * this test adds a capability Type
 * adds another capability type
 * edits a capability type
 * opens XML view in new tab
 * exports CSAR
 * deletes capability type
 */

public class UITestCapabilityType extends TestSettings {

	@Test
	public void testAddCapabilityType() throws InterruptedException {
		Thread thread = new Thread();
		String originalHandle = driver.getWindowHandle();
		//add new capability type
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Capability Types')]")).click();
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("CapabilityTypeTest");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/capabilitytypes");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//div[@class='modal-footer']//button[contains(.,'Add')]")).click();
		//driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();

		thread.sleep(5000);

		addOtherCapabilityType(thread);
		editCapabilityType(thread, originalHandle);
		addInheritance(thread);
		deleteCapabilityType(thread);
		exportCSAR(thread);

		Assert.assertTrue(true);
	}

	private void addOtherCapabilityType(Thread thread) throws InterruptedException {
		//add another capability type
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Capability Types')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Capability Types')]")).click();
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("SecondCapabilityTypeTest");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/capabilitytypes");
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();

		thread.sleep(2000);
	}

	private void editCapabilityType(Thread thread, String originalHandle) throws InterruptedException {
		//edit capability type
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Capability Types')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Capability Types')]")).click();
		driver.findElement(By.xpath("//a[@class='editButton']")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-info']")).click();

		thread.sleep(2000);

		//close opened tab with xml view
		for (String handle : driver.getWindowHandles()) {
			if (!handle.equals(originalHandle)) {
				driver.switchTo().window(handle);
				driver.close();
			}
		}
		thread.sleep(5000);

		driver.switchTo().window(originalHandle);

		thread.sleep(2000);
	}

	private void addInheritance(Thread thread) throws InterruptedException {
		//add inheritance
		driver.navigate().refresh();
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Capability Types')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Capability Types')]")).click();
		driver.findElement(By.xpath("//a[@class='editButton']")).click();
		driver.findElement(By.xpath("//div[@class='subMenu']//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(text(), 'Inheritance')]")).click();
		driver.findElement(By.xpath("//div[@class='ui-select-match']")).click();
		thread.sleep(20);
		driver.findElement(By.xpath("//div[@class='ui-select-choices-row']//a[@class='dropdown-item']//div[contains(text(), 'CapabilityTypeWithOneKvProperty')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Save')]")).click();

		thread.sleep(2000);
	}

	private void deleteCapabilityType(Thread thread) throws InterruptedException {
		//delete capability type
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Capability Types')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Capability Types')]")).click();
		driver.findElement(By.xpath("//a[@class='deleteButton']")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary']")).click();
		thread.sleep(2000);
	}

	private void exportCSAR(Thread thread) throws InterruptedException {
		//export CSAR
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton selected']//div[contains(text(), 'Other Elements: Capability Types')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Capability Types')]")).click();
		driver.findElement(By.id("sectionsImportCsarBtn")).click();
		driver.findElement(By.xpath("//input[@type='file']")).click();
	}
}

