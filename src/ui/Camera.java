package ui;

import game.World;
import game.dna.stats.StatType;
import game.dna.traits.Trait;
import game.world.creatures.Creature;
import game.world.creatures.CreatureState;
import game.world.units.ScaledLocation;
import game.world.units.ScaledSize;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	public BufferedImage getBufferedWorldImage(final World world, double drawingWidth, double drawingHeight, double deltaUpdate, long currentFPS) {
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
		drawCameraInfo(g2d, world, deltaUpdate, currentFPS);
		drawTraitUsageInfo(g2d, world);
		return image;
	}

	private void drawTraitUsageInfo(Graphics2D g2d, World world) {
		Map<Trait, Integer> traitsToNumberCreaturesWithTrait = world.getWorldStatisticsTool().getTraitsToNumberCreaturesWithTrait();
		Set<Trait> keySet = traitsToNumberCreaturesWithTrait.keySet();

		List<Trait> sortedTraits = new ArrayList<>();
		sortedTraits.addAll(keySet);
		Collections.sort(sortedTraits, new Comparator<Trait>() {
			@Override
			public int compare(Trait o1, Trait o2) {
				return traitsToNumberCreaturesWithTrait.get(o1) > traitsToNumberCreaturesWithTrait.get(o2) ? -1 : 1;
			}
		});

		int i = 1;
		for(Trait trait : sortedTraits){
			g2d.setColor(Color.decode("#" + Integer.toHexString(trait.getTraitType().hashCode()).substring(0, 5)));
			g2d.drawString(traitsToNumberCreaturesWithTrait.get(trait) + "\t| " + trait.getTraitDefinition(), (int) cachedWindowWidth - 100,15 * i);
			i++;
		}
	}

	private void drawBackground(Graphics2D g2d){
		g2d.setColor(new Color(0, 150, 0));
		g2d.fillRect(0, 0, (int) cachedWindowWidth, (int) cachedWindowHeight);
	}

	private void drawCameraInfo(Graphics2D g2d, World world, double deltaUpdate, long currentFPS){
		g2d.setColor(Color.WHITE);
		g2d.drawString("Window Width: " + cachedWindowWidth,5, 15);
		g2d.drawString("Window Height: " + cachedWindowHeight, 5, 30);
		g2d.drawString("Aspect Scale: " + cachedMonitorResolutionScale, 5, 45);
		g2d.drawString("TVDW: " + cachedTileViewingDistanceWidth, 5, 60);
		g2d.drawString("TVDH: " + cachedTileViewingDistanceHeight, 5, 75);
		g2d.drawString("Middle Width: " + cachedWindowWidthMiddle, 5, 90);
		g2d.drawString("Middle Height: " + cachedWindowHeightMiddle, 5, 105);
		g2d.drawString("Game Scale: " + cachedStandardSize, 5, 120);
		g2d.drawString("World Day: " + world.getWorldDay(), 5, 135);
		g2d.drawString("fps: " + currentFPS, 5, 150);
		g2d.drawString("creatures: " + world.getCreatures().size(), 5, 165);

		g2d.fillRect((int)Math.ceil(cachedWindowWidthMiddle - 2), (int) Math.ceil(cachedWindowHeightMiddle - 2), 4, 4);
	}

	private void drawCreatures(Graphics2D g2d, World world){
		for(int i = 0; i < world.getCreatures().size(); i++){
			Creature creature = world.getCreatures().get(i);
			if(creature.getCreatureStats().containsKey(StatType.color)){
				g2d.setColor((Color)creature.getCreatureStats().get(StatType.color));
			} else {
				g2d.setColor(Color.RED);
			}

			ScaledLocation scaledLocation = creature.getLocation().getScaledLocation(cachedStandardSize);
			ScaledSize scaledSize = creature.getSize().getScaledSize(cachedStandardSize);
			g2d.fillOval(scaledLocation.getX(x, cachedWindowWidthMiddle, scaledSize.getWidth()), scaledLocation.getY(y, cachedWindowHeightMiddle, scaledSize.getHeight()), scaledSize.getWidth(), scaledSize.getHeight());

			if(creature.getCreatureState() == CreatureState.MATING){
				g2d.setColor(Color.PINK);
				g2d.drawOval(scaledLocation.getX(x, cachedWindowWidthMiddle, scaledSize.getWidth()), scaledLocation.getY(y, cachedWindowHeightMiddle, scaledSize.getHeight()), scaledSize.getWidth(), scaledSize.getHeight());
			}
		}
	}

}
