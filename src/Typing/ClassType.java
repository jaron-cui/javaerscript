package Typing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a java class type.
 */
public class ClassType extends Type {
  private final Set<ClassType> superclasses;
  private final Set<ClassType> subclasses;
  protected final Set<FunctionType> localFunctions;
  protected final Set<FunctionType> localPrototypes;
  protected final Set<FunctionType> superFunctions;
  protected final Set<FunctionType> superPrototypes;
  public Contents contents;

  public ClassType(String name) {
    super(name);
    this.superclasses = new HashSet<>();
    this.subclasses = new HashSet<>();
    this.localFunctions = new HashSet<>();
    this.localPrototypes = new HashSet<>();
    this.superFunctions = new HashSet<>();
    this.superPrototypes = new HashSet<>();
    this.contents = new Contents(this);
  }

  /**
   * Get the function matching the name and input types.
   *
   * @param name       the name of the function
   * @param inputTypes an array indicating the types of the function arguments
   * @return the appropriate function if found, null if not
   */
  public FunctionType getMethod(String name, Type[] inputTypes, Type outputType) {
    // make this a constant-time operation sometime
    for (FunctionType function : this.localFunctions) {
      if (function.name().equals(name) && function.matchesOutput(outputType) && function
          .matchesInput(inputTypes)) {
        return function;
      }
    }
    return null;
  }

  protected Set<FunctionType> getPrototypes() {
    Set<FunctionType> prototypes = new HashSet<>(this.localPrototypes);
    prototypes.addAll(this.superPrototypes);
    return prototypes;
  }

  protected Set<FunctionType> getFunctions() {
    Set<FunctionType> functions = new HashSet<>(this.localFunctions);
    functions.addAll(this.superFunctions);
    return functions;
  }

  @Override
  public boolean supertypeOf(Type type) {
    if (!(type instanceof ClassType)) {
      return false;
    }
    return this == type || this.subclasses.contains(type);
  }

  @Override
  public boolean subtypeOf(Type type) {
    if (!(type instanceof ClassType)) {
      return false;
    }
    return this == type || this.superclasses.contains(type);
  }

  private void inheritPrototype(FunctionType prototype) {
    List<FunctionType> localSignatures = new ArrayList<>(this.localFunctions);
    localSignatures.addAll(this.localPrototypes);
    for (FunctionType local : localSignatures) {
      // inherited prototype is overridden by local prototype or function
      if (Util.canOverride(local, prototype)) {
        return;
      }
      // this implies that the local signature is more restrictive than what it
      // should override, which is not allowed
      if (Util.signatureConflict(prototype, local)) {
        throw new IllegalArgumentException("Local prototype clashes with inherited.");
      }
    }
    // no overriding prototype or local clashing was found
    for (FunctionType inherited : new ArrayList<>(this.superPrototypes)) {
      if (Util.canOverride(inherited, prototype)) {
        return;
      }
      if (Util.canOverride(prototype, inherited)) {
        this.superPrototypes.remove(inherited);
        this.superPrototypes.add(prototype);
        return;
      }
      if (Util.signatureConflict(prototype, inherited)) {
        throw new IllegalArgumentException("Conflict between inherited prototypes.");
      }
    }
    // prototype did not override and was not overridden and no inherited prototype conflicts
    for (FunctionType inherited : this.superFunctions) {
      if (Util.canOverride(inherited, prototype)) {
        return;
      }
      if (Util.canOverride(prototype, inherited)) {
        throw new IllegalArgumentException("Inherited function clashes with inherited prototype.");
      }
      if (Util.signatureConflict(prototype, inherited)) {
        throw new IllegalArgumentException("Conflict between inherited prototype and method.");
      }
    }
    // was not implemented through inheritance, did not clash with inherited methods
    this.superPrototypes.add(prototype);
  }

  private void inheritFunction(FunctionType function) {
    for (FunctionType local : this.localFunctions) {
      if (Util.canOverride(local, function)) {
        return;
      }
      if (Util.canOverride(function, local)) {
        throw new IllegalArgumentException("Local function does not properly override inherited.");
      }
      if (Util.signatureConflict(function, local)) {
        throw new IllegalArgumentException(
            "Signature conflict between inherited and local function.");
      }
    }
    for (FunctionType local : new ArrayList<>(this.localPrototypes)) {
      if (Util.canOverride(function, local)) {
        this.localPrototypes.remove(local);
        this.superFunctions.add(function);
        return;
      }
      if (Util.canOverride(local, function)) {
        throw new IllegalArgumentException(
            "Local prototype illegally constrains inherited function signature.");
      }
      if (Util.signatureConflict(local, function)) {
        throw new IllegalArgumentException("Local prototype clashes with inherited function.");
      }
    }
    for (FunctionType inherited : this.superFunctions) {
      if (Util.canOverride(function, inherited) || Util.canOverride(inherited, function) || Util
          .signatureConflict(function, inherited)) {
        throw new IllegalArgumentException("Some inherited functions are fighting.");
      }
    }
    for (FunctionType inherited : new ArrayList<>(this.superPrototypes)) {
      if (Util.canOverride(function, inherited)) {
        this.superPrototypes.remove(inherited);
        this.superFunctions.add(function);
        return;
      }
      if (Util.canOverride(inherited, function) || Util.signatureConflict(inherited, function)) {
        throw new IllegalArgumentException(
            "Inherited function does not fully override inherited prototype.");
      }
    }
    this.superFunctions.add(function);
  }

  /**
   * Set the local functions.
   * @param localFunctions the set of function signatures
   */
  protected void setLocalFunctions(Set<FunctionType> localFunctions) {
    this.localFunctions.addAll(localFunctions);
  }

  protected void setLocalPrototypes(Set<FunctionType> localPrototypes) {
    if (!localPrototypes.isEmpty()) {
      throw new IllegalArgumentException("Instantiable classes cannot have prototypes.");
    }
  }

  protected void setSuperclasses(Set<ClassType> superclasses) {
    // handle polymorphism
    for (ClassType superclass : superclasses) {
      if (this.superclasses.contains(superclass)) {
        throw new IllegalArgumentException(
            "Classes should not inherit the same superclass multiple times.");
      }
      this.superclasses.add(superclass);
      this.superclasses.addAll(superclass.superclasses);
      superclass.subclasses.add(this);

      // inherit all non-overridden methods and prototypes from superclasses
      for (FunctionType prototype : superclass.getPrototypes()) {
        this.inheritPrototype(prototype);
      }
      for (FunctionType function : superclass.getFunctions()) {
        this.inheritFunction(function);
      }
    }
  }

  protected static abstract class Builder {
    protected final ClassType target;
    protected boolean initialized;

    protected Builder(ClassType target) {
      this.target = target;
      this.initialized = false;
    }

    protected void markInitialized() {
      if (this.initialized) {
        throw new IllegalStateException("Classes cannot be reinitialized.");
      }
      this.initialized = true;
    }
  }

  public static final class Contents extends Builder {
    private Contents(ClassType target) {
      super(target);
    }

    public void initialize(Set<ClassType> superclasses, Set<FunctionType> functions) {
      this.markInitialized();
      this.target.setLocalFunctions(functions);
      this.target.setSuperclasses(superclasses);

      if (!this.target.getPrototypes().isEmpty()) {
        throw new IllegalArgumentException(
            "Instantiable classes cannot have unimplemented prototypes.");
      }
    }
  }

  @Override
  public String toString() {
    return this.name;
  }
}
