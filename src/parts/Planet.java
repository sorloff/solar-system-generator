package parts; 

import java.util.ArrayList;
import systemgen.DetailsGen;
import systemgen.SolarPainter;

public class Planet extends SolarObj {
  public static final String CLASSTYPE = "planet";
  
  private static final String[] supTypes = {"rocky",
					    "gaseous"
  };
  
  private static final String[] allTypes = {"mercurial",
					    "venusian",
					    "gaseous",
					    "terrestial",
					    "barren",
					    "strange",
  };
  
  private static final String[] tempDescs = {"frozen",
					     "cold",
					     "cool",
					     "temperate",
					     "warm",
					     "hot",
					     "molten"
  };
  
  private static final String[] waterDescs = {"dry",
					      "moist",
					      "wet",
					      "soaked",
					      "submerged"
  };
  
  private static final String[] atmoDescs = {"vacumn",
					     "thin",
					     "moderate",
					     "full",
					     "oppressive"
  };
  
  private static final String[] sizeDescs = {"tiny",
					     "small",
					     "average",
					     "large",
					     "massive"
  };
  
  private static final String[] goldDescs = {"way too close to",
					     "a bit too close to",
					     "just the right distance from ",
					     "a bit too far from",
					     "way too far from"
  };
  
  private String superType; //gaseous, rocky
  
//  private ArrayList<SolarFeature> atmospheres = new ArrayList<>();
  private ArrayList<Type> types;
  
  
  
  private int    temp;
  private int    water;
  private int    atmo;
  private double location;
  private int    gold;
  private int    color;
  private double radius;
  private int    sizeDescMark; //the indice of the size description
  
  private boolean habitable;
  private boolean habitated;
  private boolean lifeBearing;
  private ArrayList<String> composition;
  
  //311 is the distance from pluto to the sun
  //every integer represents 15 million miles
  //earth is 6
  //mercury is 2
  //mars is 9
  //jupiter is 32
  
  private final int MAXLOC    = 400;
  private final int ROCKYMAX  = 30;
  private final int GASSYMIN  = 25;
  
  //the minimum distance between planets, in pixels
  private final int DISTBUFFER = 10;
  
  //Begin size values
  
  //size metric is: 1 = 100 miles
  //our sun is 4000 (400,000 miles)
  //earth is   40
  //venus is   37
  //mars is    21
  //luna is    10
  //mercury is 15
  //jupiter is 400
  //neptune is 152
  
  private final int ROCKLOW = 7;
  private final int ROCKRNG = 60; //TODO: determine good value for this
  
  private final int GASLOW = 100;
  private final int GASRNG = 400;

  
  private final int AVGMOONS     = 2;
  private final double MOONSCALE = 0.01;

  public Planet(DetailsGen gen, SolarObj parent) {
    super(gen, parent);
  }
  
  @Override
  protected void setType() {
    this.genType = CLASSTYPE;
    
    if (this.superType.equals("destroyed")) {
      this.specType = "destroyed";
      return;
    }
    
    //filters types to those that have their rules met
    String[] possibleTypes = types.stream()
	                          .filter(t -> t.match())
				  .map(t -> t.name)
				  .toArray(String[]::new);
    
    System.out.println("Possible types: " + possibleTypes.length);
    
    if (possibleTypes.length==0)
      this.specType = "strange"; //TODO: make a better default type
    else
      this.specType = chooseFromList(possibleTypes);
  }

  @Override
  protected void initDescs() {
    //Planet name: [name].
    //This is a [planet type] planet. [planDesc]
    
    add("mercurial", "It's basically just a tiny lump of rock.");
    add("mercurial", "The days are sweltering, and the nights freezing.");
    add("mercurial", "Half the surface is molten rock. The other half, just rock.");
    
    add("venusian", "It's as dangerous as it is beautiful.");
    add("venusian", "It's a molten wasteland.");
    add("venusian", "The clouds and rain are made of metal and rock.");
    
    add("gaseous", "It might've been a star, if it just tried a little harder.");
    add("gaseous", "Actually, the surface is just there! Yeah, under the clouds. Keep going.");
    add("gaseous", "It's not really good for much, to be honest.");
    
    add("terrestial", "A ship of stone.");
    add("terrestial", "Green and bountiful.");
    add("terrestial", "Class-M.");
    
    add("barren", "A massive ball of rock and metal.");
    add("barren", "At best, good for terraforming practice.");
    add("barren", "Kind of boring.");
    
    add("strange", "God's testing grounds.");
    add("strange", "Some ancient race must have used this place for their creations.");
    add("strange", "A home for oddities and outcasts.");
    
    initTypes();
  }
  
  //adds the rules for various types to types list
  private void initTypes() {
    this.types = new ArrayList<>();
    
    //MERCURIAL RULES
    Type merc = new Type("mercurial");
    types.add(merc);
    merc.add(() -> this.superType.equals("rocky"));
    merc.add(() -> this.gold == 0);
    
    //VENUSIAN RULES
    Type venu = new Type("venusian");
    types.add(venu);
    venu.add(() -> this.superType.equals("rocky"));
    venu.add(() -> this.atmo > 2);
    venu.add(() -> this.temp > 4);
    
    //GASEOUS RULES
    Type gas = new Type("gaseous");
    types.add(gas);
    gas.add(() -> this.superType.equals("gaseous"));
    
    //TERRESTIAL RULES
    Type tera = new Type("terrestial");
    types.add(tera);
    tera.add(() -> this.superType.equals("rocky"));
    tera.add(() -> this.temp != 0 && this.temp != 6);
    tera.add(() -> this.water != 0 && this.water != 4);
    tera.add(() -> this.atmo > 2);
    tera.add(() -> this.radius > 0);
    
    //BARREN RULES
    Type bare = new Type("barren");
    types.add(bare);
    bare.add(() -> this.superType.equals("rocky"));
    bare.add(() -> this.atmo < 3);
    
    
    //STRANGE RULES
    Type stng = new Type("strange");
    types.add(stng);
    stng.add(() -> r.nextDouble() > 0.9);
  }
  

