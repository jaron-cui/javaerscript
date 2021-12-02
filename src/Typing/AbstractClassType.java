package Typing;

import java.util.Set;

public class AbstractClassType extends ClassType {
  public AbstractClassType.Contents contents;

  public AbstractClassType(String name) {
    super(name);
    this.contents = new AbstractClassType.Contents(this);
  }

  protected void setLocalPrototypes(Set<FunctionType> localPrototypes) {
    this.localPrototypes.addAll(localPrototypes);
  }

  public static final class Contents extends Builder {
    private Contents(ClassType target) {
      super(target);
    }

    public void initialize(Set<ClassType> superclasses, Set<FunctionType> functions,
                           Set<FunctionType> prototypes) {
      this.markInitialized();
      target.setLocalFunctions(functions);
      target.setLocalPrototypes(prototypes);
      target.setSuperclasses(superclasses);
    }
  }
}
