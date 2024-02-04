package com.orangomango.hbm;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.canvas.*;
import javafx.scene.robot.Robot;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.*;

public class ChimpTest extends Application{
	public static final int SX = 45;
	public static final int SY = 280;
	public static final int OK_BUTTON_X = 450;
	public static final int OK_BUTTON_Y = 635;

	private Robot robot;

	@Override
	public void start(Stage stage){
		this.robot = new Robot();

		System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");  
		WebDriver driver = new ChromeDriver();
		driver.get("https://humanbenchmark.com/tests/chimp");

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
		String[] data = pageSource.split("css-gmuwbf");
		if (data.length > 1){
			String result =data[1].split("desktop-only-warning")[0];

			// Analyze
			Map<Integer, Point2D> points = new HashMap<>();
			int maxN = Integer.MIN_VALUE;
			String[] lines = result.split("\n");
			int rowCounter = -1;
			int colCounter = -1;
			for (int i = 0; i < lines.length; i++){
				String line = lines[i];
				if (line.contains("css-k008qs")){ // New row
					rowCounter++;
					colCounter = -1;
				}
				if (line.contains("css-19b5rdt") || line.contains("css-ggichp")){ // New column
					colCounter++;
				}

				String[] pieces = line.split("data-cellnumber=");
				if (pieces.length > 1){
					String n = pieces[1].split(" ")[0];
					n = n.substring(1, n.length()-1);
					int num = Integer.parseInt(n);
					if (num > maxN) maxN = num;
					points.put(num, new Point2D(colCounter+1, rowCounter)); // ?
				}
			}

			System.out.println(points);

			for (int i = 1; i <= maxN; i++){
				Point2D pos = points.get(i);
				Platform.runLater(() -> {
					// The following numbers are used to calculate each square size
					this.robot.mouseMove(SX+5+pos.getX()*87+35, SY+5+pos.getY()*90+40);
					this.robot.mouseClick(MouseButton.PRIMARY);
				});
				Thread.sleep(100);
			}

			Platform.runLater(() -> {
				this.robot.mouseMove(OK_BUTTON_X, OK_BUTTON_Y);
				this.robot.mouseClick(MouseButton.PRIMARY);
			});
		}
	}
}