package org.eclipse.winery.repository.ui.test;

import org.openqa.selenium.remote.DesiredCapabilities;

public class TestSettings {
	
	public static void settings()
	{
		System.setProperty("webdriver.gecko.driver", "C:/Users/asst/Desktop/geckodriver.exe");
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		capabilities.setCapability("marionette", true);
	}
	
}
