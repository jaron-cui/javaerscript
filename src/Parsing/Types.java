package Parsing;

import java.util.ArrayList;
import java.util.List;

public class Types {
  public void parse(String source) {
    List<String> tokens = new ArrayList<>();
    int index = 0;
    while (index < source.length()) {
      while (index < source.length() && Character.isWhitespace(source.charAt(index))) {
        index += 1;
      }
      int start = index;
      while (index < source.length() && !Character.isWhitespace(source.charAt(index))) {
        index += 1;
      }
      tokens.add(source.substring(start, index));
    }
  }
}
