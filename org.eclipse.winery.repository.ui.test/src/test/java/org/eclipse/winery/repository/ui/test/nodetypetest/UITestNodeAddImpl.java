package org.eclipse.winery.repository.ui.test.nodetypetest;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

/*
	This test add to an existing Nodetype an Implementation and delete both

 */

public class UITestNodeAddImpl extends TestSettings {

	@Test
	public void testNodeType() throws Exception {
		driver.get("http://localhost:4200/#/nodetypes");
		driver.findElement(By.xpath("//div[@class='center']//div[@class='informationContainer']//div[@class='name']")).click();
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Implementations')]")).click();
		Thread.sleep(10);
		driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		driver.findElement(By.xpath("//input[@id='localname']")).sendKeys("ImplName");
		driver.findElement(By.xpath("//input[@id='namespace']")).sendKeys("http://plain.opentosca.org/test/nodetypeimplementations");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();
		Thread.sleep(1000);

		//Delete NodeType
		driver.findElement(By.xpath("//button[@class='btn btn-danger'][contains(.,'Delete')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Delete')]")).click();

		//Delete Implementation
		driver.get("http://localhost:4200/#/other");
		driver.navigate().refresh();
		driver.findElement(By.xpath("//a[contains(.,'Node Type Implementations')]")).click();
		driver.findElement(By.xpath("//div[contains(text(), 'ImplName')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-danger'][contains(.,'Delete')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Delete')]")).click();
		Assert.assertTrue(true);
	}





	

}
