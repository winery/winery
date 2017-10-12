package org.eclipse.winery.repository.ui.test.artifacttypetest;

import org.eclipse.winery.repository.ui.test.TestSettings;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 *
 * this test tests the search field in ArtifactType area
 *
 */

public class UITestSearchField extends TestSettings {

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
	public void testSearchField() throws Exception {
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Types')]")).click();
		driver.findElement(By.xpath("//input[@id='searchBox']")).sendKeys("Hallo");;
		//driver.findElement(By.cssSelector("btn btn-default sidebar-btn")).click();
		//driver.findElement(By.id("sectionsAddNewBtn")).click();
	}
}
