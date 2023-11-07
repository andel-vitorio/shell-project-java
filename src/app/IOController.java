package app;

import java.io.*;

import utils.Utils;

public class IOController {

  private static OutputStream outputStream = System.out;
  private static InputStream inputStream = System.in;
  private static BufferedReader reader;

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
      textLine = (outputStream.equals(System.out)) ? Utils.parseTags(textLine) : Utils.removeTags(textLine);
      outputStream.write((textLine + '\n').getBytes());
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    }
  }

  public static void write(String text) {
    System.out.print(Utils.parseTags(text));
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
      if (reader == null)
        reader = new BufferedReader(new InputStreamReader(inputStream));
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