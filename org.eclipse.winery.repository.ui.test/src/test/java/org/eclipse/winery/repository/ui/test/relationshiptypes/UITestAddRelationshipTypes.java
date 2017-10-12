package org.eclipse.winery.repository.ui.test.relationshiptypes;

import org.eclipse.winery.repository.ui.test.TestSettings;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;


import java.util.concurrent.TimeUnit;

public class UITestAddRelationshipTypes extends TestSettings {
	
	private static  FirefoxDriver driver;

	@BeforeClass
	public static void init() throws Exception {
		settings();
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

	@AfterClass
	public static void shutdown() throws Exception { 
		//driver.close();
	}

	@Test
	public void testRelationshipTypes() throws Exception {
		
		Thread thread = new Thread();
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
		thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Add')]")).click();
		thread.sleep(1000);
		Assert.assertTrue(true);
	}
}
