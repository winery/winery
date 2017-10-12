package org.eclipse.winery.repository.ui.test.nodetypetest;

import org.eclipse.winery.repository.ui.test.TestSettings;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 
 * This test add a NodeType name NewNode and try to add the same again
 * 
 */

public class UITestAddNodes extends TestSettings {
	
	private static  FirefoxDriver driver;

	@BeforeClass
	public static void init() throws Exception {
		settings();
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(10, SECONDS);
	}

	@AfterClass
	public static void shutdown() throws Exception {
		driver.close();
	}

	@Test
	public void testNodeType() throws Exception {
		// Add NodeType
		driver.get("http://localhost:4200/#/nodetypes");
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("NewNode");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/nodetypes");
		driver.findElement(By.xpath("//div[@class='modal-footer']//button[contains(.,'Add')]")).click();

		driver.get("http://localhost:4200/#/nodetypes");
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("NewNode");
		driver.findElement(By.id("namespace")).sendKeys("NodeTypeNamespace");
		WebElement element = driver.findElement(By.xpath("//div[@class='modal-footer']//button[contains(.,'Add')]"));
		Assert.assertFalse("Can add same NodeName", element.isEnabled());
		//driver.findElement(By.xpath("//div[@class='modal-footer']//button[contains(.,'Cancel')][@class='btn btn-default']")).click();
		
		//driver.findElement(By.xpath("//div[@class='entityContainer serviceTemplate']//id[contains(.,'NewNode')]")).click();
		//driver.findElement(By.xpath("//button[@class='btn btn-danger'][contains(.,'Delete')]")).click();
		//driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Delete')]")).click();
	}
}
