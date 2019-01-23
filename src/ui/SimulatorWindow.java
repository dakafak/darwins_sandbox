package ui;

import game.World;

import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class SimulatorWindow extends JFrame {

	SimulatorWindowComponent simulatorWindowComponent;

	public SimulatorWindow(String title, World world){
		super(title);
		super.setSize(1800, 900);//TODO later set this to full screen
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setLocationRelativeTo(null);
		super.setUndecorated(true);
		super.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_W){
					simulatorWindowComponent.wpressed = true;
				} else if(e.getKeyCode() == KeyEvent.VK_S){
					simulatorWindowComponent.spressed = true;
				} else if(e.getKeyCode() == KeyEvent.VK_A){
					simulatorWindowComponent.apressed = true;
				} else if(e.getKeyCode() == KeyEvent.VK_D){
					simulatorWindowComponent.dpressed = true;
				}

				if(e.getKeyCode() == KeyEvent.VK_CLOSE_BRACKET){
					if(simulatorWindowComponent.worldSpeedMultiplier < 5) {
						simulatorWindowComponent.worldSpeedMultiplier += 1;
					}
				}

				if(e.getKeyCode() == KeyEvent.VK_OPEN_BRACKET){
					if(simulatorWindowComponent.worldSpeedMultiplier > 1){
						simulatorWindowComponent.worldSpeedMultiplier -= 1;
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_W){
					simulatorWindowComponent.wpressed = false;
				} else if(e.getKeyCode() == KeyEvent.VK_S){
					simulatorWindowComponent.spressed = false;
				} else if(e.getKeyCode() == KeyEvent.VK_A){
					simulatorWindowComponent.apressed = false;
				} else if(e.getKeyCode() == KeyEvent.VK_D){
					simulatorWindowComponent.dpressed = false;
				} else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
					SimulatorWindow.super.dispose();
					System.exit(0);
				} else if(e.getKeyCode() == KeyEvent.VK_M){
					world.tryMatingCreatures();
				}
			}
		});
		super.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(e.getWheelRotation() < 0){
					System.out.println("doing things");
					simulatorWindowComponent.getCurrentCamera().zoomIn();
				} else if(e.getWheelRotation() > 0){
					System.out.println("doing things");
					simulatorWindowComponent.getCurrentCamera().zoomOut();
				}
			}
		});

		Camera mainCamera = new Camera(0, 0, 80);
		simulatorWindowComponent = new SimulatorWindowComponent(mainCamera);
		simulatorWindowComponent.setWorld(world);
		super.add(simulatorWindowComponent);
		setVisible(true);
		simulatorWindowComponent.start();
	}

}
