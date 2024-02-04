package com.orangomango.hbm;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.input.MouseButton;
import javafx.animation.AnimationTimer;

import java.util.function.Predicate;

public class AimTest extends Application{
	private static final int WIDTH = 900;
	private static final int HEIGHT = 450;

	private Robot robot;

	public void start(Stage stage){
		this.robot = new Robot();

		AnimationTimer loop = new AnimationTimer(){
			@Override
			public void handle(long time){
				boolean stop = update();
				if (stop){
					System.exit(0);
				}
			}
		};
		loop.start();
	}

	private boolean update(){
		final int sx = 40; // CHANGE
		final int sy = 320; // CHANGE

		Image image = this.robot.getScreenCapture(new WritableImage(WIDTH, HEIGHT), sx, sy, WIDTH, HEIGHT);
		PixelReader reader = image.getPixelReader();
		outer:
		for (int i = 0; i < WIDTH; i += 20){
			for (int j = 0; j < HEIGHT; j += 20){
				Color color = reader.getColor(i, j);
				if (Math.abs(color.getRed()-0.98) < 0.05 && Math.abs(color.getGreen()-0.81) < 0.05 && Math.abs(color.getBlue()-0.32) < 0.05){
					return true; // If there is the color orange, then stop
				}
				if (isTarget(reader, i, j)){
					this.robot.mouseMove(sx+i, sy+j);
					this.robot.mouseClick(MouseButton.PRIMARY);
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