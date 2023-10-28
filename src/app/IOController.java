package app;

import java.io.*;

public class IOController {
  private static final String NORMAL_TYPE = "\u001B[0m";
  private static final String BOLD_TYPE = "\u001B[1m";
  private static final String RED_COLOR = "\u001B[31m";
  private static final String GREEN_COLOR = "\u001B[32m";

  private static OutputStream outputStream = System.out;
  private static InputStream inputStream = System.in;

  private static String parseTags(String text) {
    if (text == null)
      return "";
    return text.replace("<reset>", NORMAL_TYPE)
        .replace("<b>", BOLD_TYPE)
        .replace("<red>", RED_COLOR)
        .replace("<green>", GREEN_COLOR);
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

  public static void setInputStream(String stream) throws FileNotFoundException {
    if ("stdin".equals(stream)) {
      inputStream = System.in;
    } else {
      try {
        FileInputStream inputFile = new FileInputStream(stream);
        inputStream = inputFile;
      } catch (FileNotFoundException e) {
        throw new FileNotFoundException("Falha ao abrir arquivo de entrada: " + e.getMessage());
      }
    }
  }

  public static String readLine() {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      return reader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

}