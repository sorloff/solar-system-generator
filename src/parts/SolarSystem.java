package parts; 

import java.util.ArrayList;
import systemgen.DetailsGen;

//this is the only SolarObj that does not have a parent
public class SolarSystem extends SolarObj{  
  public static final String CLASSTYPE = "system";
  
  String [] allTypes = {"regular",
			"binary",
			"trinary"};
  
  public SolarSystem(DetailsGen gen) {
    super(gen, null);
  }
  
  @Override
  protected void initDescs() {
    //This is a [system type] solar system. [systDesc]
    
    add("regular", "It's just your average system. One star, some planets. Nothing fancy.");
    
    add("binary", "Neat.");
    
    add("trinary", "Uh oh.");
  }
  
  @Override
  protected void setSpecifics() {
    this.reference = this.genType + ";" + this.specType;
  }
  
  @Override
  public ArrayList<SolarObj> generateFeatures() {
    features.add(new Star(this.gen, this));
    return features;
  }

  @Override
  protected void setType() {
    this.specType  = "regular";
    this.genType   = CLASSTYPE;
    //TODO: binary/trinary systems
  }
}
