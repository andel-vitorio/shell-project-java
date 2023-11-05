package utils;

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
}
