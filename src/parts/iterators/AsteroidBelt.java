package parts.iterators;

import java.util.Iterator;
import java.util.Random;
import java.util.function.Supplier;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import systemgen.SolarPainter;

//creates a bunch of asteroids in an arc from the top of the image to the bottom
public class AsteroidBelt implements Iterator<Oval> {
  public static final double SIZELOW = 1;
  public static final double SIZERNG = 5;
  
  public static final String[] rockyColors = {"0xFFFFFF",
					      "0xD3D3D3",
					      "0xA9A9A9"
  };
  
  public static final String[] icyColors = {"0xF0F8FF",
					    "0x5F9EA0",
					    "0x7FFFD4"
  };
  
  //total angle is multiplied by this to give a buffer
  //otherwise no asteroids spawn past the edge of the image and it looks weird
  private static final double ANGLEBUFFER = 1.1;

  private final Asteroids asts;
  
  public AsteroidBelt(double[] starLoc, double starRad, int number, double low, 
		      double rng, int height, String type) {    
    //the minimum distance an asteroid can spawn, from the center of the star
    double minDist = low * SolarPainter.DISTSCALE + starRad;
    
    double[] angles = determineAngles(height, minDist);
    
    String[] typeColors;
    
    switch(type) {
      case "rocky":
	typeColors = rockyColors;
	break;
      case "icy":
	typeColors = icyColors;
	break;
      default:
	typeColors = rockyColors;
//	typeColors = new String[] {"0xFFA500"};
    }
    
    Random r = new Random();
    
    Supplier<String> colors = () -> typeColors[r.nextInt(typeColors.length)];
    
    this.asts = new Asteroids(number, angles[0], angles[1], starLoc, low, rng,
			      colors, SIZELOW, SIZERNG, starRad);
  }
  
  //returns the start and range angles for the belt, in radians
  private double[] determineAngles(int height, double max) {
    double totalAngle = 2.0 * Math.asin(height / (2.0 * max)) * ANGLEBUFFER;
    double halfAngle = totalAngle / 2;

    double startAngle = 2 * Math.PI - halfAngle;
    
    if (Double.isNaN(startAngle))
      return new double[] {1.5 * Math.PI, Math.PI};
    
    return new double[] {startAngle, totalAngle};
  }
  
  @Override
  public boolean hasNext() {
    return asts.hasNext();
  }

  @Override
  public Oval next() {
    return asts.next();  
  }

}