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
 * this test adds an ArtifactTemplate and then add another with the same name
 *
 */

public class UITestAddArtifactTemplate extends TestSettings {

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
	public void testAddArtifactTemplate() throws Exception {
		Thread thread = new Thread();
		
		//add artifact template
		driver.get("http://localhost:4200/#/other");
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Templates')]")).click();
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("Hallo");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/capabilitytypes");
		driver.findElement(By.xpath("//div[@class='btn btn-default btn-secondary form-control ui-select-toggle']")).click();
		driver.findElement(By.xpath("//a[@class='dropdown-item']//div[contains(text(), 'ArtifactTypeWithoutProperties')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();

		thread.sleep(5000);

		//add another artifact template with same name
		driver.findElement(By.xpath("//a[@id='artSelect']//div[contains(text(), 'Other Elements: Artifact Templates')]")).click();
		driver.findElement(By.xpath("//a[@class='btn btn-default'][contains(text(), 'Artifact Templates')]")).click();
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("Hallo");
		driver.findElement(By.id("namespace")).sendKeys("Hallo");
		driver.findElement(By.xpath("//div[@class='ui-select-match']")).click();
		driver.findElement(By.xpath("//a[@class='dropdown-item']//div[contains(text(), 'ArtifactTypeWithoutProperties')]")).click();
		WebElement element = driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]"));
		element.click();
		Assert.assertFalse("Can't add same NodeName", element.isEnabled());
	}
}
