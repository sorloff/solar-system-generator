package parts; 

import java.util.ArrayList;
import java.util.function.BooleanSupplier;

//this is similar to the Type class, but for features instead
public class SolarFeature {  
  String name;
  String desc;
  ArrayList<BooleanSupplier> rules;
  
  public SolarFeature(String name, String desc) {
    this.name = name;
    this.desc = desc;
  }
  
  public boolean match() {
    return rules.stream().allMatch(s -> s.getAsBoolean());
  }
}
