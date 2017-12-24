package systemgen; 

import parts.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import parts.iterators.Oval;

//paints a large image of the solar system
public class SolarPainter {
  //1 = 100 miles
  public static final double SIZESCALE = 2;  
  //1 = 15mil miles
  public static final double DISTSCALE = 35;
  //the scale at which moon's distances are calculated
  public static final double MOONSCALE = 15;
  
  
  //the width of the area of the star we want to display
  private static final int STARDISPLAY   = 1000;
  private static final int LINETHICKNESS = 2;
  
  public static final double OUTERBUFFER   = 1.2;
  public static final int    DEFAULTHEIGHT = 5001;
  
    
  private final int        center;
  private final SolarObj   system;
  private final Random     r;
  
  private Graphics2D g;
  private final int width;
  private final int height;
  
  private final HashMap<String, Integer> ranking;
  
  
  public SolarPainter(SolarObj system) {
    this.system  = system;
    this.height  = DEFAULTHEIGHT;
    this.r       = new Random();
    this.ranking = new HashMap<>();
        
    Star sol   = (Star) system.getFeatures().get(0);
    Oort cloud = sol.getFeatures()
		    .stream()
		    .filter(c -> c.getGen().equals("Oort cloud"))
		    .map(c -> (Oort) c)
		    .findAny()
		    .get();
    
    this.width  = (int) Math.rint(cloud.getOuterRim() * DISTSCALE + getStarBound(sol));
    this.center = (int) Math.rint(height * 0.5);
    
    initRankings();
  }
  
  private void initRankings() {
    ranking.put(Oort.CLASSTYPE,        1);
    ranking.put(Star.CLASSTYPE,        2);
    ranking.put(Planet.CLASSTYPE,      3);
    ranking.put(Moon.CLASSTYPE,        4);
    ranking.put(AstBelt.CLASSTYPE,     5);
    ranking.put(Comet.CLASSTYPE,       6);
    ranking.put(Feature.CLASSTYPE,     7);
    ranking.put(SolarSystem.CLASSTYPE, 8);
  }
  
