package parts; 

import java.util.ArrayList;
import systemgen.DetailsGen;

//A feature is a thing that solarObjs can have, like a weird weather pattern
//or a habitat. Features cannot have subfeatures.
public class Feature extends SolarObj{
  public static final String CLASSTYPE = "feature";
  
//  private String genType    = "feature";
  
  private String[] allTypes = {"monolith",
			       "psychic storm",
			       "great storm",
			       "great volcano",
			       "volcano",
			       "ocean"};

  //TODO: clean this up
  public Feature(DetailsGen gen, SolarObj parent, String specType) {
    super("feature", specType, gen, parent);
  }
  
  
  /*
   * Feature can have a list of SolarFeatures
   * Acts as a featureList, basically
   * When something asks for a feature, it figures out which feature to give it
   * Pros: maintains the "solarobj determines its own type" pattern
   * Cons: none?
   */
  @Override
  protected void setType() {
    //features don't set their own types
    this.genType = CLASSTYPE;
  }

  @Override
  protected void initDescs() {
    //This place is home to a [feature name]. [feature desc]
    add("feature;monolith", "Looming and black, its ancient call is heard by all intelligent species.");
    
    add("feature;psychic storm", "No matter what you do, you can't drown out the screaming.");
    
    add("feature:great storm", "Not a single place here is free of howling winds and whipping rain.");
    
    add("feature;great volcano", "I wouldn't want to be near it when it goes off.");
    
    add("feature; volcano", "Hot rock and ash spew from its gaping maw.");
    
    add("feature; ocean", "The source of all life.");
  }

  @Override
  protected void setSpecifics() {
  }

  @Override
  public ArrayList<SolarObj> generateFeatures() {
    return null;
  }

}
