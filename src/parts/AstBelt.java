package parts; 

import parts.iterators.AsteroidBelt;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import parts.iterators.Oval;
import systemgen.DetailsGen;
import systemgen.SolarPainter;
import static systemgen.SolarPainter.DISTSCALE;
import static systemgen.SolarPainter.SIZESCALE;

public class AstBelt extends SolarObj implements Iterable<Oval> {
  public static final String CLASSTYPE = "asteroid belt";
  
  private static final String[] allTypes = {"sparse",
					    "regular",
					    "dense",
					    "strange"};
  
  //size is scaled to distance, not to planet/star/etc's size
  //our asteroid belt is ~1 AU wide, so it would be 6
  //unfortunately I don't have another asteroid belt to judge a good range from
  //also unfortunately I have to make these a little big, or else it looks bad
  private static final int SIZELOW = 10;
  private static final int SIZERNG = 5;
  
  private static final double LOCBUFFER = 1.5;
  
  //BEGIN DENSITY VALUES
  private static final double DENSELOW = 0.07;
  private static final double DENSERNG = 0.15;
  
  //density = density * [type factor]
  private static final double SPRSEFCTR = 0.1;
  private static final double DENSEFCTR = 1.5;
  
  private double location; //where the asteroid belt orbit starts
  private double size;     //location + size = where the belt ends
  private double area;     //= height * size (incorrect for an arc, but easy)
  private double density;  //density * area of belt = number of asteroids
  private int    number;
  
  private AsteroidBelt asts;
  
  public AstBelt(DetailsGen gen, SolarObj parent) {
    super(gen, parent);
  }
  
  @Override
  protected void setType() {
    this.genType  = CLASSTYPE;
  }

  @Override
  protected void initDescs() {
    //This asteroid belt lies between [planet name] and [planet name].
    //It's a [asteroid belt type] belt. [astBDesc]
    add("sparse", "A few asteroids float lazily here and there.");
    add("sparse", "Only a few asteroids have been caught here.");
    add("sparse", "Give it a few millenia, maybe it'll be a proper asteroid belt one day.");
    
    add("regular", "Your run-of-the-mill asteroid belt.");
    add("regular", "They're probably going to make it more impressive in the movies.");
    add("regular", "Perfect for a fledgling spacefaring race.");
    
    add("dense", "You can see it with the naked eye.");
    add("dense", "Who says a star can't have rings?");
    add("dense", "Just try and fly through THIS one, Han.");
    
    add("strange", "Forgotten relics and ancient ruins have collected here.");
    add("strange", "Odd things float in this inbetween space.");
    add("strange", "A garbage dump for the weird and unwanted.");
  }
  
  @Override
  protected void setSpecifics() {
    //a lot depends on this for belts, so it's here instead of in setType
    this.specType = chooseFromList(allTypes);
      
    double[] locSize = setLocSize();
    this.location    = locSize[0];
    this.size        = locSize[1];
    
    if (checkCollided())
      this.specType = "destroyed";
    
    this.area    = size * SolarPainter.DEFAULTHEIGHT;
    this.density = DENSELOW + r.nextDouble() * DENSERNG;
    this.number  = (int) Math.rint(area * density);
    
    switch(this.specType) {
      case "sparse":
	this.density *= SPRSEFCTR;
	break;
      case "dense":
	this.density *= DENSEFCTR;
	break;
      default:
	break;
    }
    
    //TODO: composition: ice/etc
  }
  
