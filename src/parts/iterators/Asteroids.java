package parts.iterators; 

import java.util.Iterator;
import java.util.Random;
import java.util.function.Supplier;
import systemgen.SolarPainter;

//an iterator for anything that prints a bunch of small circles in an arc
public class Asteroids implements Iterator<Oval> {
  private final int      number;
  private final double   angleLow;
  private final double   angleRange;
  private final double   distanceLow;
  private final double   distanceRange;
  private final double   sizeLow;
  private final double   sizeRange;
  private final double[] location;
  private final double   distanceOffset;
  private final Random   r;
  
  private final Supplier<String> colors;
  
  private int currentNumber;
  
  public Asteroids(int number, double angleLow, double angleRange, double[] location,
		   double distanceLow, double distanceRange, Supplier<String> colors,
		   double sizeLow, double sizeRange, double distanceOffset) {
    this.currentNumber  = 0;
    this.number         = number;
    this.angleLow       = angleLow;
    this.angleRange     = angleRange;
    this.location       = location;
    this.distanceLow    = distanceLow;
    this.distanceRange  = distanceRange;
    this.colors         = colors;
    this.sizeLow        = sizeLow;
    this.sizeRange      = sizeRange;
    this.distanceOffset = distanceOffset;
    this.r              = new Random();
  }
  
  @Override
  public boolean hasNext() {
    return currentNumber < number;
  }

  @Override
  public Oval next() {
    currentNumber++;
    
    int asteroidSize = (int) Math.rint((r.nextDouble() * sizeRange + sizeLow) * SolarPainter.SIZESCALE);
    
    //angles can't be above 2pi
    double angle = ((r.nextDouble() * angleRange + angleLow) % (2 * Math.PI));
    double dist  = (r.nextDouble() * distanceRange + distanceLow) * SolarPainter.DISTSCALE + distanceOffset; 
    
    int x = (int) Math.rint(location[0] + dist * Math.cos(angle));
    int y = (int) Math.rint(location[1] + dist * Math.sin(angle));

    String   color  = colors.get();
    double[] coords = new double[] {x, y};
    double[] shape  = new double[] {asteroidSize*2, asteroidSize*2};
    double[] angles = new double[] {0, 360};
    float    alpha  = 1.0f;
        
    return new Oval(color, coords, shape, angles, alpha);
  }

}
