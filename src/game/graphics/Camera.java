package game.graphics;

import game.World;
import game.dna.traits.TraitType;
import game.world.creatures.Creature;
import game.world.units.ScaledLocation;
import game.world.units.ScaledSize;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Camera {

	double x;
	double y;
	double viewingDistanceInTiles;

	public Camera(double x, double y, double viewingDistanceInTiles){
		this.x = x;
		this.y = y;
		this.viewingDistanceInTiles = viewingDistanceInTiles;
	}

	double cachedWindowWidth;
	double cachedWindowHeight;
	double cachedWindowWidthMiddle;
	double cachedWindowHeightMiddle;
	double cachedMonitorResolutionScale;
	double cachedTileViewingDistanceWidth;
	double cachedTileViewingDistanceHeight;
	double cachedStandardSize;
	public BufferedImage getBufferedWorldImage(final World world, double drawingWidth, double drawingHeight) {
		if(drawingWidth != cachedWindowWidth || drawingHeight != cachedWindowHeight){
			cachedWindowWidth = drawingWidth;
			cachedWindowHeight = drawingHeight;
			cachedWindowWidthMiddle = cachedWindowWidth / 2;
			cachedWindowHeightMiddle = cachedWindowHeight / 2;
			cachedMonitorResolutionScale = cachedWindowWidth / cachedWindowHeight;
			cachedTileViewingDistanceHeight = viewingDistanceInTiles;
			cachedTileViewingDistanceWidth = cachedTileViewingDistanceHeight * cachedMonitorResolutionScale;
			cachedStandardSize = cachedWindowHeight / viewingDistanceInTiles;
		}

		BufferedImage image = new BufferedImage((int) drawingWidth, (int) drawingHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		drawBackground(g2d);
		drawCreatures(g2d, world);
		drawCameraInfo(g2d);
		return image;
	}

	private void drawBackground(Graphics2D g2d){
		g2d.setColor(new Color(0, 150, 0));
		g2d.fillRect(0, 0, (int) cachedWindowWidth, (int) cachedWindowHeight);
	}

	private void drawCameraInfo(Graphics2D g2d){
		g2d.setColor(Color.WHITE);
		g2d.drawString("Window Width: " + cachedWindowWidth,5, 15);
		g2d.drawString("Window Height: " + cachedWindowHeight, 5, 30);
		g2d.drawString("Aspect Scale: " + cachedMonitorResolutionScale, 5, 45);
		g2d.drawString("TVDW: " + cachedTileViewingDistanceWidth, 5, 60);
		g2d.drawString("TVDH: " + cachedTileViewingDistanceHeight, 5, 75);
		g2d.drawString("Middle Width: " + cachedWindowWidthMiddle, 5, 90);
		g2d.drawString("Middle Height: " + cachedWindowHeightMiddle, 5, 105);
		g2d.drawString("Game Scale: " + cachedStandardSize, 5, 120);

		g2d.fillRect((int)Math.ceil(cachedWindowWidthMiddle - 2), (int) Math.ceil(cachedWindowHeightMiddle - 2), 4, 4);
	}

	private void drawCreatures(Graphics2D g2d, World world){
		for(Creature creature : world.getCreatures()){
			if(creature.getTraitTypeToTraitMap().get(TraitType.color).getTraitDefinition().equals("tan")){
				g2d.setColor(new Color(32, 84, 84));
			} else if(creature.getTraitTypeToTraitMap().get(TraitType.color).getTraitDefinition().equals("brown")){
				g2d.setColor(new Color(16, 42, 42));
			} else if(creature.getTraitTypeToTraitMap().get(TraitType.color).getTraitDefinition().equals("black")){
				g2d.setColor(Color.BLACK);
			} else if(creature.getTraitTypeToTraitMap().get(TraitType.color).getTraitDefinition().equals("green")){
				g2d.setColor(new Color(0, 100, 0));
			} else if(creature.getTraitTypeToTraitMap().get(TraitType.color).getTraitDefinition().equals("blue")){
				g2d.setColor(Color.BLUE);
			}

			ScaledLocation scaledLocation = creature.getLocation().getScaledLocation(cachedStandardSize);
			ScaledSize scaledSize = creature.getSize().getScaledSize(cachedStandardSize);
			g2d.fillOval(scaledLocation.getX(x, cachedWindowWidthMiddle, scaledSize.getWidth()), scaledLocation.getY(y, cachedWindowHeightMiddle, scaledSize.getHeight()), scaledSize.getWidth(), scaledSize.getHeight());
		}
	}

}
