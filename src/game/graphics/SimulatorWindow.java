package game.graphics;

import game.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class SimulatorWindow extends JFrame implements Runnable {

	World world;
	boolean running = true;
	Camera mainCamera;

	public SimulatorWindow(String title){
		super(title);
		super.setSize(800, 800);//TODO later set this to full screen
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
				}
			}
		});

		mainCamera = new Camera(0, 0, 20);
		setVisible(true);
	}

	@Override
	public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		BufferedImage worldImage = mainCamera.getBufferedWorldImage(world, getWidth(), getHeight());
		g2d.drawRenderedImage(worldImage, null);
	}

	@Override
	public void run() {
		while(running){
			repaint();//TODO grab dynamic painting method from blade dodger rebuild and refactor this class
		}
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}
}
