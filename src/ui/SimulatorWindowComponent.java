package ui;

import game.World;

import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class SimulatorWindowComponent extends JComponent {

	Camera mainCamera;
	World world;

	public SimulatorWindowComponent(Camera mainCamera){
		this.mainCamera = mainCamera;
	}

	long originalStartTime;
	long lastUpdateTime;
	long updateTimeDifference;
	long runningTime;

	final long updateCap = 700_000;
	final long baseDeltaTime = 10_000_000;
	double deltaUpdate = 1;

	long lastFPS;
	long currentFrames;
	long timeSinceLastFPSupdate;

	public void start() {
		originalStartTime = System.currentTimeMillis();
		lastUpdateTime = System.nanoTime();

		while(getWorld() != null){
			runningTime = System.currentTimeMillis() - originalStartTime;

			updateTimeDifference = System.nanoTime() - lastUpdateTime;

			if(updateTimeDifference >= updateCap) {
				lastUpdateTime = System.nanoTime();

				currentFrames++;
				timeSinceLastFPSupdate += updateTimeDifference;
				if(timeSinceLastFPSupdate >= 1_000_000_000){
					timeSinceLastFPSupdate = 0;
					lastFPS = currentFrames;
					currentFrames = 0;
				}

				deltaUpdate = ((double)updateTimeDifference) / baseDeltaTime;
				getWorld().runWorldUpdates(runningTime, deltaUpdate);
				repaint();
			}
		}
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	@Override
	public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		BufferedImage worldImage = mainCamera.getBufferedWorldImage(getWorld(), getWidth(), getHeight(), deltaUpdate, lastFPS);
		g2d.drawRenderedImage(worldImage, null);
	}

}
