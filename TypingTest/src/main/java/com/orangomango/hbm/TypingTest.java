package com.orangomango.hbm;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.robot.Robot;
import javafx.scene.input.KeyCode;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.*;

public class TypingTest extends Application{
	private Robot robot;
	private String lastNum = null;

	@Override
	public void start(Stage stage){
		this.robot = new Robot();
		System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");  
		WebDriver driver = new ChromeDriver();
		driver.get("https://humanbenchmark.com/tests/typing");

		Thread loop = new Thread(() -> {
			try {
				Thread.sleep(5000);
				System.out.println("Starting...");
				while (true){
					update(driver);
					Thread.sleep(2000);
				}
			} catch (InterruptedException ex){
				driver.quit();
				System.exit(1);
			}
		});
		loop.setDaemon(true);
		loop.start();
	}

	private void update(WebDriver driver){
		String pageSource = driver.getPageSource().replace(">", ">\n");
		boolean output = false;
		String[] lines = pageSource.split("\n");
		List<String> characters = new ArrayList<>();
		for (int i = 0; i < lines.length; i++){
			String line = lines[i];
			if (line.contains("incomplete")){
				output = true;
				String c = lines[i+1].split("<")[0];
				characters.add(c);
			}
		}

		if (output){
			for (String c : characters){
				Platform.runLater(() -> {
					if (c.equals(" ")){
						this.robot.keyType(KeyCode.SPACE);
					} else if (c.equals(",")){
						this.robot.keyType(KeyCode.COMMA);
					} else if (c.equals(".")){
						this.robot.keyType(KeyCode.PERIOD);
					} else if (c.equals("'")){
						this.robot.keyType(KeyCode.QUOTE);
					} else if (c.equals(";")){
						this.robot.keyType(KeyCode.SEMICOLON);
					} else if (c.equals("-")){
						this.robot.keyType(KeyCode.MINUS);
					} else if (c.equals("?")){
						this.robot.keyPress(KeyCode.SHIFT);
						this.robot.keyType(KeyCode.SLASH);
						this.robot.keyRelease(KeyCode.SHIFT);
					} else if (c.equals("\"")){
						this.robot.keyType(KeyCode.QUOTEDBL);
					} else if (c.equals("!")){
						this.robot.keyType(KeyCode.EXCLAMATION_MARK);
					} else {
						char ch = c.charAt(0);
						if (Character.isUpperCase(ch)) this.robot.keyPress(KeyCode.SHIFT);
						if (KeyCode.getKeyCode(c.toUpperCase()) == null){
							System.out.println(c);
						}
						this.robot.keyType(KeyCode.getKeyCode(c.toUpperCase()));
						if (Character.isUpperCase(ch)) this.robot.keyRelease(KeyCode.SHIFT);
					}
				});
			}
		}
	}
}