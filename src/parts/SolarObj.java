package parts; 

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import systemgen.DetailsGen;


//Holds data for a solar object
//SolarObj only hold logic for creating new objects and a way to represent solar structure
//Names/descs are all in DetailsGen and are determined via the reference variable.
public abstract class SolarObj {
  protected String name;
  protected String genType;
  protected String specType;
  protected String desc;
  public String reference;
  
  protected ArrayList<String> allDescs = new ArrayList<>();
  protected String[]          allTypes;
    
  protected DetailsGen gen;
  protected SolarObj   parent;
  
  protected ArrayList<SolarObj> features = new ArrayList<>();
  protected Random r                     = new Random();
  
  
  protected SolarObj() {
    System.out.println("Stop. Why are you calling this?");
  }
  
  //for subclasses like planet that generate type based on specifics
  public SolarObj(DetailsGen gen, SolarObj parent) {
    this.parent  = parent; 

    initDescs();

    setSpecifics();
    setType();

    //TODO: add back in generateFeatures here
//    generateFeatures();
        
    this.name      = gen.getName(this.genType, this.specType);
    this.desc      = setDesc();
    this.gen       = gen;
    this.reference = genType + ";" + specType; 
  }
  
  //for subclasses like feature that let the parent set their type for them
  protected SolarObj(String genType, String specType, DetailsGen gen, SolarObj parent) {
    this.genType   = genType;
    this.specType  = specType;
    this.name      = gen.getName(genType, specType);
    this.desc      = setDesc();
    this.gen       = gen;
    this.parent    = parent;
    this.reference = genType + ";" + specType;   
    
    setSpecifics();
  }
  
  //for supertype creation
  protected SolarObj(DetailsGen gen, SolarObj parent, String specType) {
    this.genType   = "generic solar object";
    this.specType  = "specific solar object";
    this.reference = this.genType + ";" + this.specType;
    this.name      = "Mr. Abstract Object";
    this.desc      = "An abstract object. You... shouldn't be seeing this.";
    this.gen       = gen;
    this.parent    = null;
  }
  
  //determines type based on parent and specifics
  protected abstract void setType();
  
  //randomly generates/sets specifics of SolarObj that aren't features (like size)
  protected abstract void setSpecifics();
  
  //adds all descriptions to this.descs
  //useful for user-added descriptions
  //TODO: set this to load from file
  protected abstract void initDescs();
  
  public abstract ArrayList<SolarObj> generateFeatures();
  
  protected String setDesc() {
    
    String[] descs = allDescs.stream()
			     .filter(s -> s.startsWith(specType))
			     .map(s -> s.split(": ")[1])
			     .toArray(String[]::new);
    
    if (descs.length==0)
      return "This " + this.genType + " has no description! Woops!";
    int choice = r.nextInt(descs.length);
    return descs[choice];
  }
  
  public SolarObj getParentType(String type) {
    if (this.genType.equals(type))
      return this;
    else if (this.getParent() != null)
      return this.getParent().getParentType(type);
    else
      return null;
  }
  
  @Override
  public String toString() {
    String description;
    String parentRef = (this.parent==null) ? "null" : this.getParent().reference;
    
    description = "Name: " + this.name + "\n" +
		  "Parent: " + parentRef + "\n" + //TODO: remove this
		  "A " + this.specType + " " + this.genType + ". " + this.desc + "\n";
    
    return description;
  }
  
  public void add(String type, String desc) {
    allDescs.add(type + ": " + desc);
  }
  
  public SolarObj getParent() {
    return this.parent;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getDesc() {
    return this.desc;
  }
  
  public String getGen() {
    return this.genType;
  }
  
  public String getSpec() {
    return this.specType;
  }
  
  public ArrayList<SolarObj> getFeatures() {
    return this.features;
  }
  
  //chooses a single value from the given list
  protected String chooseFromList(String[] types) {
    Random r   = new Random();
    int choice = r.nextInt(types.length);
    return types[choice];
  }
  
  //returns a random subset of the given list
  protected ArrayList<String> chooseUniqueFromList (String[] types, double chance) {
    ArrayList<String> choices = new ArrayList<>();
    Random r                  = new Random();
    
    for (String s : types)
      if (r.nextDouble() <= chance)
	choices.add(s);
    
    return choices;
  }
  
  //given three rgb values in [0, 255], returns the hex code
  //caps between [0, 255] because I can't trust myself with anything
  protected int rgbToHex(int red, int green, int blue) {    
    red = Math.min(red, 255);
    red = Math.max(red, 0);
    
    green = Math.min(green, 255);
    green = Math.max(green, 0);
    
    blue = Math.min(blue, 255);
    blue = Math.max(blue, 0);
    
    String rHex = Integer.toString(red, 16);
    String gHex = Integer.toString(green, 16);
    String bHex = Integer.toString(blue, 16);
    
    //hex codes need 0-padding to work
    if (rHex.length() < 2)
      rHex = "0" + rHex;
    if (gHex.length() < 2)
      gHex = "0" + gHex;
    if (bHex.length() < 2)
      bHex = "0" + bHex;
    
    return Integer.parseInt(rHex + gHex + bHex, 16);
  }
}
