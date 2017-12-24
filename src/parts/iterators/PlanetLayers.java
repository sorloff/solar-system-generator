package parts.iterators; 

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

//layers for planets (right now, only gas giants)
public class PlanetLayers implements Iterator<Oval>{
  private final Queue<Oval> layers;
  
  public PlanetLayers() {
    layers = new LinkedList<>();
    initLayers();
  }
  
  private void initLayers() {
    
  }
  
  @Override
  public boolean hasNext() {
    return !layers.isEmpty();
  }

  @Override
  public Oval next() {
    return layers.remove();
  }

}
