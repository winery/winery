package org.eclipse.winery.repository.ui.test.servicetemplate;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * Add ServiceTemplate
 * add Selfsercie Portal
 * add Tags
 * add Docu
 */

public class UITestAddServiceTemplate extends TestSettings {
	
	@Test
	public void serviceTemplate() throws Exception {
		driver.get("http://localhost:4200/#/servicetemplates");
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("NewServiceTemplate");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org.servicetemplates");
		driver.findElement(By.xpath("//div[@class='modal-footer']//button[contains(.,'Add')]")).click();
		
		/* Add Plans 
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Plans')]")).click();
		driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		driver.findElement(By.xpath("//input[@id='key']")).sendKeys("addPlan");
		driver.findElement(By.id("planLanguage")).sendKeys("BPMN4TOSCA 2.0");
		//dropdown.selectByVisibleText("BPMN4TOSCA 2.0");
		*/
		addSelfService();
		//addTags(Thread);
		addDocu();
		deleteAll();

		Assert.assertTrue(true);
	}

	private void deleteAll() {
		//Delete
		driver.findElement(By.xpath("//button[@class='btn btn-danger'][contains(.,'Delete')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Delete')]")).click();
	}

	private void addDocu() throws InterruptedException {
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Documentation')]")).click();
		driver.findElement(By.xpath("//textarea[@class='texDoc ng-untouched ng-pristine ng-valid']")).sendKeys("Documentation");
		Thread.sleep(1000);
	}

	private void addTags() throws InterruptedException {
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Tags')]")).click();
		driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		driver.findElement(By.id("name")).sendKeys("addTag");
		driver.findElement(By.id("value")).sendKeys("addValue");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();
		Thread.sleep(1000);

	}

	private void addSelfService() throws InterruptedException {
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Selfservice Portal')]")).click();
		driver.findElement(By.id("applicationDescriptionDiv")).sendKeys("Text");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Save')]")).click();
		Thread.sleep(1000);
	}
}
