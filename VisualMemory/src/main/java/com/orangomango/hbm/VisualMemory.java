package com.orangomango.hbm;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.image.*;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.scene.Scene;

import java.util.*;

/**
 * Visual memory.
 * The application remembers the current pattern and then it selects
 * all the correct squares.
 * 
 * @author OrangoMango
 * @version 1.0
 * 
 * DEBUG notes:
 * Move the window so that the red circles are perfectly aligned.
 */
public class VisualMemory extends Application{
	private static final int SIZE = 400; // CHANGE
	private Robot robot;
	private List<Point2D> currentList = new ArrayList<>();
	private boolean running = false;
	private volatile boolean cooldown = true, canStart = false;
	private int firstPiece, secondPiece;

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
		final int sx = 340; // CHANGE
		final int sy = 390; // CHANGE
		if (DEBUG) gc.clearRect(0, 0, SIZE, SIZE);
		if (this.running){
			if (this.currentList.size() > 0){
				if (this.cooldown && this.canStart){
					Point2D point = this.currentList.remove(0);
					System.out.println(firstPiece+" "+secondPiece);
					if (!DEBUG){
						this.robot.mouseMove(sx+5+point.getX()*(secondPiece+firstPiece)+secondPiece/2, sy+5+point.getY()*(secondPiece+firstPiece)+secondPiece/2);
						this.robot.mouseClick(MouseButton.PRIMARY);
					}
					System.out.println("Selecting "+point);
					this.cooldown = false;
					schedule(() -> this.cooldown = true, 200);
				}
			} else {
				schedule(() -> this.running = false, 1500);
			}
		} else {
			Image image = this.robot.getScreenCapture(new WritableImage(SIZE, SIZE), sx, sy, SIZE, SIZE);
			if (DEBUG) gc.drawImage(image, 0, 0);
			PixelReader reader = image.getPixelReader();

			Color lastColor = null;
			int count = 0;
			List<Integer> steps = new ArrayList<>();
			if (DEBUG) gc.strokeLine(0, 20, SIZE, 20);
			for (int i = 0; i < image.getWidth(); i++){
				Color color = reader.getColor(i, 20);
				if (lastColor == null){
					lastColor = color;
				} else {
					if (!color.equals(lastColor)){
						count++;
						steps.add(i);
					}
					lastColor = color;
				}
			}
			if (steps.size() < 3) return;
			this.firstPiece = steps.get(2)-steps.get(1);
			this.secondPiece = steps.get(1)-steps.get(0);
			if (this.firstPiece >= this.secondPiece) return;
			int size = count/2;
			//System.out.println(size+" "+firstPiece+" "+secondPiece);

			boolean empty = true;
			if (size >= 3){
				for (int i = 0; i < size; i++){
					for (int j = 0; j < size; j++){
						int px = 5+i*(secondPiece+firstPiece);
						int py = 5+j*(secondPiece+firstPiece);
						Color color = reader.getColor((int)Math.min(image.getWidth()-1, px+secondPiece/2), (int)Math.min(image.getHeight()-1, py+secondPiece/2));
						boolean selected = color.getRed() > 0.98 && color.getGreen() > 0.98 && color.getBlue() > 0.98;
						if (DEBUG) gc.setFill(selected ? Color.GREEN : Color.RED);
						if (DEBUG) gc.fillOval(px, py, secondPiece, secondPiece);
						if (selected){
							Point2D point = new Point2D(i, j);
							if (!this.currentList.contains(point)){
								this.currentList.add(point);
								System.out.println("Adding "+point);
							}
							empty = false;
						}
					}
				}
			}

			if (empty && this.currentList.size() > 0){
				this.running = true;
				this.canStart = false;
				schedule(() -> this.canStart = true, 2500);
				System.out.println("Running...");
			}
		}
	}

	private static void schedule(Runnable r, int delay){
		new Thread(() -> {
			try {
				Thread.sleep(delay);
				r.run();
			} catch (InterruptedException ex){
				ex.printStackTrace();
			}
		}).start();
	}
}