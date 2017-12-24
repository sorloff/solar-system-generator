package parts; 

import java.util.ArrayList;
import systemgen.DetailsGen;

public class Star extends SolarObj{
  public static final String   CLASSTYPE = "star";
  
  private static final String[] allTypes  = {"red dwarf",
					     "white dwarf",
					     "regular",
					     "blue giant",
					     "red giant",
					     "red supergiant"};
  
  private static final String MS = "regular";
  private static final String RS = "red supergiant";
  private static final String RG = "red giant";
  private static final String BG = "blue giant";
  private static final String WD = "white dwarf";
  private static final String RD = "red dwarf";
  
  //BEGIN SIZE DATA
  
  //size metric is: 1 = 100 miles
  //our sun is 4000 (400,000 miles)
  
  //size = (SIZELOW + random(SIZERND)) * [WDSIZE, RDSIZE, etc]
  private static final double WDSIZE = 0.01; //0.8
  private static final double RDSIZE = 0.1;  //0.9
  private static final double BGSIZE = 10;   //10
  private static final double RGSIZE = 100;  //10
  private static final double RSSIZE = 1000; //15
  
  private static final int SIZELOW = 3000; //300
  private static final int SIZERND = 2000; //200

  
  private static final int AVGPLANETS = 2;
  private static final int AVGASTBELT = 5;
  private static final int AVGCOMET   = 3;
  
  
  //Begin base Goldilock's Zone values
  //TODO: push out and expand GZ to make it look better
  private final int BASEHOT = 4;
  private final int BASEMIN = 6;
  private final int BASEMAX = 12;
  private final int BASECLD = 14;
  private int[] goldBounds;
  
  //This is the HR diagram, in ASCII. Kind of.
  /*
   * MS: **
   * RS: !!
   * RG: ^^
   * BG: ##
   * WD: ()
   * RD: ..
   * 
   * -- O  B  A  F  G  K  M
   * -- 01 02 03 04 05 06 07
   * 10 ## ## ## ## !! !! !!
   * 09 ## ## ## ## !! !! !!
   * 08 ## ## ## ## ^^ ^^ ^^
   * 07 ** ** ** ** ^^ ^^ ^^
   * 06 ** ** ** ** ** ^^ ^^
   * 05 ** ** ** ** ** .. .. //sol is (05, F)
   * 04 () () ** ** ** .. ..
   * 03 () () () ** ** .. ..
   * 02 () () () .. .. .. ..
   * 01 () () () .. .. .. ..
   */
  
  //this is described by the comment above, but flipped along the x axis
  private static String[][] typeMap = {new String[] {WD, WD, WD, RD, RD, RD, RD},
				       new String[] {WD, WD, WD, RD, RD, RD, RD},
				       new String[] {WD, WD, WD, MS, MS, RD, RD},
				       new String[] {WD, WD, MS, MS, MS, RD, RD},
				       new String[] {MS, MS, MS, MS, MS, RD, RD},
				       new String[] {MS, MS, MS, MS, MS, RG, RG},
				       new String[] {MS, MS, MS, MS, RG, RG, RG},
				       new String[] {BG, BG, BG, BG, RG, RG, RG},
				       new String[] {BG, BG, BG, BG, RS, RS, RS},
				       new String[] {BG, BG, BG, BG, RS, RS, RS}
  };
    
  private static String[] colorTypes   = {"blue", "blue", "blue", "white", "yellow", "orange", "red"};
  private static double[] effectiveLum = {0.2, 0.4, 0.6, 0.7, 0.8, 1.4, 1.5, 1.8, 2, 2.5};
  private static int[]    realTemps    = {40, 20, 10, 7, 5, 4, 3};
  
  private int   mass;
  private int   radius;
  private int   luminosity;
  private int   temp;
  private int   color;
  private int[] colorParts;
  
  public Star(DetailsGen gen, SolarObj parent) {
    super(gen, parent);
  }
    
