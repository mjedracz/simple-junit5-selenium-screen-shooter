package mario.core;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

public class ScreenShooter implements AfterTestExecutionCallback {

    private static final String SCREENSHOT_DIR = "screenshots";
    private static Logger log = Logger.getLogger(ScreenShooter.class.getName());

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {
        final Optional<Throwable> executionException = extensionContext.getExecutionException();
        if (executionException.isPresent() && exceptionValidForScreenShot(executionException.get())) {
            try {
                handleScreenShot(extensionContext);
            } catch (Exception e) {
                log.info(() -> String.format("Failed to take screenshot: %s", e.getMessage()));
            }
        }
    }

    private WebDriver getDriver(ExtensionContext extensionContext) throws Exception {
        Object test = extensionContext.getRequiredTestInstance();
        Field driverField = test.getClass().getDeclaredField("driver");
        driverField.setAccessible(true);
        return (WebDriver) driverField.get(test);
    }

    private void handleScreenShot(ExtensionContext extensionContext) throws Exception {
        WebDriver driver = getDriver(extensionContext);
        Path directory = getScreenShotDirectory();
        String filename = getFileName(extensionContext);
        Path screenShotPath = Paths.get(directory.toString(), filename);
        takeScreenShot(driver, screenShotPath);
    }

    private void takeScreenShot(WebDriver driver, Path screenShotPath) throws IOException {
        byte[] file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Files.write(screenShotPath, file, CREATE_NEW);
    }

    private Path getScreenShotDirectory() {
        return Paths.get(SCREENSHOT_DIR);
    }

    private String getFileName(ExtensionContext extensionContext) {
        String methodName = extensionContext.getRequiredTestMethod().getName();
        String className = extensionContext.getRequiredTestClass().getName();
        return String.format("%s_%s_%d.png", className, methodName, System.currentTimeMillis());
    }

    private boolean exceptionValidForScreenShot(Throwable executionException) {
        return executionException instanceof WebDriverException ||
                executionException instanceof AssertionError;
    }
}
