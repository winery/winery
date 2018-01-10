package org.eclipse.winery.repository.ui.test.nodetypetest;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 *
 * This test add a Requieremnt to a NodeType
 */

public class UITestNodeAddRequirement extends TestSettings {

	@Test
	public void testNodeType() throws Exception {
		driver.get("http://localhost:4200/#/nodetypes");
		driver.findElement(By.xpath("//div[@class='center']//div[@class='informationContainer']//div[@class='name']")).click();
		//Add Requirement Definition RequirementTypeWithOneKvProperty
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Requirement Definition')]")).click();
		driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		driver.findElement(By.xpath("//input[@id='key']")).sendKeys("requiredType");
		driver.findElement(By.xpath("//div[@class='ui-select-match']//span[@class='btn btn-default btn-secondary form-control ui-select-toggle']")).click();
		driver.findElement(By.xpath("//a[@class='dropdown-item']//div[contains(text(), 'NodeTypeWithTwoKvProperty')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();
		Thread.sleep(1000);
		Assert.assertTrue(true);
	}
}
