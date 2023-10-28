import java.io.IOException;

import app.IOController;

public class App {
  public static void main(String[] args) throws IOException {
    IOController.setInputStream("input");
    String str = IOController.readLine();
    IOController.writeLine(str);
  }
}