  public void makeImage() {
    BufferedImage image;
    
    try {
      image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    } catch (OutOfMemoryError e) {
      System.out.println("SolarPainter::makeImage had to use BigBuffered Image.");
      System.out.println("Image size was: (" + width + ", " + height + ")");
      return;
//      image = BigBufferedImage.create(width, height, BigBufferedImage.TYPE_INT_ARGB);
    }
    
    image.setAccelerationPriority(1);
    this.g = image.createGraphics();

    g.setStroke(new BasicStroke(LINETHICKNESS, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
    paintImage();
    
    try {
      String path = Paths.get(".").toAbsolutePath().normalize().toString();
      ImageIO.write(image, "png", new File(path + "/solar_system.png"));
    } catch (IOException e) {
      System.out.println("Program could not write to directory.");
      System.out.println("IOException: " + e);
    }
  }
  
  private void paintImage() {
    ArrayList<SolarObj> parts = collectParts(system);
    
    parts.add(system);
    
    //sort parts by the ranking defined in initRankings()
    SolarObj[] sortedParts = parts.stream()
				  .sorted((x, y) -> ranking.get(x.getGen()) - ranking.get(y.getGen()))
				  .toArray(SolarObj[]::new);
    
    for (SolarObj s : sortedParts)
      drawHelper(s);
  }
  
  private ArrayList<SolarObj> collectParts(SolarObj s) {
    ArrayList<SolarObj> parts = new ArrayList<>();
    
    ArrayList<SolarObj> features = s.getFeatures();
    parts.addAll(features);
    
    for (SolarObj f : features)
      parts.addAll(collectParts(f));
    
    return parts;
  }
  
  private void drawHelper(SolarObj s) {
    switch (s.getGen()) {
      case "system":
	drawSystem(s);
	break;
      case "star":
	drawStar(s);
	break;
      case "planet":
	drawPlanet(s);
	break;
      case "moon":
	drawMoon(s);
	break;
      case "asteroid belt":
	drawBelt(s);
	break;
      case "comet":
	drawComet(s);
	break;
      case "Oort cloud":
	drawOort(s);
	break;
      case "feature":
//	drawFeature(s);
	break;
      default:
	System.out.println("Couldn't find type: '" + s.getGen() + "' in SolarPainter.");
    }
    
  }
  
  //this is basically just the background of the system
  //this has to be drawn last, or else I can't do clever things with composites
  private void drawSystem(SolarObj s) {
    Composite original = g.getComposite();
    
    g.setColor(Color.black);
    AlphaComposite over = AlphaComposite.getInstance(AlphaComposite.DST_OVER);
    g.setComposite(over);
    g.fillRect(0, 0, width, height);
    
    g.setComposite(original);
    
    //TODO: draw background stars
  }
  
  private void drawStar(SolarObj s) {
    Star sol = (Star) s;
        
    int radius = (int) Math.rint(getStarRadius(sol));

    //Begin drawing goldilock's zone
    int[] goldBounds = sol.getGoldBounds();
    
    int goldHot = goldBounds[0];
    int goldMin = goldBounds[1];
    int goldMax = goldBounds[2];
    int goldCld = goldBounds[3];
    
    System.out.println("Gold bounds: (" + goldHot + ", " + goldMin +", " +
					  goldMax + ", " + goldCld + ")");
    
    
    int realBnd = (int) getStarBound(sol);
    
    int   size;
    double[] loc;
    
    //Gold Cold
    g.setColor(Color.darkGray);
    size = (int) Math.rint(getGoldRadius(goldCld, sol));
    loc  = getGoldLoc(goldCld, sol);
    System.out.println("Gold Cold: " + size);
    g.fillOval((int) Math.rint(loc[0]), (int) Math.rint(loc[1]), size*2, size*2);
    
    //Gold Max
    g.setColor(Color.gray);
    size = (int) Math.rint(getGoldRadius(goldMax, sol));
    loc  = getGoldLoc(goldMax, sol);
    System.out.println("Gold Max: " + size);
    g.fillOval((int) Math.rint(loc[0]), (int) Math.rint(loc[1]), size*2, size*2);
    
    //Gold Min
    g.setColor(Color.lightGray);
    size = (int) Math.rint(getGoldRadius(goldMin, sol));
    loc  = getGoldLoc(goldMin, sol);
    System.out.println("Gold Min: " + size);
    g.fillOval((int) Math.rint(loc[0]), (int) Math.rint(loc[1]), size*2, size*2);
    
    //Gold Hot
    g.setColor(Color.black);
    size = (int) Math.rint(getGoldRadius(goldHot, sol));
    loc  = getGoldLoc(goldHot, sol);
    System.out.println("Gold Hot: " + size);
    g.fillOval((int) Math.rint(loc[0]), (int) Math.rint(loc[1]), size*2, size*2);
    
    
    
    //Begin drawing actual star
    
    System.out.println("Sun Radius: " + radius);
    System.out.println("Luminosity: " + sol.getLuminosity());
    
    double[] coords = getStarLoc(sol);
    int x = (int) Math.rint(coords[0]);
    int y = (int) Math.rint(coords[1]);
    
    System.out.println("Sun Coords: (" + x + ", " + y + ")");
    
    g.setColor(Color.decode(Integer.toString(sol.getColor())));
    g.fillOval(x, y, radius*2, radius*2);
    
  }
  
  //TODO: for testing purposes, draw max moon range around each planet
  private void drawPlanet(SolarObj s) {
    Planet terra = (Planet) s;
    Star sol     = (Star) terra.getParent();
        
    //the "real" radius of the planet
    int radius = (int) Math.rint(getPlanetRadius(terra));
    
    double[] coords = getPlanetLoc(terra);
    int x = (int) Math.rint(coords[0]);
    int y = (int) Math.rint(coords[1]);
    
    System.out.println("Planet raw distance :" + terra.getLocation());
    System.out.println("Planet location: (" + x + ", " + y + ")");
    System.out.println("Planet radius: " + radius);
    
    //begin orbit calculations:
    g.setColor(Color.gray);
    int planetCenter = (int) Math.rint(terra.getLocation() * DISTSCALE);
    int orbitRad     = (int) Math.rint(getStarRadius(sol) + planetCenter);
    double[] sunLoc  = getStarLoc(sol);
    
    //the orbit trail needs to be scaled and positioned around the sun
    int orbitX = (int) Math.rint(sunLoc[0] - planetCenter);
    int orbitY = (int) Math.rint(sunLoc[1] - planetCenter);
    
    g.drawOval(orbitX, orbitY, orbitRad*2, orbitRad*2);
    
    
    g.setColor(Color.white);
    g.fillOval(x, y, radius*2, radius*2);
    
    //make gaseous planets a little easier to identify
    if (terra.getSpec().equals("gaseous")) {
      int start  = (int) Math.rint(radius * 0.75);
      int end    = (int) Math.rint(radius * 0.5);
      g.setColor(Color.black);
      g.fillOval(x + radius - start, y + radius - start, start*2, start*2);
      
      g.setColor(Color.white);
      g.fillOval(x + radius - end, y + radius - end, end*2, end*2);
    }
    
    //make strange planets a little more strange
    //we're the same old crew of planets, that you knew, but maybe just a little more strange
    if (terra.getSpec().equals("strange")) {
      g.setColor(Color.white);
      boolean white = true;
      int xTrue = x + radius;
      int yTrue = y + radius;
      for (int i = 0; i < radius; i++) {
	if (white) {
	  white = false;
	  g.setColor(Color.black);
	}
	else {
	  white = true;
	  g.setColor(Color.white);
	}
	
	g.drawOval(xTrue - i, yTrue - i, i*2, i*2);
      }
    }
  }
  
  private void drawMoon(SolarObj s) {
    Moon luna    = (Moon) s;
    
    int size        = (int) Math.rint(getMoonRadius(luna));
    double[] coords = getMoonLoc(luna);
    
    int x = (int) Math.rint(coords[0]);
    int y = (int) Math.rint(coords[1]);
    
    g.setColor(Color.white);
    g.fillOval(x, y, size*2, size*2);
    
    int mark = (int) Math.rint(size * 0.5);
    g.setColor(Color.black);
    g.fillOval(x + size - mark, y + size - mark, mark*2, mark*2);
  }
  
  private void drawBelt(SolarObj s) {
    AstBelt anoat = (AstBelt) s;
    Star sol      = (Star) s.getParent();
        
    double[] sLoc    = getStarLoc(sol);
    double sRad      = getStarRadius(sol);
    double[] starLoc = new double[] {sLoc[0] + sRad, sLoc[1] + sRad};
    
    anoat.inIterator(starLoc, sRad, height);
    drawOvals(anoat);
    
    System.out.println("Belt type: " + anoat.getSpec());
  }
  
  private void drawComet(SolarObj s) {
    AffineTransform originTrans = g.getTransform();
    
    Comet halley = (Comet) s;
    Star sol     = (Star) s.getParent();
    
    double[] cometLoc = getCometLoc(halley);
    double[] starLoc  = getStarLoc(sol);
    double   starRad  = getStarRadius(sol);
    
    starLoc[0] += starRad;
    starLoc[1] += starRad;
        
    System.out.println("Comet type: " + halley.getSpec());
    
    g.transform(halley.getRotation(starLoc, cometLoc));
    
    halley.inIterator(cometLoc);
    drawOvals(halley);
    
    g.setTransform(originTrans);
  }
  
  private void drawOort(SolarObj s) {
    Oort cloud = (Oort) s;
    Star sol   = (Star) cloud.getParent();
    
    System.out.println("Oort cloud type: " + cloud.getSpec());
    
    double[] sLoc    = getStarLoc(sol);
    double sRad      = getStarRadius(sol);
    double[] starLoc = new double[] {sLoc[0] + sRad, sLoc[1] + sRad};
    
    cloud.inIterator(starLoc, sRad, height);
    drawOvals(cloud);
  }
  
  //draws the ovals for anything that uses a bunch of ovals
  private void drawOvals(Iterable<Oval> s) {
    Composite originalComposite = g.getComposite();
    
    for (Oval part : s) {
      int[] coords   = part.getCoords();
      int[] shape    = part.getShape();
      int[] angles   = part.getAngles();
      Composite comp = part.getComp();
      
      g.setColor(Color.decode(part.getColor()));
      g.setComposite(comp);
      
      g.fillArc(coords[0], coords[1], shape[0], shape[1], angles[0], angles[1]);
    }
    
    g.setComposite(originalComposite);
  }

  //BEGIN HELPER FUNCTIONS
  private double getStarRadius(Star sol) {
    return sol.getRadius() * SIZESCALE;
  }
  
  private double getStarBound(Star sol) {
    double radius = getStarRadius(sol);
    return (radius < STARDISPLAY) ? radius : STARDISPLAY;
  }
  
  private double[] getStarLoc(Star sol) {
    double radius = getStarRadius(sol);
    double x = (radius < STARDISPLAY) ? -radius : -2 * radius + STARDISPLAY;
    double y = center - radius;
    
    return new double[] {x, y};
  }
  
  private double[] getPlanetLoc(Planet terra) {
    Star sol = (Star) terra.getParent();
    
    double bound  = getStarBound(sol);
    double radius = getPlanetRadius(terra);
    
    double x =  terra.getLocation() * DISTSCALE - radius + bound;
    double y =  height * 0.5 - radius;
    
    return new double[] {x, y};
  }
  
  private double getPlanetRadius(Planet terra) {
    return terra.getRadius() * SIZESCALE;
  }
  
  private double[] getMoonLoc(Moon luna) {
    Planet terra          = (Planet) luna.getParent();
    double size           = getMoonRadius(luna);
    double[] planetCoords = getPlanetLoc(terra);
    
    double offX = planetCoords[0];
    double offY = planetCoords[1];
    
    double planetRadius = getPlanetRadius(terra);
    
    double[] coords = luna.getCoords(getPlanetRadius(terra) +
				     luna.getDistance() * MOONSCALE);
    
    double x = offX + coords[0] + planetRadius - size;
    double y = offY + coords[1] + planetRadius - size;
    
    return new double[] {x, y};
  }
  
  private double getMoonRadius(Moon luna) {
    return luna.getSize() * SIZESCALE;
  }
  
  private double getGoldRadius(int size, Star sol) {
    return size * DISTSCALE + getStarRadius(sol);
  }
  
  private double[] getGoldLoc(int size, Star sol) {
    double[] sunLoc = getStarLoc(sol);
    
    return new double[] {sunLoc[0] - size * DISTSCALE,
		         sunLoc[1] - size * DISTSCALE};
  }
  
  private double[] getCometLoc(Comet halley) {
    Star sol     = (Star) halley.getParent();
    double[] loc = halley.getLocation();
        
    loc[0] = loc[0] * DISTSCALE + getStarBound(sol);
    loc[1] = loc[1] * DISTSCALE;
    
    return loc;
  }
}
