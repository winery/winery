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
 * this test exports an ArtifactTemplate as CSAR 
 *
 */

public class UITestExportAsCsar extends TestSettings {

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
	public void testExportAsCsar() throws Exception {
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Templates')]")).click();
		driver.findElement(By.xpath("//a[@class='exportButton']")).click();
		//driver.findElement(By.xpath("//button[contains(text(), 'OK')]"));

		WebElement element = driver.findElement(By.xpath("//button[contains(text(), 'OK')]"));
		element.click();
		Assert.assertTrue("Export Artifact Template as CSAR successful", element.isEnabled());
	}
}
