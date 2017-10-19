package org.eclipse.winery.repository.ui.test;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

public class UITestBaobab extends TestSettings {

	@Test
	public void baobabTest() throws Exception {
		this.setRevisionTo("304b62b06556afa1a7227164a9c0d2c9a1178b8f");

		driver.get("http://localhost:4200/#/nodetypes");
		driver.findElement(By.xpath("//div[@class='entityContainer nodeType']//div[@class='center']//div[contains(text(), baobab)]")).click();
		driver.findElement(By.id("localName")).click();
		driver.findElement(By.id("renamePropertyInput")).sendKeys("RenameBaobab");
		driver.findElement(By.xpath("//button[@class='btn btn-primary btn-xs'][contains(text(), 'Save')]")).click();
		Assert.assertTrue(true);
	}
}

