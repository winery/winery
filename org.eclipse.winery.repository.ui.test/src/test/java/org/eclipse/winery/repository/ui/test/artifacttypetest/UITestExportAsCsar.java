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
 * this test exports an ArtifactType as CSAR 
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
	//Export Artifacttyoe as CSAR
	public void testExportAsCsar() throws Exception {
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Types')]")).click();
		driver.findElement(By.xpath("//a[@class='exportButton']")).click();
		//driver.findElement(By.cssSelector("btn btn-default sidebar-btn")).click();
		//driver.findElement(By.id("sectionsAddNewBtn")).click();
	}
}
