package org.eclipse.winery.repository.ui.test.nodetypetest;

import org.eclipse.winery.repository.ui.test.TestSettings;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import static java.util.concurrent.TimeUnit.SECONDS;


/**
 * this test add a NodeType with all components and delete it after this
 * Rename Node
 * Add State
 * Add implementation
 * add Requiremnent Definitions (type: RequirementTypeWithOneKvProperty)
 * add CapabilityDefinition (type: CapabilityTypeWithOneKvProperty)
 * add PropertiesDefinition (Custom key/value pairs)
 * add Inheritance (Derived from RequirementTypeWithOneKvProperty)
 * add Dosumentation
 * 
 * delete NewNodeType
 * delete createt implementation
 * 
 * add Interface is missing 
 */

public class UITestNodeTypes extends TestSettings {
	
	private static  FirefoxDriver driver;
	private static Thread thread;

	@BeforeClass
	public static void init() throws Exception {
		settings();
		thread = new Thread();
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(10, SECONDS);
	}

	@AfterClass
	public static void shutdown() throws Exception {
		//driver.close();
	}

	@Test
	public void testNodeType() throws Exception {
		driver.get("http://localhost:4200/#/nodetypes");
		driver.findElement(By.id("sectionsAddNewBtn")).click();
		driver.findElement(By.id("componentName")).sendKeys("TestNode");
		driver.findElement(By.id("namespace")).sendKeys("http://plain.winery.opentosca.org/nodetypes");
		driver.findElement(By.xpath("//div[@class='modal-footer']//button[contains(.,'Add')]")).click();

		//Refresh page
		driver.navigate().refresh();
		renameNode();
		thread.sleep(1000);
		addState();
		//addInterface();
		addImplementation();
		addRequirement();
		addCapabi();
		addProperty();
		addInheritance();
		
		
		//Add Documentation
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Documentation')]")).click();
		driver.findElement(By.xpath("//textarea[@class='texDoc ng-untouched ng-pristine ng-valid']")).sendKeys("Documentation");
		
		//	Change value
		thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Save')]")).click();
		thread.sleep(1000);
		
		//Delete NodeType
		driver.findElement(By.xpath("//button[@class='btn btn-danger'][contains(.,'Delete')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Delete')]")).click();
		
		//Delete Implementation
		driver.get("http://localhost:4200/#/other");
		driver.navigate().refresh();
		driver.findElement(By.xpath("//a[contains(.,'Node Type Implementations')]")).click();
		driver.findElement(By.xpath("//div[@id='ImplNamespace/ImplName']")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-danger'][contains(.,'Delete')]")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Delete')]")).click();
		Assert.assertTrue(true);
	}

	private void addInheritance() throws InterruptedException {
		//Add Inheritance NodeTypeWithOnKVProperty
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Inheritance')]")).click();
		Select dropdown = new Select(driver.findElement(By.xpath("//div[@class='ui-select-container dropdown open']")));
		dropdown.selectByVisibleText("NodeTypeWithOneKVProperty");
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Save')]")).click();
		thread.sleep(1000);
	}

	private void addProperty() throws InterruptedException {
		//Add Property
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Properties Definition')]")).click();
		driver.findElement(By.xpath("//input[@id='customkv'][@type='radio']")).click();
		thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		driver.findElement(By.xpath("//input[@id='key']")).sendKeys("AddProperty");
		//	@TODO: Change value 
		thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();
		thread.sleep(1000);
	}
	
	private void addCapabi() throws InterruptedException {
		//Add Capability Definition CapabilityTypeWithOneKvProperty
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Capability Definition')]")).click();
		driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		driver.findElement(By.xpath("//input[@id='key']")).sendKeys("capaName");
		Select dropdown = new Select(driver.findElement(By.xpath("//select[@class='form-control']")));
		dropdown.selectByVisibleText("CapabilityTypeWithOneKvProperty");
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();
		thread.sleep(1000);

	}

	private void addRequirement() throws InterruptedException{
		//Add Requirement Definition RequirementTypeWithOneKvProperty
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Requirement Definition')]")).click();
		driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		driver.findElement(By.xpath("//input[@id='key']")).sendKeys("requiredType");
		Select dropdown = new Select(driver.findElement(By.xpath("//select[@class='form-control']")));
		dropdown.selectByVisibleText("RequirementTypeWithOneKvProperty");
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();
		thread.sleep(1000);
	
	}

	private void addImplementation() throws InterruptedException {
		//Add Implementations
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Implementations')]")).click();
		thread.sleep(10);
		driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		driver.findElement(By.xpath("//input[@id='localname']")).sendKeys("ImplName");
		driver.findElement(By.xpath("//input[@id='namespace']")).sendKeys("http://plain.opentosca.org/test/nodetypeimplementations");
		thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(text(), 'Add')]")).click();
		thread.sleep(1000);
	}

	private void addInterface() {

		/**
		 //Add Interface
		 driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Interface')]")).click();
		 driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		 driver.findElement(By.xpath("//input[@id='interfaceName']")).sendKeys("addInterface");
		 thread.sleep(1000);
		 driver.findElement(By.xpath("//div[@class='modal-footer']//button[@class='btn btn-primary'][contains(.,'Add')]")).click();
		 driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		 driver.findElement(By.xpath("//input[@id='interfaceName']")).sendKeys("addInterface");
		 driver.findElement(By.xpath("//button[@id='addIfBtn']")).click();
		 driver.findElement(By.xpath("//input[@id='interfaceName']")).sendKeys("add");
		 thread.sleep(1000);
		 driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Add')]")).click();
		 driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Save')]")).click();
		 thread.sleep(1000);
		 **/
	}

	private void renameNode() {
		//Rename Node
		driver.findElement(By.id("localname")).click();
		WebElement element = driver.findElement(By.id("renamePropertyInput"));
		element.sendKeys(Keys.chord(Keys.CONTROL, "a"), "55");
		element.sendKeys("NewName");
		driver.findElement(By.xpath("//button[@class='btn btn-primary btn-xs'][contains(text(), 'Save')]")).click();
	}
	
	private static void addState() throws InterruptedException {
		//Add State
		driver.findElement(By.xpath("//a[@class='styledTabMenuButton styledTabMenuButton2ndlevel']//div[contains(.,'Instance States')]")).click();
		driver.findElement(By.xpath("//button[@class='rightbutton btn btn-primary btn-xs'][contains(.,'Add')]")).click();
		driver.findElement(By.xpath("//input[@id='state']")).sendKeys("AddState");
		thread.sleep(1000);
		driver.findElement(By.xpath("//button[@class='btn btn-primary'][contains(.,'Add')]")).click();
		thread.sleep(1000);
		
	}
}
