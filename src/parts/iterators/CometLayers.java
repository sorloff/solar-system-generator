package parts.iterators; 

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import systemgen.SolarPainter;

//creates a bunch of translucent ovals for a comet
public class CometLayers implements Iterator<Oval>{
  private static final int SIZEBUFFER = 10;
  private static final int COLORSTART = 40;
  private static final int RANDOMDIFF = 5;
  private static final int NORMALDIFF = 50;
  
  private double[] coords;
  private double   size;
  private String   type;
  
  //the layers created first will be on the "bottom" of the comet
  Queue<Oval> layers;
  
  private Random r;
  
  public CometLayers(double[] location, double size, String type) {
    this.coords    = new double[2];
    this.coords[0] = location[0];
    this.coords[1] = location[1];
    
    this.type      = type;
    this.size      = size * SolarPainter.SIZESCALE;
    this.layers    = new LinkedList();
    this.r         = new Random();
    
    setLayers();
  }
  
  /*
   * There's two types of comets: regular and colorful
   * 
   * Regular comets generate a single color first. Then, their tails get
   * progressively lighter while remaining close to that base color.
   * 
   * Colorful comets generate a new, progressively lighter color for each layer.
   */
  //TODO: don't get lighter, just get more opaque
  private void setLayers() {
    //the "base" of the comet
    Oval circle = new Oval("0xffffff", 
			     new double[] {coords[0] - size, coords[1] - size},
			     new double[] {size*2, size*2},
			     new double[] {0, 360},
			     1.0f
    );
    layers.add(circle);
    
    //regular comets start off with a single color in a low range
    //colorful comets start off with a single color in a slightly brighter range
    int colorLow   = COLORSTART;
    int colorRange = 255 - colorLow;

    int colDiff = (type.equals("colorful")) ? RANDOMDIFF : NORMALDIFF;

    int red   = (type.equals("colorful")) ? colorLow + r.nextInt(colorRange) : 
					    r.nextInt(COLORSTART);
    
    int green = (type.equals("colorful")) ? colorLow + r.nextInt(colorRange) : 
					    r.nextInt(COLORSTART);
    
    int blue  = (type.equals("colorful")) ? colorLow + r.nextInt(colorRange) : 
					    r.nextInt(COLORSTART);

    String color = rgbToHex(red, green, blue);
    double height = size + SIZEBUFFER;
    int sizDiff   = -5;
    double fact   = 100;
    float alpha   = 0.1f;
    float aDiff   = 0.1f;
    
    //large, transparent layers first, becoming more opaque and thinner
    while (height > 0) {
      double length = fact * height;
      //The tail portion of this layer.
      Oval tail = new Oval(color,
			     new double[] {coords[0] - length, coords[1] - height},
			     new double[] {length*2, height*2},
			     new double[] {270, 180},
			     alpha
      );
      
      //The "base" portion of this layer. Goes in front of the tail.
      Oval body = new Oval(color,
			     new double[] {coords[0] - height, coords[1] - height},
			     new double[] {height*2, height*2},
			     new double[] {90, 180},
			     alpha
      );
      
      layers.add(tail);
      layers.add(body);
      
      height += sizDiff;
      alpha   = Math.min(1.0f, alpha + aDiff);
      
      //regular comets just keep getting lighter, with slight variation
      //colorful comets get a whole new color each time
      if (type.equals("colorful")) {
	colorLow  += colDiff;
	colorRange = 255 - colorLow;

	red   = colorLow + r.nextInt(colorRange);
	green = colorLow + r.nextInt(colorRange);
	blue  = colorLow + r.nextInt(colorRange);
      } else {
	red   += r.nextInt(colDiff);
	green += r.nextInt(colDiff);
	blue  += r.nextInt(colDiff);
      }

      color   = rgbToHex(red, green, blue);
    }
  }
  
  @Override
  public boolean hasNext() {
    return !layers.isEmpty();
  }

  @Override
  public Oval next() {
    return layers.remove();
  }

  //similar to SolarObj::rgbToHex, but returns strings instead
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
