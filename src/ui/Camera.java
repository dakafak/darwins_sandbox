package ui;

import game.World;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Camera {

	private Dimension resolution;
	private int zoomLevel;
	public Camera(Dimension resolution, int zoomLevel){
		this.resolution = resolution;
		this.zoomLevel = zoomLevel;
	}

	public BufferedImage getRenderedImageForView(World currentWorld){
		return null;
	}

}
