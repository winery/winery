package org.eclipse.winery.repository.ui.test.relationshiptypes;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * Add RealtionshipType:
 * - add Implementation
 * - add State
 * - add Interitance
 */

public class UITestAddRelationshipTypes extends TestSettings {

	@Test
	public void testRelationshipTypes() throws Exception {

		driver.get("http://localhost:4200/#/relationshiptypes");
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("Add Relationship Type");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/relationshiptype");
		driver.findElement(By.xpath("//div[@class='modal-footer']//button[contains(.,'Add')]")).click();
		driver.navigate().refresh();

		//Add State
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Instance States')]")).click();
		driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		driver.findElement(By.xpath("//input[@id='state']")).sendKeys("AddState");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Add')]")).click();
		Thread.sleep(1000);

		//Add Implementations
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Implementations')]")).click();
		Thread.sleep(10);
		driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		driver.findElement(By.xpath("//input[@id='localname']")).sendKeys("ImplName");
		driver.findElement(By.xpath("//input[@id='namespace']")).sendKeys("http://plain.opentosca.org/test/relationshiptypeimplementations");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();
		Thread.sleep(1000);

		addProperty();
		addInheritance();
		addDocu();
		deleteAll();
		Assert.assertTrue(true);

	}

	private void deleteAll() throws InterruptedException {
		//Delete NodeType
		driver.findElement(By.xpath("//button[@class='btn btn-danger'][contains(.,'Delete')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Delete')]")).click();

		//Delete Implementation
		driver.get("http://localhost:4200/#/other");
		driver.navigate().refresh();
		driver.findElement(By.xpath("//a[contains(.,'Relationship Type Implementations')]")).click();
		driver.findElement(By.xpath("//div[contains(text(), 'ImplName')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-danger'][contains(.,'Delete')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Delete')]")).click();
		Assert.assertTrue(true);
	}

	private void addInheritance() throws InterruptedException {

		//Add Inheritance RelationshipTypeTypeWithOnKVProperty
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Inheritance')]")).click();
		//Select select = (Select) driver.findElement(By.xpath("//span[@class='ui-select-match-text pull-left']"));
		driver.findElement(By.xpath("//div[@class='ui-select-match']")).click();
		driver.findElement(By.xpath("//a[@class='dropdown-item']//div[contains(text(), 'RelationshipTypeWithOneKvProperty')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Save')]")).click();
		Thread.sleep(1000);

	}

	private void addDocu() throws InterruptedException {
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Documentation')]")).click();
		driver.findElement(By.xpath("//textarea[@class='texDoc ng-untouched ng-pristine ng-valid']")).sendKeys("Documentation");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Save')]")).click();
		Thread.sleep(1000);

	}

	private void addProperty() throws InterruptedException {
		//Add Property
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Properties Definition')]")).click();
		driver.findElement(By.xpath("//input[@id='customkv'][@type='radio']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		driver.findElement(By.xpath("//input[@id='key']")).sendKeys("AddProperty");
		//	@TODO: Change value 
		Thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();
		Thread.sleep(1000);
	}
}