  //returns location and size
  //location is between two consecutive planets/sun/oort cloud, and only belt per each pair
  //size is dependent on the locations array, so it makes sense to do it all here
  private double[] setLocSize() {
    double maxOverFlow = (AsteroidBelt.SIZELOW + AsteroidBelt.SIZERNG) * SolarPainter.SIZESCALE;
    
    //I couldn't figure out a more elegant way to do this
    //each (lows[i], highs[i]) pair is a possible range for the belt to spawn in
    //so (lows[0], highs[0]) is between the sun and the first planet
    ArrayList<Double> lows  = new ArrayList<>();
    ArrayList<Double> highs = new ArrayList<>();
    
    lows.add(0.0 + maxOverFlow); // the sun
    
    //comparator to sort planets by their location, ascending
    Comparator<Planet> sortPlanets = (x, y) -> {
      double diff = x.getLocation() - y.getLocation();
      if (diff < 0)
	return -1;
      else if (diff == 0)
	return 0;
      else
	return 1;
    };
    
    Planet[] planets = parent.getFeatures()
			     .stream()
			     .filter(p -> p.getGen().equals("planet"))
			     .map(p -> (Planet) p)
			     .sorted(sortPlanets)
			     .toArray(Planet[]::new);
    
    //calculations here have to be done in terms of pixels
    //location of planet's edge = (loc * DISTSCALE - rad * SIZESCALE) / DISTSCALE
    for (Planet p : planets) {
      double loc  = p.getLocation() * DISTSCALE;
      double rad  = p.getRadius() * SIZESCALE;      
      double left  = (loc - rad - maxOverFlow) / DISTSCALE;
      double right = (loc + rad + maxOverFlow) / DISTSCALE;
      
      //normally I'd want to directly add/subtract the buffer, but sometimes
      //stars can be very close to the sun and doing so makes low < 0
      left  /= LOCBUFFER;
      right *= LOCBUFFER;

      highs.add(left);
      lows.add(right);
    }
    
    Oort cloud = parent.getFeatures()
		       .stream()
		       .filter(c -> c.getGen().equals("Oort cloud"))
		       .map(c -> (Oort) c)
		       .findAny()
		       .get();

    highs.add(cloud.getLocation());
    
    /*
     * There's three types of ranges I don't want to have:
     * 1) Ranges that are too small, which make the asteroid belt look dumb.
     * 2) Ranges that don't make sense, where low >= high. This is caused when
     *	  the range is smaller than the buffer I added.
     * 3) Ranges where low < 0. This is caused when low is very close to 0,
     *	  and the buffer shoves it over. I could've limited the buffer so it 
     *	  never hit below 0, but lows that close to 0 don't look good anyway.
     */ 
    for (int i = 0; i<lows.size(); i++) {
      double dist = highs.get(i) - lows.get(i);
      if (dist < SIZELOW || lows.get(i) >= highs.get(i) || lows.get(i) <= 0) {
	lows.remove(i);
	highs.remove(i);
	//this is horrible, horrible practice but it's the easiest way
	i--;
      }
    }
    
    //TODO: proper error handling
    if (lows.isEmpty())
      return new double[] {0, 0};
    
    int choice  = r.nextInt(lows.size());
    double low  = lows.get(choice);
    double high = highs.get(choice) - SIZERNG;
    
    double loc         = low + (high - low) * r.nextDouble();
    double returnSize  = r.nextDouble() * SIZERNG;
    
    return new double[] {loc, returnSize};
  }

  //returns true if asteroid belt is too close to another asteroid belt
  private boolean checkCollided() {
    AstBelt[] neighbors = parent.getFeatures()
				.stream()
				.filter(a -> a.getGen().equals("asteroid belt"))
				.toArray(AstBelt[]::new);
    
    for (AstBelt a : neighbors) {
      double otherLoc = a.getLocation();        //the left side of the other belt
      double othrEnd  = otherLoc + a.getSize(); //the right side of the other belt  
      double thisEnd  = this.location + this.size;
      //TODO: give this a half-again buffer for both this and the other belt
      if ((otherLoc < thisEnd && thisEnd < othrEnd) || 
	  (location < othrEnd && othrEnd < thisEnd)) {
	return true;
      }
    }
    
    return false;
  }
  
  @Override
  public ArrayList<SolarObj> generateFeatures() {
    //TODO: asteroid belt features
    return null;
  }
  
  public double getSize() {
    return this.size;
  }
  
  public double getLocation() {
    return this.location;
  }
  
  public double getDensity() {
    return this.density;
  }
  
  //has to be called before the iterator is used
  //TODO: add icy type
  public void inIterator(double[] starLoc, double starRad, int height) {
    this.asts = new AsteroidBelt(starLoc, starRad, number, location, size, height, specType);
  }
  
  @Override
  public Iterator<Oval> iterator() {
    return this.asts;
  }
}
