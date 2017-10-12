package org.eclipse.winery.repository.ui.test;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import java.util.concurrent.TimeUnit;

public class UITestBaobab extends TestSettings {
	
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
	public void baobabtest() throws Exception {
		driver.get("http://localhost:4200/#/nodetypes");
		driver.findElement(By.xpath("//div[@class='entityContainer nodeType']//div[@class='center']//div[contains(text(), baobab)]")).click();
		driver.findElement(By.id("localname")).click();
		driver.findElement(By.id("renamePropertyInput")).sendKeys("RenameBaobab");
		driver.findElement(By.xpath("//button[@class='btn btn-primary btn-xs'][contains(text(), 'Save')]")).click();
		Assert.assertTrue(true);
	}
}

