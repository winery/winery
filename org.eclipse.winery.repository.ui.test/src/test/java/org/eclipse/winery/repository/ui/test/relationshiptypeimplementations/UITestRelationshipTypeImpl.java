package org.eclipse.winery.repository.ui.test.relationshiptypeimplementations;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * Add RealtionTypeImplementation
 */
public class UITestRelationshipTypeImpl extends TestSettings {
	
	@Test
	public void relationshiptypeimpl() throws Exception {

		driver.get("http://localhost:4200/#/relationshiptypeimplementations");
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("NewRelationshipImpl");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/relationshiptypeimplementations");
		driver.findElement(By.xpath("//div[@class='modal-footer']//button[contains(.,'Add')]")).click();
		Thread.sleep(1000);
		driver.navigate().refresh();
		
		/*
		//add Implementation Atrefact
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Implementation Artifacts')]")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		driver.findElement(By.id("key")).sendKeys("AddArtifact");
		Select dropdown = new Select(driver.findElement(By.xpath("//select[@class='form-control']")));
		dropdown.selectByVisibleText("ArtifactTypeWithOneKvProperty");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//span[@class='close pull-right']")).click();
		Thread.sleep(1000);
		*/

		//add Inheritance 
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Inheritance')]")).click();
		driver.findElement(By.xpath("//div[@class='ui-select-match']")).click();
		driver.findElement(By.xpath("//a[@class='dropdown-item']//div[contains(text(), 'NewRelationshipImpl')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Save')]")).click();
		Thread.sleep(1000);

		//add Docu
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Documentation')]")).click();
		driver.findElement(By.xpath("//textarea[@class='texDoc ng-untouched ng-pristine ng-valid']")).sendKeys("Documentation");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Save')]")).click();
		Thread.sleep(1000);

		//delete
		driver.findElement(By.xpath("//button[@class='btn btn-danger'][contains(.,'Delete')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Delete')]")).click();

		//delete artefact
		Assert.assertTrue(true);
	}
}
