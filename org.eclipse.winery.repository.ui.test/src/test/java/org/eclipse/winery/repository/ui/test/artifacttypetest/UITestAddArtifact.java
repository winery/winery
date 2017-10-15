package org.eclipse.winery.repository.ui.test.artifacttypetest;

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
 * this test adds an ArtifactType and then add another with the same name
 *
 */

public class UITestAddArtifact extends TestSettings {

	private static FirefoxDriver driver;


	@BeforeClass
	public static void init() throws Exception {
		settings();
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(10, SECONDS);
	}

	@AfterClass
	public static void shutdown() throws Exception {
		//driver.close();
	}

	@Test
	public void testAddArtifact() throws Exception {
		//Add new artifact type
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Types')]")).click();
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("Hallo");
		driver.findElement(By.id("namespace")).sendKeys("Hallo");
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();

		//add another artifact type with same name 
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Types')]")).click();
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("Hallo");
		driver.findElement(By.id("namespace")).sendKeys("Hallo");

		WebElement element = driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]"));
		element.click();
		Assert.assertFalse("Can't add same NodeName", element.isEnabled());
	}
}
