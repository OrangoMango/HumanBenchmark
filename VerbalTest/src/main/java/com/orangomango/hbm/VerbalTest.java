package com.orangomango.hbm;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.robot.Robot;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.*;

/**
 * Verbal memory.
 * The application simply uses a list to save all the encoutered words.
 * 
 * @author OrangoMango
 * @version 1.0
 * 
 * DEBUG notes:
 * Here there is no DEBUG, make a screenshot and note the coordinates
 * of the "Seen" button and of the "New" button
 */
public class VerbalTest extends Application{
	public static final int SEEN_BUTTON_X = 400; // CHANGE
	public static final int NEW_BUTTON_X = 550; // CHANGE
	public static final int BUTTON_Y = 590; // CHANGE
	private Robot robot;
	private List<String> words = new ArrayList<>();

	@Override
	public void start(Stage stage){
		this.robot = new Robot();
		System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");  
		WebDriver driver = new ChromeDriver();
		driver.get("https://humanbenchmark.com/tests/verbal-memory");

		Thread loop = new Thread(() -> {
			try {
				Thread.sleep(5000);
				System.out.println("Starting...");
				while (true){
					update(driver);
					Thread.sleep(400);
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
		String word = null;
		String[] lines = pageSource.split("\n");
		for (int i = 0; i < lines.length; i++){
			String line = lines[i];
			if (line.contains("class=\"word")){
				word = lines[i+1].split("<")[0];
				System.out.println("Word: "+word);
			}
		}

		if (word != null){
			final String w = word;
			Platform.runLater(() -> {
				if (this.words.contains(w)){
					this.robot.mouseMove(SEEN_BUTTON_X, BUTTON_Y);
				} else {
					this.words.add(w);
					this.robot.mouseMove(NEW_BUTTON_X, BUTTON_Y);
				}
				this.robot.mouseClick(MouseButton.PRIMARY);
			});
		}
	}
}