package com.orangomango.hbm;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.robot.Robot;
import javafx.scene.input.KeyCode;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Number memory.
 * The application simply reads the numbers on the screen
 * and rewrites them in the text field
 * 
 * @author OrangoMango
 * @version 1.0
 * 
 * DEBUG notes:
 * No DEBUG is needed here, as the application just reads the numbers
 * and rewrites them in the text field.
 */
public class NumberMemory extends Application{
	private Robot robot;
	private String lastNum = null;

	@Override
	public void start(Stage stage){
		this.robot = new Robot();
		System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");  
		WebDriver driver = new ChromeDriver();
		driver.get("https://humanbenchmark.com/tests/number-memory");

		Thread loop = new Thread(() -> {
			try {
				while (true){
					update(driver);
					Thread.sleep(1000);
				}
			} catch (InterruptedException ex){
				driver.quit();
				System.exit(1);
			}
		});
		loop.setDaemon(true);
		loop.start();
	}

	private void update(WebDriver driver) throws InterruptedException{
		String pageSource = driver.getPageSource().replace(">", ">\n");
		String[] lines = pageSource.split("\n");
		boolean contained = false;
		for (int i = 0; i < lines.length; i++){
			String line = lines[i];
			if (line.contains("big-number")){
				contained = true;
				String num = lines[i+1].split("<")[0];
				if (this.lastNum == null){
					this.lastNum = num;
					System.out.println(this.lastNum);
				}
			}
		}

		if (!contained && this.lastNum != null){
			for (int i = 0; i < this.lastNum.length(); i++){
				final char c = this.lastNum.charAt(i);
				Platform.runLater(() -> this.robot.keyType(KeyCode.valueOf("DIGIT"+c)));
			}

			Platform.runLater(() -> this.robot.keyType(KeyCode.ENTER));
			Thread.sleep(100);
			Platform.runLater(() -> this.robot.keyType(KeyCode.ENTER));
			this.lastNum = null;
		}
	}
}