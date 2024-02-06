package com.orangomango.hbm;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.robot.Robot;
import javafx.scene.paint.Color;
import javafx.scene.image.*;
import javafx.animation.AnimationTimer;
import javafx.scene.input.MouseButton;

/**
 * Reaction time.
 * very simple application. The bot checks when a given pixel is green,
 * and clicks on it as fast as possible.
 * 
 * @author OrangoMango
 * @version 1.0
 * 
 * DEBUG notes:
 * This is so easy to configure that no DEBUG is needed.
 * Just change the coordinates where the application is reading and clicking (optional).
 */
public class ReactionTest extends Application{
	private Robot robot;
	private boolean cooldown = true;

	public void start(Stage stage){
		this.robot = new Robot();

		AnimationTimer loop = new AnimationTimer(){
			@Override
			public void handle(long time){
				update();
			}
		};
		loop.start();
	}

	private void update(){
		Image image = this.robot.getScreenCapture(new WritableImage(5, 5), 450, 450, 5, 5); // CHANGE
		PixelReader reader = image.getPixelReader();
		Color color = reader.getColor(2, 2);
		if (color.getGreen() > 0.8 && this.cooldown){
			this.robot.mouseMove(450, 450); // CHANGE
			this.robot.mouseClick(MouseButton.PRIMARY);
			this.robot.mouseClick(MouseButton.PRIMARY);
			this.cooldown = false;
			new Thread(() -> {
				try {
					Thread.sleep(200);
					this.cooldown = true;
				} catch (InterruptedException ex){
					ex.printStackTrace();
				}
			}).start();
		}
	}
}