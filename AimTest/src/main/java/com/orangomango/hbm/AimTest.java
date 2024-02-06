package com.orangomango.hbm;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.input.MouseButton;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.scene.Scene;

import java.util.function.Predicate;

/**
 * Aim trainer bot.
 * It clicks as fast as possible on the target. When the orange button
 * appears, the program exits.
 * 
 * @author OrangoMango
 * @version 1.0
 * 
 * DEBUG notes:
 * Make sure that the window covers the entire rectangle of the test.
 */
public class AimTest extends Application{
	private static final int WIDTH = 900;
	private static final int HEIGHT = 450;
	private Robot robot;

	public static final boolean DEBUG = false;

	public void start(Stage stage){
		this.robot = new Robot();

		Canvas canvas = new Canvas(WIDTH, HEIGHT);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		AnimationTimer loop = new AnimationTimer(){
			@Override
			public void handle(long time){
				boolean stop = update(gc);
				if (stop){
					System.exit(0);
				}
			}
		};
		loop.start();

		if (DEBUG){
			StackPane pane = new StackPane(canvas);
			Scene scene = new Scene(pane, WIDTH, HEIGHT);
			stage.setScene(scene);
			stage.show();
		}
	}

	private boolean update(GraphicsContext gc){
		final int sx = 40; // CHANGE
		final int sy = 320; // CHANGE
		if (DEBUG) gc.clearRect(0, 0, WIDTH, HEIGHT);

		Image image = this.robot.getScreenCapture(new WritableImage(WIDTH, HEIGHT), sx, sy, WIDTH, HEIGHT);
		if (DEBUG) gc.drawImage(image, 0, 0);
		PixelReader reader = image.getPixelReader();
		outer:
		for (int i = 0; i < WIDTH; i += 20){
			for (int j = 0; j < HEIGHT; j += 20){
				Color color = reader.getColor(i, j);
				if (Math.abs(color.getRed()-0.98) < 0.05 && Math.abs(color.getGreen()-0.81) < 0.05 && Math.abs(color.getBlue()-0.32) < 0.05){
					return true; // If there is the color orange, then stop
				}
				if (isTarget(reader, i, j)){
					if (!DEBUG){
						this.robot.mouseMove(sx+i, sy+j);
						this.robot.mouseClick(MouseButton.PRIMARY);
					}
					break outer;
				}
			}
		}

		return false;
	}

	private boolean isTarget(PixelReader reader, int x, int y){
		Predicate<Color> ok = color -> color != null && Math.abs(color.getRed()-0.58) < 0.05 && Math.abs(color.getGreen()-0.76) < 0.05 && Math.abs(color.getBlue()-0.90) < 0.05;
		Color n = getColorAt(reader, x, y-1);
		Color e = getColorAt(reader, x+1, y);
		Color s = getColorAt(reader, x, y+1);
		Color w = getColorAt(reader, x-1, y);
		return ok.test(n) && ok.test(e) && ok.test(s) && ok.test(w);
	}

	private Color getColorAt(PixelReader reader, int x, int y){
		if (x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT){
			return reader.getColor(x, y);
		} else {
			return null;
		}
	}
}