  @Override
  protected void setType() {
    this.genType  = CLASSTYPE;
    this.specType = typeMap[luminosity - 1][temp - 1];
  }

  @Override
  protected void initDescs() {
    //Star name: [name]
    //This is a [star type] star. [starDesc]
    add("red dwarf", "It's old and decrepit, as far as stars go.");
    add("red dwarf", "A small, red lump of burning gas.");
    add("red dwarf", "One day this star will cool to a small, burning husk. Well, moreso than it already has.");
    add("red dwarf", "God, what an awful show.");

    
    add("white dwarf", "This star probably got bullied by other stars back in star school.");
    add("white dwarf", "It's the ghost of some long-forgotten sun.");
    add("white dwarf", "It's the dead-white bones of some ancient sun.");
    
    add("regular", "A warm yellow star.");
    add("regular", "Your average star. Yellow, not too hot, not too cold. Just right.");
    add("regular", "Hey, average is just fine.");
    
    add("blue giant", "One day this star will expand, and engulf all you see before you.");
    add("blue giant", "It's the top of its class.");
    add("blue giant", "It's big, hot, and in the prime of its youth.");
    
    add("red giant", "It's not hot enough to blue, not big enough to be super.");
    add("red giant", "This star definitely used to bully other stars back in star school.");
    add("red giant", "A second chance for those far-off icy planets.");
    
    add("red supergiant", "It's old and fat.");
    add("red supergiant", "But bigger isn't always better.");
    add("red supergiant", "An interstellar behemoth.");  
  }
  
  //TODO: set this to approx the probablity function of actual stars
  @Override
  protected void setSpecifics() {
    //1 = 10e-4; 10 = 10e6
    this.luminosity = r.nextInt(10) + 1;
    
    //1-7:
    //O B A F G K M
    this.temp = r.nextInt(7) + 1;
    
    this.radius     = setRadius();
    this.mass       = setMass();
    this.color      = setColor(colorTypes[temp - 1]);
    this.goldBounds = setGoldBounds();
  }
  
  //mass is correlated with going up the chart
  //white dwarfs have the same mass of stars + 2 their size
  private int setMass() {
    String t  = typeMap[luminosity - 1][temp - 1];
    int value = this.luminosity;
    
    if (t.equals("white dwarf"))
      value += 2;
    
    return value;
  }
  
  //radius is correlated with going up to the left in the chart
  //certain star types are weird and are bigger/smaller than they should be
  private int setRadius() {
    String t  = typeMap[luminosity - 1][temp - 1];
    int value = SIZELOW + r.nextInt(SIZERND);
    
    switch (t) {
      case "white dwarf":
	value *= WDSIZE;
	break;
      case "red dwarf":
	value *= RDSIZE;
	break;
      case "regular":
	break;
      case "blue giant":
	value *= BGSIZE;
	break;
      case "red giant":
	value *= RGSIZE;
	break;
      case "red supergiant":
	value *= RSSIZE;
	break;
      default:
	System.out.println("Could not find star type: '" + t + "' in Star.");
    }
    
    return value;
  }
  
  //gives the star a random color within a certain range (blue, red, yellow, etc)
  private int setColor(String range) {
    int unit = 255 / typeMap.length;
    int lum  = 7 * unit;
    
    int red   = lum + (r.nextInt(25));
    int green = lum + (r.nextInt(25));
    int blue  = lum + (r.nextInt(25));
        
    switch(range) {
      case "blue":
	red   -= unit * 2;
	green -= unit * 2;
	blue  += unit * 2;
	break;
      case "white":
	red   += unit * 2;
	green += unit * 2;
	blue  += unit * 2;
	break;
      case "yellow":
	red   += unit * 2;
	green += unit * 2;
	blue  -= unit * 3;
	break;
      case "orange":
	red   += unit * 3;
	green += unit * 1;
	blue  -= unit * 2;
	break;
      case "red":
	red   += unit * 2;
	green -= unit * 3;
	blue  -= unit * 3;
	break;
      default:
	System.out.println("Could not find color '" + range + "' in Star::getColor.");
    }
    
    this.colorParts = new int[] {red, green, blue};
    
    return rgbToHex(red, green, blue);
  }

