package utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
  public static String expandTilde(String path) {
    if (path.startsWith("~")) {
      String homeDir = System.getProperty("user.home");
      path = path.replaceFirst("~", homeDir);
    }
    return path;
  }

  public static String contractTilde(String path) {
    String homeDir = System.getProperty("user.home");
    if (path.startsWith(homeDir)) {
      path = path.replaceFirst(homeDir, "~");
    }
    return path;
  }

  public static ArrayList<String> extractQuotedStrings(String input) {
    ArrayList<String> result = new ArrayList<>();

    for (String line: input.split("\n")) {
      Pattern pattern = Pattern.compile("'(.*?)'|\"(.*?)\"|([^\"'\\s]+)");
      Matcher matcher = pattern.matcher(line);
  
      while (matcher.find()) {
        String singleQuotes = matcher.group(1);
        String doubleQuotes = matcher.group(2);
        String unquoted = matcher.group(3);
  
        if (singleQuotes != null) {
          result.add(singleQuotes);
        }
  
        if (doubleQuotes != null) {
          result.add(doubleQuotes);
        }
  
        if (unquoted != null) {
          result.add(unquoted);
        }
      }
    }


    return result;
  }

  public static String removeQuotes(String input) {
    StringBuilder result = new StringBuilder();
    boolean inSingleQuotes = false;
    boolean inDoubleQuotes = false;

    for (char c : input.toCharArray()) {
      if (c == '\'' && !inDoubleQuotes) {
        inSingleQuotes = !inSingleQuotes;

      } else if (c == '"' && !inSingleQuotes) {
        inDoubleQuotes = !inDoubleQuotes;
      } else {
        result.append(c);
      }
    }

    return result.toString();
  }

}
