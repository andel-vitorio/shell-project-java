package utils;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;

public class Utils {

  private static final String NORMAL_TYPE = "\u001B[0m";
  private static final String BOLD_TYPE = "\u001B[1m";
  private static final String RED_COLOR = "\u001B[31m";
  private static final String GREEN_COLOR = "\u001B[32m";
  private static final String BLUE_COLOR = "\u001B[34m";
  private static final String CYAN_COLOR = "\u001B[36m";
  private static final String MAGENTA_COLOR = "\u001B[35m";

  public static String parseTags(String text) {
    if (text == null)
      return "";
    return text.replace("<reset>", NORMAL_TYPE)
        .replace("<b>", BOLD_TYPE)
        .replace("<red>", RED_COLOR)
        .replace("<green>", GREEN_COLOR)
        .replace("<blue>", BLUE_COLOR)
        .replace("<cyan>", CYAN_COLOR)
        .replace("<magenta>", MAGENTA_COLOR);
  }

  public static String removeTags(String text) {
    if (text == null)
      return "";
    text = Utils.removeAnsiEscapes(text);
    return text.replace("<reset>", "")
        .replace("<b>", "")
        .replace("<red>", "")
        .replace("<green>", "");
  }

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

    for (String line : input.split("\n")) {
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

  public static String resolvePath(String inputPath) {
    String[] pathComponents = inputPath.split(File.separator);

    File currentDirectory = null;

    if (inputPath.startsWith("/")) {
      currentDirectory = new File("/");
    } else {
      currentDirectory = new File(System.getProperty("user.dir"));
    }

    for (int i = 0; i < pathComponents.length; i++) {
      String component = pathComponents[i];
      if (component.equals(".")) {
      } else if (component.equals("..")) {
        currentDirectory = currentDirectory.getParentFile();
      } else if (component.startsWith("~")) {
        String homeDir = System.getProperty("user.home");
        pathComponents[i] = component.replace("~", homeDir);
        currentDirectory = new File(homeDir);
      } else {
        currentDirectory = new File(currentDirectory, component);
      }
    }

    return currentDirectory.getAbsolutePath();
  }

  public static Map<String, String> parseArguments(String input) {
    Map<String, String> arguments = new HashMap<>();
    String[] parts = input.split("\\s+");

    String option = null;
    boolean isQuotedValue = false;
    StringBuilder quotedValue = new StringBuilder();

    for (String part : parts) {
      if (part.startsWith("--") && !isQuotedValue) {
        option = part.substring(2);
      } else if (option != null) {
        if (isQuotedValue) {
          quotedValue.append(" ").append(part);

          if (part.endsWith("\"")) {
            isQuotedValue = false;
            arguments.put(option, quotedValue.toString().replace("\"", ""));
            option = null;
            quotedValue.setLength(0);
          }
        } else {
          if (part.startsWith("\"")) {
            isQuotedValue = true;
            quotedValue.append(part);

            if (part.endsWith("\"")) {
              isQuotedValue = false;
              arguments.put(option, quotedValue.toString().replace("\"", ""));
              option = null;
              quotedValue.setLength(0);
            }
          } else {
            arguments.put(option, part);
            option = null;
          }
        }
      }
    }

    return arguments;
  }

  public static void zipDirectory(File dir, String baseName, ZipOutputStream zos) throws IOException {
    File[] files = dir.listFiles();

    if (files != null) {
      for (File file : files) {
        if (file.isDirectory()) {
          zipDirectory(file, baseName + file.getName() + File.separator, zos);
        } else {
          zipFile(file, zos, baseName);
        }
      }
    }
  }

  public static void zipFile(File file, ZipOutputStream zos, String baseName) throws IOException {
    FileInputStream fis = new FileInputStream(file);
    ZipEntry zipEntry = new ZipEntry(baseName + file.getName());
    zos.putNextEntry(zipEntry);

    byte[] buffer = new byte[1024];
    int length;

    while ((length = fis.read(buffer)) > 0) {
      zos.write(buffer, 0, length);
    }

    fis.close();
  }

  public static String removeAnsiEscapes(String input) {
    String ansiEscapePattern = "\\x1B\\[[0-?]*[ -/]*[@-~]";
    Pattern pattern = Pattern.compile(ansiEscapePattern);
    Matcher matcher = pattern.matcher(input);
    return matcher.replaceAll("");
  }

}
