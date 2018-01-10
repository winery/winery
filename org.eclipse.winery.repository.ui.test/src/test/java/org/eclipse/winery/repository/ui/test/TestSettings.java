package org.eclipse.winery.repository.ui.test;

import java.net.URL;

import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.rest.server.WineryUsingHttpServer;

import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TestSettings extends TestWithGitBackedRepository {

	protected static RemoteWebDriver driver;

	private static final String WEBDRIVER_GECKO_DRIVER = "webdriver.gecko.driver";

	private static Server repositoryServer;
	private static Server repositoryUiServer;

	private static boolean runsOnSauce;

	@BeforeClass
	public static void init() throws Exception {
		String sauceUsername = System.getenv("SAUCE_USERNAME");
		if (sauceUsername == null) {
			runsOnSauce = false;

			repositoryServer = WineryUsingHttpServer.createHttpServer();
			repositoryServer.start();

			repositoryUiServer = WineryUsingHttpServer.createHttpServerForRepositoryUi();
			repositoryUiServer.start();

			if (System.getProperty(WEBDRIVER_GECKO_DRIVER) == null) {
				// for tests in the IDE only -- for travis, the binary should have been automatically downloaded - see https://github.com/Ardesco/Selenium-Maven-Template/blob/master/pom.xml

				System.setProperty("webdriver.gecko.driver", "C:/Users/Franzi/Documents/geckodriver.exe");
				//System.setProperty("webdriver.gecko.driver", "C:/Users/lharz/Downloads/geckodriver.exe");
			}

			DesiredCapabilities desiredCapabilities = DesiredCapabilities.firefox();
			desiredCapabilities.setCapability("marionette", true);
			driver = new FirefoxDriver(desiredCapabilities);
		} else {
			runsOnSauce = true;
			String sauceAccessKey = System.getenv("SAUCE_ACCESS_KEY");
			String travisJobNumber = System.getenv("TRAVIS_JOB_NUMBER");
			String travisBuildNumber = System.getenv("TRAVIS_BUILD_NUMBER");

			DesiredCapabilities desiredCapabilities = DesiredCapabilities.firefox();
			desiredCapabilities.setCapability("tunnel-identifier", travisJobNumber);
			desiredCapabilities.setCapability("build", travisBuildNumber);
			URL hubUrl = new URL("https://" + sauceUsername + ":" + sauceAccessKey + "@localhost:4445/wd/hub");

			driver = new RemoteWebDriver(hubUrl, desiredCapabilities);
		}

		driver.manage().timeouts().implicitlyWait(10, SECONDS);
	}

	@Before
	public void setToLatestRevision() throws Exception {
		this.setRevisionTo("origin/plain");
	}

	@AfterClass
	public static void shutdown() throws Exception {
		driver.close();
		if (!runsOnSauce) {
			repositoryServer.stop();
			repositoryUiServer.stop();
		}
	}
}
