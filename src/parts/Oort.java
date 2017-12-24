package parts; 

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import parts.iterators.OortCloud;
import parts.iterators.Oval;
import systemgen.DetailsGen;
import systemgen.SolarPainter;

public class Oort extends SolarObj implements Iterable<Oval> {
  public static final String CLASSTYPE = "Oort cloud";
  
  private static final String[] allTypes = {"sparse",
					    "rocky",
					    "icy",
					    "normal",
					    "gaseous",
					    "strange"};
  
  private static final double OUTERBUFFER = 1.2;
  private static final double DEFAULTSIZE = 100;
  
  //BEGIN DENSITY VALUES
  private static final double DENSELOW = 0.003;
  private static final double DENSERNG = 0.007;
  
  private static final double SPARSEFACTOR = 0.1;
  
  private double    size;
  private double    area;
  private double    density;
  private int       number;
  private double    location;
  private OortCloud cloud;
  
  private boolean   habitable;
  private boolean   habitated;

  public Oort(DetailsGen gen, SolarObj parent) {
    super(gen, parent);
  }
  
  @Override
  protected void setType() {
    this.genType  = CLASSTYPE;
  }

  @Override
  protected void initDescs() {
    //This star has a [oortType] Oort cloud surrounding it. [oortDesc]    
    add("sparse", "Here and there, the last remnants of this star's birth float.");
    add("sparse", "Perhaps an ancient race mined this cloud, long ago.");
    add("sparse", "Anything floating here has fallen to the star or its planets long ago.");
    
    add("rocky", "It's pretty much just an asteroid field, but bigger.");
    add("rocky", "A gold mine of useful metals for a young spacefaring race.");
    add("rocky", "Compared to most oort clouds, this is a desert.");
    
    add("icy", "Floating balls of snow and ice have accumulated out here.");
    add("icy", "There's enough frozen water here to give a barren planet oceans.");
    add("icy", "Once in a millenia, one of these will be shaken free and become a comet.");
    
    add("normal", "Once in a millenia, the ice and rock floating here will smash together.");
    add("normal", "A perfect home for the antisocial astronaut.");
    add("normal", "The last stop before the great unknown.");
    
    add("gaseous", "This star is surrounded by a thin cloud of dust and gas.");
    add("gaseous", "From the right angle, it's beautiful.");
    add("gaseous", "An astronomer's worst nightmare.");
    
    add("strange", "What wonders have floated out here, into the dark?");
    add("strange", "You have to wonder how these things got all the way out here.");
    add("strange", "Maybe these things were trying to escape the light.");
  
  }
  
  @Override
  protected void setSpecifics() { 
    this.specType = chooseFromList(allTypes);
    
    this.size     = DEFAULTSIZE;
    this.density  = DENSELOW + r.nextDouble() * DENSERNG;
    this.area     = size * SolarPainter.DEFAULTHEIGHT;
    this.location = setLocation();    
    
    switch(specType) {
      case "sparse":
	this.density *= SPARSEFACTOR;
	break;
      default:
	break;
    }
    
    this.number   = (int) Math.rint(area * density);
  }
  
  private double setLocation() {
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
    
    Planet pluto = parent.getFeatures()
			 .stream()
			 .filter(p -> p.genType.equals("planet"))
			 .map(p -> (Planet) p)
			 .max(sortPlanets)
			 .get();
    
    double plutoLoc  = pluto.getLocation() * SolarPainter.DISTSCALE;
    double plutoRad  = pluto.getRadius() * SolarPainter.SIZESCALE;
    double rightSide = (plutoLoc + plutoRad) / SolarPainter.DISTSCALE;
    
    return rightSide * OUTERBUFFER;
  }
  
  @Override
  public ArrayList<SolarObj> generateFeatures() {
    //TODO: Oort belt features
    return null;
  }

  public double getLocation() {
    return this.location;
  }
  
  public double getOuterRim() {
    return this.location + this.size;
  }
  
  public void inIterator(double[] starLoc, double starRad, int height) {
    Star sol = (Star) parent;
    this.cloud = new OortCloud(starLoc, starRad, number, location, size, height, specType, sol.getColorParts());
  }
  
  @Override
  public Iterator<Oval> iterator() {
    return cloud;
  }


}
