package app;

import java.io.*;

public class IOController {
  private static final String NORMAL_TYPE = "\u001B[0m";
  private static final String BOLD_TYPE = "\u001B[1m";
  private static final String RED_COLOR = "\u001B[31m";
  private static final String GREEN_COLOR = "\u001B[32m";
  private static final String BLUE_COLOR = "\u001B[34m";  
  private static final String CYAN_COLOR = "\u001B[36m";
  private static final String MAGENTA_COLOR = "\u001B[35m";


  private static OutputStream outputStream = System.out;
  private static InputStream inputStream = System.in;
  private static BufferedReader reader;

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

  private static String removeTags(String text) {
    if (text == null)
      return "";
    return text.replace("<reset>", "")
        .replace("<b>", "")
        .replace("<red>", "")
        .replace("<green>", "");
  }

  public static void setOutputStream(String stream) throws IOException {
    if ("stdout".equals(stream)) {
      outputStream = System.out;
    } else {
      try {
        outputStream = new FileOutputStream(stream);
      } catch (IOException e) {
        throw new IOException("Falha ao abrir arquivo de saída: " + e.getMessage());
      }
    }
  }

  public void closeOutputStream() {
    if (outputStream != null && outputStream != System.out) {
      try {
        outputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void writeLine(String textLine) throws IOException {
    try {
      textLine = (outputStream.equals(System.out)) ? parseTags(textLine) : removeTags(textLine);
      outputStream.write((textLine + '\n').getBytes());
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    }
  }

  public static void write(String text) {
    System.out.print(parseTags(text));
  }

  public static boolean setInputStream(String stream) throws FileNotFoundException {
    if ("stdin".equals(stream)) {
      inputStream = System.in;
    } else {
      try {
        FileInputStream inputFile = new FileInputStream(stream);
        reader = null;
        inputStream = inputFile;
      } catch (FileNotFoundException e) {
        System.out.println(e.getMessage());
        return false;
      }
    }
    return true;
  }

  public static String readLine() {
    try {
      if (reader == null) reader = new BufferedReader(new InputStreamReader(inputStream));
      return reader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void reset() throws IOException {
    setInputStream("stdin");
    setOutputStream("stdout");
    reader = null;
  }
}