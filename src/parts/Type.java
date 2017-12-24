package parts; 

import java.util.ArrayList;
import java.util.function.BooleanSupplier;

//some solar objects (planets, moons) might have a very large number of types
//this holds type names and rules for easy type determination
public class Type {  
  String name;
  private ArrayList<BooleanSupplier> rules;
  
  public Type(String name) {
    this.rules = new ArrayList<>(); 
    this.name  = name;
  }
  
  public boolean match() {
    return rules.stream().allMatch(s -> s.getAsBoolean());
  }
  
  public void add(BooleanSupplier b) {
    rules.add(b);
  }
  
  public void addAll(ArrayList<BooleanSupplier> a) {
    rules.addAll(a);
  }
}