  private int[] setGoldBounds() {
    //"realTemp" measures temperature in terms of thousands centigrade
    //our sun is ~5000C, so it would return 5
    //default goldMin and goldMax are for our sun
    //so now we shift them accordingly
    //TODO: make this reflect actual goldilocks calculations
    double unitLum = this.getEffectiveLuminosity();
    
    int goldHot = (int) Math.ceil(BASEHOT * unitLum);
    int goldMin = (int) Math.ceil(BASEMIN * unitLum);
    int goldMax = (int) Math.ceil(BASEMAX * unitLum);
    int goldCld = (int) Math.ceil(BASECLD * unitLum);
    
    return new int[] {goldHot, goldMin, goldMax, goldCld};
  }
  
  //most star features assume that planets will be created first,
  //and use them to determine location and such
  @Override
  public ArrayList<SolarObj> generateFeatures() {
    addPlanets();
    features.add(getOortCloud());
    addAstBelts();
    addComets();
    return features;
  }
  
  //TODO: create addType function that does the similar logic
  private void addPlanets() {    
    //TODO: make this so bigger stars have a high lower bound
    int count = r.nextInt(AVGPLANETS * (this.mass)) + 2;
    
    for (int i = 0; i<count; i++) {
      Planet p = new Planet(gen, this);
      System.out.println(p.getSpec());
      if (!p.specType.equals("destroyed"))
	features.add(p);
      else if (r.nextDouble() > 0.9) //otherwise sometimes it'll run forever
	i--;
    }    
  }
  
  //TODO: call this if planets ever collide?
  private void addAstBelts() {
    int count = r.nextInt(AVGASTBELT);
    
    for (int i = 0; i<count; i++) {
      AstBelt a = new AstBelt(gen, this);
      System.out.println(a.getSpec());
      if (!a.specType.equals("destroyed"))
	features.add(a);
      else if (r.nextDouble() > 0.9)
	i--;
    } 
  }
  
  private void addComets() {
    int count = r.nextInt(AVGCOMET) + 1;
     
    for (int i = 0; i<count; i++) {
      Comet a = new Comet(gen, this);
      System.out.println(a.getSpec());
      if (!a.specType.equals("destroyed"))
	features.add(a);
      else if (r.nextDouble() > 0.9)
	i--;
    }
  }
  
  private SolarObj getOortCloud() {
    return new Oort(gen, this);
  }
  
  public int[] getGoldBounds() {
    return this.goldBounds;
  }
  
  public int getMass() {
    return this.mass;
  }
  
  public String getType() {
    return this.specType;
  }
  
  public int getRadius() {
    return this.radius;
  }
  
  public int getTemp() {
    return this.temp;
  }
  
  public int getRealTemp() {
    return this.realTemps[temp - 1];
  }
  
  public int getColor() {
    return this.color;
  }
  
  public int[] getColorParts() {
    return this.colorParts;
  }
  
  public int getLuminosity() {
    return this.luminosity;
  }
  
  public double getRealLuminosity() {
    return Math.pow(10, this.luminosity - 5);
  }
  
  public double getEffectiveLuminosity() {
    return this.effectiveLum[this.luminosity - 1];
  }
  
  @Override
  public String toString() {
    String description;
    String parentRef = (this.parent==null) ? "null" : this.getParent().reference;
    
    description = "Name: " + this.name + "\n" +
		  "Parent: " + parentRef + "\n" + //TODO: remove this
		  "A " + this.specType + " " + this.genType + ". " + this.desc + "\n" +
		  "Size: " + this.radius + "\n" +
		  "Mass: " + this.mass + "\n" +
		  "Luminosity: " + this.luminosity + "\n" + 
		  "Real Luminosity: " + this.getRealLuminosity() + "\n" + "\n";
    
    return description;
  }
}
