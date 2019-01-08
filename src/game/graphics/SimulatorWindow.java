package game.graphics;

import game.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class SimulatorWindow extends JFrame {

	World world;
	boolean continueRunning = true;
	Camera mainCamera;

	public SimulatorWindow(String title){
		super(title);
		super.setSize(1200, 800);//TODO later set this to full screen
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setLocationRelativeTo(null);
		super.setUndecorated(true);
		super.addKeyListener(new KeyListener() {


			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
					SimulatorWindow.super.dispose();
					System.exit(0);
				} else if(e.getKeyCode() == KeyEvent.VK_M){
					world.tryMatingCreatures();
				}
			}
		});

		mainCamera = new Camera(0, 0, 40);
		setVisible(true);
	}

	@Override
	public void paint(Graphics g){
//		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		BufferedImage worldImage = mainCamera.getBufferedWorldImage(world, getWidth(), getHeight());
		g2d.drawRenderedImage(worldImage, null);
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
		continueRunning = true;
		lastUpdateTime = System.currentTimeMillis();

		while(continueRunning){
			updateTimeDifference = System.currentTimeMillis() - lastUpdateTime;
			runningTime = System.currentTimeMillis() - originalStartTime;

			if(updateTimeDifference >= updateCap) {
				deltaUpdate = ((updateTimeDifference) * 1.0f) / baseDeltaTime;

				//TODO add update calls here
				//TODO add speed functionality - potentially just add a multiplier for deltaUpdate so the game can run at a faster pace if chosen
				world.tellCreaturesToWander(runningTime);
				world.moveCreatures(deltaUpdate);
				refresh();

				lastUpdateTime = System.currentTimeMillis();
			}
		}
	}

	private void refresh(){
		repaint();
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}
}