  @Override
  protected void setSpecifics() {
    this.superType = chooseFromList(supTypes);
    
    int minLoc    = superType.equals("rocky") ? 0 : GASSYMIN;
    int maxLoc    = superType.equals("rocky") ? ROCKYMAX : MAXLOC;
    this.location = r.nextDouble() * (maxLoc - minLoc) + minLoc;

    //TODO: water/temp modified by atmosphere
    this.atmo   = superType.equals("rocky") ? atmoDescs.length - 1 : r.nextInt(atmoDescs.length);
    this.water  = r.nextInt(waterDescs.length);
    this.temp   = r.nextInt(tempDescs.length);
    
    double rand         = r.nextDouble();
    this.sizeDescMark   = (int) Math.floor(rand * sizeDescs.length);
    int low             = (this.superType.equals("rocky")) ? ROCKLOW : GASLOW;
    int range           = (this.superType.equals("rocky")) ? ROCKRNG : GASRNG;
    //TODO: restrict radius to be half-again far from the sun
    this.radius         = low + range * rand;
    
    //Begin modifications on above based on location
    Star sParent     = (Star) this.parent;
    int[] goldBounds = sParent.getGoldBounds();
    
    int goldHot = goldBounds[0];
    int goldMin = goldBounds[1];
    int goldMax = goldBounds[2];
    int goldCld = goldBounds[3];    
        
    this.gold = -1;
    
    //modifying temperature and atmosphere accordingly
    //planets too close/too far away have their atmospheres evaporated/frozen
    if (location <= goldHot) {
      this.temp = Math.min(tempDescs.length - 1, temp + 2);
      this.atmo = Math.max(0, atmo - 1);
      gold      = 0;
    } else if (location <= goldMin) {
      this.temp = Math.min(tempDescs.length - 1, temp + 1);
      gold      = 1;
    } else if (goldMin <= location && location <= goldMax) {
      gold      = 2;
      //maybe make things more wet/temperate?
    } else if (location >= goldCld) {
      this.temp = Math.max(0, temp - 1);
      gold      = 4;
    } else if (location >= goldMax) {
      this.temp = Math.max(0, temp - 2);
      this.atmo = Math.max(0, atmo - 1);
      gold      = 3;
    }
    
    System.out.println(goldDescs[gold]);
    
    //if the planet is rocky and small, it can't support a large atmosphere
    if (superType.equals("rocky") && radius < 2)
      this.atmo = Math.min(1, atmo);
    
    //filter sol's features to just planets
    Planet[] neighbors = sParent.getFeatures().stream()
			        .filter(n -> n.getGen().equals("planet"))
			        .toArray(Planet[]::new);
        
    for (Planet n : neighbors)
      if (checkCollided(n))
	this.superType = "destroyed";
    
  }
  
  //returns true if this planet has collided with another planet/the sun
  private boolean checkCollided(Planet p) {
    //distance between this planet and the sun, in pixels
    double sunDist = getLocation() * SolarPainter.DISTSCALE -
		     getRadius() * SolarPainter.SIZESCALE;
    
    //dude why are you in the sun get outta there
    if (sunDist <= 0)
      return true;
    
    double otherLoc = p.getLocation();
    
    //the actual distance between planets, in pixels
    double distance = Math.abs(this.location - otherLoc) * SolarPainter.DISTSCALE -
		      (this.getRadius() + p.getRadius()) * SolarPainter.SIZESCALE * 2;
    
    return distance < DISTBUFFER;
  }
  
  //TODO: cast SolarFeature to SolarObj
  @Override
  public ArrayList<SolarObj> generateFeatures() {
    addMoons();
    return features;
  }
  
  private void addMoons() {
    features.add(new Moon(gen, this));
    
    //TODO: make this so bigger planets have a high lower bound
    int count = r.nextInt((int) Math.rint(AVGMOONS * (this.radius * MOONSCALE)) + 1);
    
    for (int i = 0; i<count; i++) {
      Moon m = new Moon(gen, this);
      System.out.println(m.getSpec());
      if (!m.specType.equals("destroyed") && !m.specType.equals("overweight"))
	features.add(m);
      else if (r.nextDouble() > 0.9)
	i--;
    }
  }
  
  //TODO: remove the + 1
  public double getLocation() {
    return location + 1;
  }

  public double getRadius() {
    return this.radius;
  }
  
  @Override
  public String toString() {
    String description;
    String parentRef = (this.parent==null) ? "null" : this.getParent().reference;    
    
    description = "Name: " + this.name + "\n" +
		  "Parent: " + parentRef + "\n" + //TODO: remove this
		  "A " + this.specType + " " + this.genType + ". " + this.desc + "\n" +
		  "Temperature: " + tempDescs[temp] + "\n" +
		  "Atmosphere: " + atmoDescs[atmo] + "\n" +
		  "Size: " + sizeDescs[sizeDescMark] + "\n" +
		  "Location: " + (location) + "\n" + 
		  "This planet is " + goldDescs[gold] + " its star." + "\n" + "\n";
    
    return description;
  }
}
