package Typing;

import java.util.HashSet;
import java.util.Set;

public abstract class Type implements IPart {
  protected final String name;

  public Type(String name) {
    this.name = name;
  }

  // get the name of the type
  public String name() {
    return this.name;
  }

  // is the given type a supertype?
  public abstract boolean supertypeOf(Type type);

  // is the given type a subtype?
  public abstract boolean subtypeOf(Type type);
}