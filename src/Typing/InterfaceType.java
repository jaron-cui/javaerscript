package Typing;

import java.util.HashSet;
import java.util.Set;

public class InterfaceType extends AbstractClassType {
  public InterfaceType.Contents contents;

  public InterfaceType(String name) {
    super(name);
    this.contents = new InterfaceType.Contents(this);
  }

  public static final class Contents extends Builder {
    private Contents(ClassType target) {
      super(target);
    }

    public void initialize(Set<InterfaceType> superinterfaces, Set<FunctionType> prototypes) {
      this.markInitialized();
      target.setLocalPrototypes(prototypes);
      target.setSuperclasses(new HashSet<>(superinterfaces));
    }
  }
}
