package parts; 

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import systemgen.DetailsGen;
import parts.iterators.CometLayers;
import parts.iterators.Oval;
import systemgen.SolarPainter;

public class Comet extends SolarObj implements Iterable<Oval>{
  public static final String CLASSTYPE  = "comet";
  
  private static final String[] allTypes = {"small",
					    "great",
					    "regular",
					    "colorful",
					    "strange"};
  
  //size = size * [type mod]
  private static final double SMALLMOD = 0.5;
  private static final double GREATMOD = 1.7;
  
  //the default size for comets
  //this isn't random because it's hard enough already to make comets look decent
  private static final double REGSIZE  = 20;

  //comets should spawn a bit away from the sun or else the angle is such that
  //their tails get cut off
  private static final int XLOW = 100;
  private static final int XRNG = 300;
  
  //max "height" of a planet (from the center) is ~17
  //max "height" of the image is ~150
  //comets should spawn closer to planets or else their tails get cut off
  private static final int YLOW = 30;
  private static final int YRNG = 20;
  
  //x is just x, but y is the distance from the middle of the image
  private double[]    location;
  private double      size;
  private double      side;
  private CometLayers layers;
  
  public Comet(DetailsGen gen, SolarObj parent) {
    super(gen, parent);
  }

  @Override
  protected void setType() {
    this.genType  = CLASSTYPE;
  }
  

  @Override
  protected void initDescs() {
    //This system is home to a [comet type] comet. [cometDesc]
    
    add("small", "One day it'll burn up, and that'll be that.");
    add("small", "Good practice for amateur astronomers.");
    add("small", "Nobody's going to write home about it.");
    
    add("regular", "Your average comet. Still very pretty.");
//    add("regular", "");
//    add("regular", "");
    
    add("great", "A frozen giant.");
    add("great", "Let's just hope it never hits anything.");
    add("great", "Poets will tell tales of its majesty.");

    add("colorful", "Ooh, pretty!");
    add("colorful", "Poets will write tales of its beauty.");
    add("colorful", "A diamond in the cosmic rough.");
    
    add("strange", "A harbringer. Or, just a bringer.");
    add("strange", "Poets will write tales of it, for better or for worse.");
    add("strange", "From what forgotten star did these things travel?");
  }
  
  @Override
  protected void setSpecifics() {
    this.specType = chooseFromList(allTypes);
    
    //the center of the image, in the distance scale
    double center = SolarPainter.DEFAULTHEIGHT / 2 / SolarPainter.DISTSCALE;
    
    //Comets should spawn in either the upper or lower areas between planets
    //and the edge of the image. Side = -1 means the comet is on top.
    this.side     = (r.nextBoolean()) ? -1.0 : 1.0;
    this.location = new double[] {XLOW + r.nextDouble() * XRNG,
	         center + side * (YLOW + r.nextDouble() * YRNG)
    };
    
    this.size     = REGSIZE;
    
    switch(specType) {
      case "small":
	size *= SMALLMOD;
	break;
      case "great":
	size *= GREATMOD;
	break;
    }
  }
  
  private boolean checkCollided() {
    return false;
  }

  @Override
  public ArrayList<SolarObj> generateFeatures() {
    return null;
  }
  
  public double[] getLocation() {
    return location;
  }
  
  //returns the transformation that rotates the image such that
  //the comet's tail is pointed away from the sun
  public AffineTransform getRotation(double[] sunCoords, double[] thisCoords) {
    AffineTransform trans = new AffineTransform();
    
    double x = thisCoords[0] - sunCoords[0];
    double y = thisCoords[1] - sunCoords[1];
        
    double theta = Math.atan2(y, x);
    
    trans.rotate(theta, thisCoords[0], thisCoords[1]);
    return trans;
  }
  
  //has to be called before the iterator is used
  public void inIterator(double[] trueLocation) {
    this.layers = new CometLayers(trueLocation, size, specType);
  }

  @Override
  public Iterator<Oval> iterator() {
    return this.layers;
  }


}
