package Typing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Util {
  public static boolean canOverride(FunctionType override, FunctionType original) {
    return original.name().equals(override.name()) && override.supertypeOf(original);
  }

  public static Collection<FunctionType> heritableFunctions(Collection<FunctionType> implemented,
                                                            Collection<FunctionType> heritable) {
    Collection<FunctionType> heritableFunctions = new HashSet<>();
    for (FunctionType candidate : heritable) {
      boolean inherited = true;
      for (FunctionType function : implemented) {
        if (Util.canOverride(function, candidate)) {
          inherited = false;
          break;
        }
      }
      if (inherited) {
        implemented.add(candidate);
        heritableFunctions.add(candidate);
      }
    }
    return heritableFunctions;
  }

  public static boolean containsConflictingSignatures(Collection<FunctionType> signatures) {
    List<FunctionType> checkedFunctions = new ArrayList<>();
    for (FunctionType function : signatures) {
      for (FunctionType checked : checkedFunctions) {
        if (function.name().equals(checked.name()) && (function.supertypeOf(checked) || function
            .subtypeOf(checked))) {
          return true;
        }
      }
      checkedFunctions.add(function);
    }
    return false;
  }

  public static boolean signatureConflict(FunctionType function, FunctionType existing) {
    if (function.name().equals(existing.name())) {
      return false;
    }
    return (existing.matchesInput(function.inputs()) && !existing.matchesOutput(function.output()))
        || (function.matchesInput(existing.inputs()) && !function.matchesOutput(existing.output()));
  }
}
