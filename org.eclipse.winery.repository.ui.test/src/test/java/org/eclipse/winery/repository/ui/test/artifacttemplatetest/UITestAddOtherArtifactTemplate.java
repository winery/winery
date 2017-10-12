package org.eclipse.winery.repository.ui.test.artifacttemplatetest;

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
 * this test adds an ArtifactTemplate with a short documentation
 *
 */

public class UITestAddOtherArtifactTemplate extends TestSettings {

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
	//Add other artifact template
	public void testAddOtherArtifactTemplate() throws Exception {

		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Templates')]")).click();
		//add new artifact template
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("Hallo");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/relationshiptype");
		driver.findElement(By.xpath("//div[@class='ui-select-match']")).click();
		driver.findElement(By.xpath("//a[@class='dropdown-item']//div[contains(text(), 'ArtifactTypeWithOneKvProperty')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();

		//add documentation
		WebElement element = driver.findElement(By.xpath("//textarea[@class='texDoc']"));
		element.sendKeys("Hallo, Ich bin eine Dokumentation!");
		element.click();
		Assert.assertFalse("Can't add same NodeName", element.isEnabled());
	}
}

