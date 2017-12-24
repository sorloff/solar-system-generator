package systemgen; 

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Main {
  public static void main(String[] args) throws FileNotFoundException {
    SystemGenerator s = new SystemGenerator();
    s.createSystem();
    String systemDescription = s.printSystem();
    
    System.out.print(systemDescription);
    
    SolarPainter vanGogh = new SolarPainter(s.system);
    vanGogh.makeImage();
    
    PrintWriter p = new PrintWriter("description.txt");
    for (String line : systemDescription.split("\n"))
      p.println(line);
    p.close();
  }
}
