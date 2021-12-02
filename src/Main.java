import Typing.AbstractClassType;
import Typing.FunctionType;
import Typing.ClassType;
import Typing.InterfaceType;
import Typing.Type;

import java.util.HashSet;
import java.util.Set;

public class Main {
  public static void main(String[] args) {
    InterfaceType Number = new InterfaceType("Number");
    FunctionType getValue = new FunctionType("getValue", new Type[]{}, Number);
    Number.contents.initialize(Set.of(), Set.of(getValue));
    AbstractClassType Integer = new AbstractClassType("Integer");
    Integer.contents.initialize(Set.of(Number), Set.of(), Set.of());

    System.out.println("Integer method: " + Integer.getMethod("getValue", new Type[]{}, Number));
  }
}
