package systemgen;

import parts.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import parts.iterators.Oval;
import static systemgen.SolarPainter.DISTSCALE;



public class CometTest {
  private int height = SolarPainter.DEFAULTHEIGHT;
  private int width  = height * 4;
  
  private Graphics2D g;
  
  
  public static void main(String[] args) {
    CometTest foo = new CometTest();
    foo.makeImage();
  }
  
  public void makeImage() {
    BufferedImage image;
    
    try {
      image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    } catch (OutOfMemoryError e) {
      System.out.println("SolarPainter::makeImage had to use BigBuffered Image.");
      System.out.println("Image size was: (" + width + ", " + height + ")");
      return;
//      image = BigBufferedImage.create(width, height, BigBufferedImage.TYPE_INT_ARGB);
    }
    
    image.setAccelerationPriority(1);
    this.g = image.createGraphics();

    g.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
    
    g.setColor(Color.black);
//    g.fillRect(0, 0, width, height);
    
    drawComet();
    
    try {
      String path = Paths.get(".").toAbsolutePath().normalize().toString();
      ImageIO.write(image, "png", new File(path + "/gas_test.png"));
    } catch (IOException e) {
      System.out.println("Program could not write to directory.");
      System.out.println("IOException: " + e);
    }
    
    
  }
  
  public void drawComet() {
    Composite originalComposite = g.getComposite();
    AffineTransform originTrans = g.getTransform();
    
    
    AlphaComposite testComp = AlphaComposite.getInstance(AlphaComposite.SRC_IN);
    
    int x = width/2;
    int y = height/2;
    
    
    g.setColor(Color.lightGray);
    g.fillOval(x, y, 1000, 1000);
    
    g.setComposite(testComp);

    g.setColor(Color.darkGray);
    g.fillRect(x+500, y+500, 2000, 300);
  }
}
