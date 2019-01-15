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
	long frameRateResetTime;
	long updateTimeDifference;
	long runningTime;

	int updateCap = 7;
	int baseDeltaTime = 10;
	double deltaUpdate = 1;

	public void start() {
		lastUpdateTime = System.currentTimeMillis();

		while(getWorld() != null){
			updateTimeDifference = System.currentTimeMillis() - lastUpdateTime;
			runningTime = System.currentTimeMillis() - originalStartTime;

			if(updateTimeDifference >= updateCap) {
				deltaUpdate = ((updateTimeDifference) * 1.0f) / baseDeltaTime;
				//TODO add speed functionality - potentially just add a multiplier for deltaUpdate so the game can run at a faster pace if chosen
				getWorld().runWorldUpdates(runningTime, deltaUpdate);
				repaint();

				lastUpdateTime = System.currentTimeMillis();
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
		BufferedImage worldImage = mainCamera.getBufferedWorldImage(getWorld(), getWidth(), getHeight(), deltaUpdate);
		g2d.drawRenderedImage(worldImage, null);
	}

}
