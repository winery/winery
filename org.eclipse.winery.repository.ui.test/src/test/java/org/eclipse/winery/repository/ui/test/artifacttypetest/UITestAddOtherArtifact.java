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
 * this test adds an ArtifactType with a short documentation
 *
 */

public class UITestAddOtherArtifact extends TestSettings {

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
	public void testAddOtherArtifact() throws Exception {
		//Add artifact type
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Types')]")).click();
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("ArtifactTypeTest");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/capabilitytypes");
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();
		driver.findElement(By.xpath("//div[@class='subMenu']//a//div[@class='center'][contains(text(), 'Documentation')]")).click();
		
		//add documentation
		WebElement element = driver.findElement(By.xpath("//textarea[@class='texDoc']"));
		element.sendKeys("Hallo, Ich bin eine Dokumentation!");
		element.click();
		Assert.assertFalse("Can't add same NodeName", element.isEnabled());
	}
}
