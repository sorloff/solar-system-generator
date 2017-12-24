package parts; 

import java.util.ArrayList;
import systemgen.DetailsGen;
import systemgen.SolarPainter;

public class Moon extends SolarObj {
  public static final String CLASSTYPE  = "moon";
  
  private static final String[] allTypes = {"small",
					    "regular",
					    "massive",
					    "strange"};
  
  private static final String[] sizeDescs = {"tiny",
					     "small",
					     "average",
					     "large",
					     "massive"
  };
  
  private static final String[] distDescs = {"very close to",
					     "close to",
					     "a medium distance from",
					     "fairly far from",
					     "very far from"
  };
  
  //the percentage of terra's radius that all moons' radius cannot exceed
  private static final double WEIGHTSCALE = 0.5;
  
  private static final int DISTLOW = 2;
  private static final int DISTLIM = 3;
  
  private static final int DISTBUFFER = 10;
  
  private static final int SIZELOW = 3;
  private static final int SIZERNG = 15;
  
  //moon locations are in polar coords around their planet
  private double distance;
  private double degree;
  private int    color;
  //TODO: make description array for moon
  private int    sizeDescMark;
  private double radius;
  
  private String supType;
  
  public Moon(DetailsGen gen, SolarObj parent) {
    super(gen, parent);
  }
  
  @Override
  protected void setType() {
    this.genType  = CLASSTYPE;
    if(!this.supType.equals("destroyed"))
      this.specType = chooseFromList(allTypes);
    else
      this.specType = "destroyed";
  }

  @Override
  protected void initDescs() {
    //A [moon type] moon. [moonDesc]    
    add("small", "Just barely visible on the planet's surface.");
    add("small", "It'd make a good anchor for a space elevator, at least.");
    add("small", "Barely a moon at all, really.");
    
    add("regular", "It's average.");
    add("regular", "White and shining.");
    add("regular", "You know, made of cheese.");
    
    add("massive", "On the surface, this moon takes up more than half the sky.");
    add("massive", "A planet in its own right.");
    add("massive", "Solar eclipses are pretty common.");

    add("strange", "It's pretty loony.");
    add("strange", "Bound to inspire a lunatic or two.");
    add("strange", "I was going to put a 'loony' pun here but that seemed too obvious.");
  }
  
  @Override
  protected void setSpecifics() {       
    double rand         = r.nextDouble();
    this.sizeDescMark   = (int) Math.floor(rand * sizeDescs.length);
    this.radius         = SIZELOW + SIZERNG * rand;
    
    this.distance = r.nextDouble() * DISTLIM + DISTLOW;
    this.degree   = r.nextDouble() * 360;
    
    //TODO: color
    //TODO: composition
    //TODO: habitable
    //TODO: life
    
    //collision checking
    Moon[] neighbors = parent.getFeatures().stream()
			     .filter(n -> n.getGen().equals("moon"))
			     .toArray(Moon[]::new);
        
    for (Moon n : neighbors)
      if (checkCollided(n)) {
	this.supType = "destroyed";
	return;
      }
    
    if (checkOverweight()) {
      this.supType = "overweight";
      return;
    }
    
    this.supType = "regular ol moon";
  }
  
  //returns true if this moon has collided with another moon
  private boolean checkCollided(Moon m) {
    Planet terra = (Planet) parent;
    
    double[] coords = getCoords((terra.getRadius()) * SolarPainter.SIZESCALE +
				 this.getDistance() * SolarPainter.MOONSCALE);
    
    double[] others = m.getCoords((terra.getRadius()) * SolarPainter.SIZESCALE +
				   this.getDistance() * SolarPainter.MOONSCALE);
    
    //if the distance between moons is less than the sum of their radius, they've collided
    double diff = Math.sqrt(Math.pow(coords[0] - others[0], 2) + 
			    Math.pow(coords[1] - others[1], 2));
    
    double radii    = (this.getSize() + m.getSize()) * SolarPainter.SIZESCALE;
    
    return diff < radii + DISTBUFFER;
  }
  
  //returns true if this moon is too big for the planet,
  //given both its radius and the neighbor moons' radii
  private boolean checkOverweight() {
    double totalMass = 0.0;
    Planet terra     = (Planet) parent;
    
    Moon[] neighbors = terra.getFeatures()
			    .stream()
			    .filter(f -> f.getGen().equals("moon"))
			    .toArray(Moon[]::new);
    
    for (Moon m : neighbors)
      totalMass += m.getSize();
    
    return totalMass > (terra.getRadius() / WEIGHTSCALE);
  }
  
  @Override
  public ArrayList<SolarObj> generateFeatures() {
    //TODO: lunar features
    //TODO: atmosphere
    return null;
  }
  
  public double getSize() {
    return radius;
  }
  
  public double getDistance() {
    return this.distance;
  }
  
  public double getDegree() {
    return this.degree;
  }
  
  public double getColor() {
    return this.color;
  }
  
  //returns the cartesian coordinates if the planet was at [0, 0]
  //takes the "real" distance from the center of the planet to the moon
  public double[] getCoords(double realDistance) {
    double angle = degree * (Math.PI / 180);
    double x = realDistance * Math.cos(angle);
    double y = realDistance * Math.sin(angle);
    
    return new double[] {x, y};
  }
  
  //same as getCoords, but doesn't take into account the real distance
  public double[] getSimpleCoords() {
    double angle = degree * (Math.PI / 180);
    double x = distance * Math.cos(angle);
    double y = distance * Math.sin(angle);
    
    return new double[] {x, y};
  }
  

  

}
