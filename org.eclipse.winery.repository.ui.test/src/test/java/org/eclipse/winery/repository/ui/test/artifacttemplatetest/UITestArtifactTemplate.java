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
 * this test adds two different ArtifactTemplates
 * edits an artifact template
 * opens XML view in new tab, 
 * exports CSAR 
 * deletes artifact template
 *
 */

public class UITestArtifactTemplate extends TestSettings {

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
	//Artifact Template
	public void testArtifactTemplate() throws Exception {
		Thread thread = new Thread();
		String originalHandle = driver.getWindowHandle();
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Templates')]")).click();
		//add new artifact template
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("Hallo");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/relationshiptype");
		driver.findElement(By.xpath("//div[@class='ui-select-match']")).click();
		driver.findElement(By.xpath("//a[@class='dropdown-item']//div[contains(text(), 'ArtifactTypeWithoutProperties')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();

		thread.sleep(2000);

		//add another artifact template
		driver.findElement(By.xpath("//a[@id='artSelect']//div[contains(text(), 'Other Elements: Artifact Templates')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Templates')]")).click();
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("Hu");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/relationshiptype");
		driver.findElement(By.xpath("//div[@class='ui-select-match']")).click();
		driver.findElement(By.xpath("//a[@class='dropdown-item']//div[contains(text(), 'ArtifactTypeWithoutProperties')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();

		thread.sleep(2000);

		//edit artifact template, go to XML view
		driver.findElement(By.xpath("//a[@id='artSelect']//div[contains(text(), 'Other Elements: Artifact Templates')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Templates')]")).click();
		driver.findElement(By.xpath("//a[@class='editButton']")).click();
		
		driver.findElement(By.xpath("//a[@class='btn btn-info']")).click();
		
		thread.sleep(2000);
		
		for (String handle : driver.getWindowHandles()) {
			if (!handle.equals(originalHandle)) {
				driver.switchTo().window(handle);
				driver.close();
			}
		}
		thread.sleep(5000);
		
		driver.switchTo().window(originalHandle);

		thread.sleep(2000);
		
		//export CSAR
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Types')]")).click();
		driver.findElement(By.id("sectionsImportCsarBtn")).click();
		driver.findElement(By.xpath("//input[@type='file']")).click();

		thread.sleep(2000);
		
		//delete artifact template
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Templates')]")).click();
		driver.findElement(By.xpath("//a[@class='deleteButton']")).click();
		//driver.findElement(By.xpath("//button[@class='btn btn-primary']")).click();

		WebElement element = driver.findElement(By.xpath("//button[@class='btn btn-primary']"));
		element.click();
		Assert.assertTrue("Test successful", element.isEnabled());
	}
}
