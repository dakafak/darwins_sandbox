package ui;

import game.World;

import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class SimulatorWindowComponent extends JComponent {

	Camera currentCamera;
//	Camera mainCamera;
	World world;

	boolean wpressed;
	boolean spressed;
	boolean apressed;
	boolean dpressed;

	public SimulatorWindowComponent(Camera mainCamera){
		this.currentCamera = mainCamera;
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
				adjustCameraLocation();
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

	private void adjustCameraLocation(){
		if(apressed && !dpressed){
			moveCamera(1 * deltaUpdate, 0);
		} else if(!apressed && dpressed){
			moveCamera(-1 * deltaUpdate, 0);
		}

		if(wpressed && !spressed){
			moveCamera(0, 1 * deltaUpdate);
		} else if(!wpressed && spressed){
			moveCamera(0, -1 * deltaUpdate);
		}
	}

	private void moveCamera(double dx, double dy){
		double nextCameraX = currentCamera.getLocation().getX() + dx;
		double nextCameraY = currentCamera.getLocation().getY() + dy;

		if(nextCameraX < world.getMinWorldLocation().getX()){
			currentCamera.getLocation().setX(world.getMinWorldLocation().getX());
		} else if(nextCameraX > world.getMaxWorldLocation().getX()){
			currentCamera.getLocation().setX(world.getMaxWorldLocation().getX());
		} else if(nextCameraX >= world.getMinWorldLocation().getX()
				&& nextCameraX <= world.getMaxWorldLocation().getX()) {
			currentCamera.getLocation().setX(nextCameraX);
		}

		if(nextCameraY < world.getMinWorldLocation().getY()){
			currentCamera.getLocation().setY(world.getMinWorldLocation().getY());
		} else if(nextCameraY > world.getMaxWorldLocation().getY()){
			currentCamera.getLocation().setY(world.getMaxWorldLocation().getY());
		} else if(nextCameraY >= world.getMinWorldLocation().getY()
				&& nextCameraY <= world.getMaxWorldLocation().getY()){
			currentCamera.getLocation().setY(nextCameraY);
		}

//		System.out.println(currentCamera.getLocation().getX());
	}

	@Override
	public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		BufferedImage worldImage = currentCamera.getBufferedWorldImage(getWorld(), getWidth(), getHeight(), deltaUpdate, lastFPS);
		g2d.drawRenderedImage(worldImage, null);
	}

}
