package ui;

import game.world.World;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class SimulatorWindowComponent extends JComponent {

	Camera currentCamera;
	World world;

	boolean wpressed;
	boolean spressed;
	boolean apressed;
	boolean dpressed;

	public SimulatorWindowComponent(Camera mainCamera) {
		this.currentCamera = mainCamera;
	}

	long originalStartTime;
	long lastUpdateTime;
	long updateTimeDifference;
	long runningTime;

	final long updateCap = 5_000_000;
	final long baseDeltaTime = 10_000_000;
	double deltaUpdate = 1;
	double deltaUpdateWithSpeedModifer = 1;

	int worldSpeedMultiplier = 1;

	long lastFPS;
	long currentFrames;
	long timeSinceLastFPSupdate;
	boolean paused;

	public void start() {
		originalStartTime = System.currentTimeMillis();
		lastUpdateTime = System.nanoTime();

		while (getWorld() != null) {
			runningTime = System.currentTimeMillis() - originalStartTime;

			updateTimeDifference = System.nanoTime() - lastUpdateTime;

			if (updateTimeDifference >= updateCap) {
				lastUpdateTime = System.nanoTime();

				currentFrames++;
				timeSinceLastFPSupdate += updateTimeDifference;
				if (timeSinceLastFPSupdate >= 1_000_000_000) {
					timeSinceLastFPSupdate = 0;
					lastFPS = currentFrames;
					currentFrames = 0;
				}

				deltaUpdate = ((double) updateTimeDifference) / baseDeltaTime;
				if (deltaUpdate > 2) {
					deltaUpdate = 2;
				}

				deltaUpdateWithSpeedModifer = deltaUpdate * worldSpeedMultiplier;

				adjustCameraLocation();
				if (!paused) {
					getWorld().runWorldUpdates(deltaUpdateWithSpeedModifer);
				}
				repaint();
			}
		}
	}

	public void togglePause() {
		paused = !paused;
	}

	public boolean isPaused() {
		return paused;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	private void adjustCameraLocation() {
		if (apressed && !dpressed) {
			moveCamera(1 * deltaUpdate, 0);
		} else if (!apressed && dpressed) {
			moveCamera(-1 * deltaUpdate, 0);
		}

		if (wpressed && !spressed) {
			moveCamera(0, 1 * deltaUpdate);
		} else if (!wpressed && spressed) {
			moveCamera(0, -1 * deltaUpdate);
		}
	}

	private void moveCamera(double dx, double dy) {
		double nextCameraX = currentCamera.getLocation().getX() + (dx * currentCamera.getViewingDistance() * .005);
		double nextCameraY = currentCamera.getLocation().getY() + (dy * currentCamera.getViewingDistance() * .005);

		if (nextCameraX < world.getMinWorldLocation().getX()) {
			currentCamera.getLocation().setX(world.getMinWorldLocation().getX());
		} else if (nextCameraX > world.getMaxWorldLocation().getX()) {
			currentCamera.getLocation().setX(world.getMaxWorldLocation().getX());
		} else {
			currentCamera.getLocation().setX(nextCameraX);
		}

		if (nextCameraY < world.getMinWorldLocation().getY()) {
			currentCamera.getLocation().setY(world.getMinWorldLocation().getY());
		} else if (nextCameraY > world.getMaxWorldLocation().getY()) {
			currentCamera.getLocation().setY(world.getMaxWorldLocation().getY());
		} else {
			currentCamera.getLocation().setY(nextCameraY);
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		BufferedImage worldImage = currentCamera.getBufferedWorldImage(getWorld(), getWidth(), getHeight(), deltaUpdate, lastFPS, worldSpeedMultiplier);
		g2d.drawRenderedImage(worldImage, null);
		if (isPaused()) {
			g2d.setColor(Color.black);
			g2d.fillRect(500, 500, 100, 20);
			g2d.setColor(Color.yellow);
			g2d.drawString("PAUSED", 530, 515);
		}
	}

	public Camera getCurrentCamera() {
		return currentCamera;
	}

	public void setCurrentCamera(Camera currentCamera) {
		this.currentCamera = currentCamera;
	}
}
