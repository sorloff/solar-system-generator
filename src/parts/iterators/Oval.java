package parts.iterators; 

import java.awt.AlphaComposite;

//a single oval for anything that needs ovals
//right now, only used for comets and gaseous Oort clouds
//holds the various values for direct input to g.drawOval
public class Oval {
  private static final int COMPRULE = AlphaComposite.SRC_OVER;
  
  private final String         color;
  private final int[]          coords;
  private final int[]          shape;
  private final int[]          angles;
  private final AlphaComposite alpha;
  
  public Oval(String color, double[] coords, double shape[], double[] angles, float alpha) {
    this.coords = new int[2];
    this.shape  = new int[2];
    this.angles = new int[2];
    
    this.color  = color;
    
    this.coords[0] = round(coords[0]);
    this.coords[1] = round(coords[1]);
	    
    this.shape[0]  = round(shape[0]);
    this.shape[1]  = round(shape[1]);
    
    this.angles[0] = round(angles[0]);
    this.angles[1] = round(angles[1]);
    
    this.alpha  = AlphaComposite.getInstance(COMPRULE, alpha);
  }
  
  private int round(double x) {
    return (int) Math.rint(x);
  }
  
  
  public String getColor() {
    return color;
  }
  
  public int[] getCoords() {
    return coords;
  }
  
  public int[] getShape() {
    return shape;
  }
  
  public int[] getAngles() {
    return angles;
  }
  
  public AlphaComposite getComp() {
    return alpha;
  }
}
