package systemgen; 

//generates names of objects (for now)
public class DetailsGen {
  public String[] genTypes = {"system",
			      "star",
			      "planet",
			      "asteroid belt",
			      "Oort cloud",
			      "moon",
			      "comet",
			      "feature"};
  

  
  public DetailsGen() {

  }
  
  
  public String getName(String genType, String specType) {
    return genType + ";" + specType;
  }
}
