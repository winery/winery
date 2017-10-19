package org.eclipse.winery.repository.ui.test.nodetypetest;

import org.eclipse.winery.repository.ui.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;


/**
 * this test add a NodeType with all components and delete it after this
 * Rename Node
 * Add docu
 */

public class UITestNodeTypes extends TestSettings {

	@Test
	public void testNodeType() throws Exception {
		driver.get("http://localhost:4200/#/nodetypes");
		driver.findElement(By.xpath("//div[@class='center']//div[@class='informationContainer']//div[@class='name']")).click();
		//Rename Node
		WebElement element = driver.findElement(By.id("renamePropertyInput"));
		element.sendKeys(Keys.chord(Keys.CONTROL, "a"), "55");
		element.sendKeys("NewName");
		driver.findElement(By.xpath("//button[@class='btn btn-primary btn-xs'][contains(text(), 'Save')]")).click();
		Thread.sleep(1000);
		//addInterface();

		//Add Documentation
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Documentation')]")).click();
		driver.findElement(By.xpath("//textarea[@class='texDoc ng-untouched ng-pristine ng-valid']")).sendKeys("Documentation");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Save')]")).click();
		Thread.sleep(1000);
		Assert.assertTrue(true);
	}
	
	private void addInterface() {
		/**
		 //Add Interface
		 driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Interface')]")).click();
		 driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		 driver.findElement(By.xpath("//input[@id='interfaceName']")).sendKeys("addInterface");
		 thread.sleep(1000);
		 driver.findElement(By.xpath("//div[@class='modal-footer']//button[@class='btn btn-primary'][contains(.,'Add')]")).click();
		 driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		 driver.findElement(By.xpath("//input[@id='interfaceName']")).sendKeys("addInterface");
		 driver.findElement(By.xpath("//button[@id='addIfBtn']")).click();
		 driver.findElement(By.xpath("//input[@id='interfaceName']")).sendKeys("add");
		 thread.sleep(1000);
		 driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Add')]")).click();
		 driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Save')]")).click();
		 thread.sleep(1000);
		 **/
	}
}
