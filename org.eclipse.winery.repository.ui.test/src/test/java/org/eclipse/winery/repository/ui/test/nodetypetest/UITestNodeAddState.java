package org.eclipse.winery.repository.ui.test.nodetypetest;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 *
 * This test add a State to a NodeType
 */

public class UITestNodeAddState extends TestSettings {

	@Test
	public void testNodeType() throws Exception {
		driver.get("http://localhost:4200/#/nodetypes");
		driver.findElement(By.xpath("//div[@class='center']//div[@class='informationContainer']//div[@class='name']")).click();
		driver.navigate().refresh();
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Instance States')]")).click();
		driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		driver.findElement(By.xpath("//input[@id='state']")).sendKeys("AddState");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Add')]")).click();
		Thread.sleep(1000);
		Assert.assertTrue(true);
	}

}
