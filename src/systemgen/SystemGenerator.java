package systemgen; 

import parts.SolarSystem;
import parts.SolarObj;
import java.util.ArrayList;
import java.util.HashMap;

//iterates through each solar object and its features and generates the system
//also contains useful functions for dealing with a fully generated system
public class SystemGenerator {
  //holds the objs in easy <name, object> format for future linking
  HashMap<String, SolarObj> objRef;
  
  DetailsGen gen;
  
  SolarObj system;
  
  public SystemGenerator() {
    this.objRef = new HashMap<>();
    this.gen    = new DetailsGen();
  }
  
  public void createSystem() {
    this.system = new SolarSystem(this.gen);
    generateParts(system);
  }
  
  public void generateParts(SolarObj s) {
    ArrayList<SolarObj> features = s.generateFeatures();
    if (features!=null)
      features.forEach(f -> generateParts(f));
  }
  
  public String printSystem() {
    StringBuilder s = new StringBuilder();
    
    printHelper(system, s);
    
    return s.toString();
  }
  
  public void printHelper(SolarObj s, StringBuilder builder) {
    builder.append(s.toString());
    
    ArrayList<SolarObj> features = s.getFeatures();
    if (features!=null)
      for (SolarObj f : features)
	printHelper(f, builder);
    
    builder.append("\n");
    builder.append("\n");
  }
  
  
}
