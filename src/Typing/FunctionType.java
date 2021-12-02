package Typing;

import java.util.Arrays;

public class FunctionType extends Type {
  private final Type[] inputs;
  private final Type output;
  public FunctionType(String name, Type[] inputs, Type output) {
    super(name);
    this.inputs = inputs;
    this.output = output;
  }

  public Type[] inputs() {
    return Arrays.copyOf(this.inputs, this.inputs.length);
  }

  public Type output() {
    return this.output;
  }

  boolean matchesInput(Type[] types) {
    if (this.inputs.length != types.length) {
      return false;
    }
    for (int i = 0; i < types.length; i += 1) {
      if (!types[i].subtypeOf(this.inputs[i])) {
        return false;
      }
    }
    return true;
  }

  boolean matchesOutput(Type type) {
    return type.supertypeOf(this.output);
  }

  @Override
  public boolean supertypeOf(Type type) {
    if (!(type instanceof FunctionType)) {
      return false;
    }
    FunctionType functionType = (FunctionType) type;
    return this.matchesOutput(functionType.output) && this.matchesInput(functionType.inputs);
  }

  @Override
  public boolean subtypeOf(Type type) {
    if (!(type instanceof FunctionType)) {
      return false;
    }
    FunctionType functionType = (FunctionType) type;
    return functionType.matchesOutput(this.output) && functionType.matchesInput(this.inputs);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("[");
    for (Type input : this.inputs) {
      builder.append(input);
      builder.append(" ");
    }
    builder.append("-> ");
    builder.append(this.output);
    builder.append("]");

    return builder.toString();
  }
}