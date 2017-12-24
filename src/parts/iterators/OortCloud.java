package parts.iterators; 

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import systemgen.SolarPainter;

/*
 * Usually Oort clouds are basically just big asteroid fields, so this class
 * uses AsteroidBelt because that's easy. However, sometimes Oort clouds are big
 * gas "rings" around the star.
 */
public class OortCloud implements Iterator<Oval>{
  private static final double MINSIZE = 10.0;
  
  private AsteroidBelt iter;
  private Queue<Oval> layers;
  
  private final double[] starLoc;
  private final double   starRad;
  private final double   low;
  private final double   rng;
  private final int[]    baseColor;
  private final Random   r;
  
  
  private final boolean usingAsteroids;

  public OortCloud(double[] starLoc, double starRad, int number, 
		   double low, double rng, int height, String type, int[] baseColor) {
    
    this.starLoc   = starLoc;
    this.starRad   = starRad;
    this.low       = low;
    this.rng       = rng;
    this.baseColor = baseColor;
    this.r         = new Random();
    
    usingAsteroids = (!type.equals("gaseous"));
    
    if (usingAsteroids)
      this.iter = new AsteroidBelt(starLoc, starRad, number, low, rng, height, type);
    else
      initLayers();
  }
  
  //TODO: print smaller arcs (using asteroid belt calcs probably)
  private void initLayers() {
    this.layers     = new LinkedList<>();
    double trueLow  = low * SolarPainter.DISTSCALE;
    double size     = rng * SolarPainter.DISTSCALE;
    
    System.out.println("Oort min: " + trueLow);
    System.out.println("Oort range: " + size);
    
    float opacity = 0.1f;
    
    int colorLow   = 40;
    int colorRange = 255 - colorLow;

    int colDiff = 5;

    int red   = colorLow + r.nextInt(colorRange);
    
    int green = colorLow + r.nextInt(colorRange);
    
    int blue  = colorLow + r.nextInt(colorRange);
    
    //gas cloud starts on outside, dark and translucent
    //layers go in, becoming more opaque and brighter
    while (size > MINSIZE) {
      double radius = starRad + trueLow + size;      
      Oval ring = new Oval(rgbToHex(red, green, blue),
			   new double[] {starLoc[0] - radius, starLoc[1] - radius},
			   new double[] {radius*2, radius*2},
			   new double[] {270, 180},
			   opacity
      );
      
      layers.add(ring);
      
      colorLow  += colDiff;
      colorRange = 255 - colorLow;

      red   = colorLow + r.nextInt(colorRange);
      green = colorLow + r.nextInt(colorRange);
      blue  = colorLow + r.nextInt(colorRange);
      
      size         -= 200;
      opacity       = Math.min(1.0f, opacity + 0.01f);
    }
    
    double radius = starRad + trueLow;
    
    double[] centerLoc  = new double[] {starLoc[0] - radius,
					starLoc[1] - radius
    };
    double[] centerSize = new double[] {radius*2, radius*2};
    
    Oval center = new Oval("0x000000", centerLoc, centerSize, new double[] {270, 180}, 1.0f);
    
    layers.add(center);
    
  }
    
  @Override
  public boolean hasNext() {
    if (usingAsteroids)
      return iter.hasNext();
    else {
      System.out.println("Layers left: " + layers.size());
      return !layers.isEmpty();
    }
  }

  @Override
  public Oval next() {
    if (usingAsteroids)
      return iter.next();
    else
      return layers.remove();
  }

  private String rgbToHex(int red, int green, int blue) {    
    red = Math.min(red, 255);
    red = Math.max(red, 0);

    green = Math.min(green, 255);
    green = Math.max(green, 0);

    blue = Math.min(blue, 255);
    blue = Math.max(blue, 0);

    String rHex = Integer.toString(red, 16);
    String gHex = Integer.toString(green, 16);
    String bHex = Integer.toString(blue, 16);


    if (rHex.length() < 2)
      rHex = "0" + rHex;
    if (gHex.length() < 2)
      gHex = "0" + gHex;
    if (bHex.length() < 2)
      bHex = "0" + bHex;

    return "0x" + rHex + gHex + bHex;
  }
  
}
