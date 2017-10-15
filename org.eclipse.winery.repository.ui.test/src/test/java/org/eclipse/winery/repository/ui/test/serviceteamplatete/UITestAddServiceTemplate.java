package org.eclipse.winery.repository.ui.test.serviceteamplatete;

import org.eclipse.winery.repository.ui.test.TestSettings;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

public class UITestAddServiceTemplate extends TestSettings {
	
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
	public void serviceTemplate() throws Exception {
		driver.get("http://localhost:4200/#/servicetemplates");
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("NewServiceTemplate");
		driver.findElement(By.id("namespace")).sendKeys("ServiceTemplateNamespace");
		driver.findElement(By.xpath("//div[@class='modal-footer']//button[contains(.,'Add')]")).click();
		Assert.assertTrue(true);
	}
}
