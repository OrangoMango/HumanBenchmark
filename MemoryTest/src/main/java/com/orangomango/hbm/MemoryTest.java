package com.orangomango.hbm;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.robot.Robot;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;

import java.util.*;

/**
 * Sequence memory bot.
 * The application is able to memorize the selected squares
 * and reproduce them in the correct order.
 * 
 * @author OrangoMango
 * @version 1.0
 * 
 * DEBUG notes:
 * Start the test and wait until all squares are turned off.
 * After that, run the program and move the window so that it fits perfectly.
 */	
public class MemoryTest extends Application{
	private static final int SIZE = 400; // CHANGE
	private Robot robot;
	private List<Point2D> currentCombination = new ArrayList<>();
	private boolean running = false;
	private volatile boolean cooldown = true;

	public static final int SX = 370; // CHANGE
	public static final int SY = 320; // CHANGE

	public static final boolean DEBUG = false;

	@Override
	public void start(Stage stage){
		this.robot = new Robot();
		Canvas canvas = new Canvas(SIZE, SIZE);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		AnimationTimer loop = new AnimationTimer(){
			@Override
			public void handle(long time){
				update(gc);
			}
		};
		loop.start();

		if (DEBUG){
			StackPane pane = new StackPane(canvas);
			Scene scene = new Scene(pane, SIZE, SIZE);
			stage.setScene(scene);
			stage.show();
		}
	}

	private void update(GraphicsContext gc){
		if (DEBUG) gc.clearRect(0, 0, SIZE, SIZE);

		if (this.running){
			if (this.currentCombination.size() > 0){
				if (this.cooldown){
					Point2D point = this.currentCombination.remove(0);
					if (!DEBUG){
						this.robot.mouseMove(SX+point.getX()*150+50, SY+point.getY()*150+50); // CHANGE
						this.robot.mouseClick(MouseButton.PRIMARY);
					}
					System.out.format("Pressing at %.0f %.0f\n", point.getX(), point.getY());
					this.cooldown = false;
					new Thread(() -> {
						try {
							Thread.sleep(100);
							this.cooldown = true;
						} catch (InterruptedException ex){
							ex.printStackTrace();
						}
					}).start();
				}
			} else {
				this.running = false;
			}
		} else {
			Image image = this.robot.getScreenCapture(new WritableImage(SIZE, SIZE), SX, SY, SIZE, SIZE);
			PixelReader reader = image.getPixelReader();
			if (DEBUG) gc.drawImage(image, 0, 0);

			Point2D open = null;
			for (int i = 0; i < 3; i++){
				for (int j = 0; j < 3; j++){
					Color color = reader.getColor(i*150+50, j*150+50);
					if (color.getRed() > 0.98 && color.getGreen() > 0.98 && color.getBlue() > 0.98){
						if (DEBUG) gc.setFill(Color.GREEN);
						open = new Point2D(i, j);
					} else {
						if (DEBUG) gc.setFill(Color.RED);
					}
					if (DEBUG) gc.fillOval(i*150+50-25, j*150+50-25, 50, 50); // CHANGE (debug)
				}
			}

			if (open != null){
				Point2D last = this.currentCombination.size() == 0 ? null : this.currentCombination.get(this.currentCombination.size()-1);
				if (last == null || (last.getX() != open.getX() || last.getY() != open.getY())){
					this.currentCombination.add(open);
					System.out.format("Adding %.0f %.0f\n", open.getX(), open.getY());
				}
			} else {
				this.running = true;
			}
		}
	}
}
