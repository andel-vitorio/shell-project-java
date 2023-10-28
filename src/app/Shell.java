package app;

import java.io.IOException;
import java.util.*;

public class Shell {

  private boolean isRunning;

  private void writeCommandLine() {
    IOController.write("$ ");
  }

  public Shell() {
    isRunning = false;
  }


  public void run() throws IOException {
    //IOController.setInputStream("input");
    IOController.writeLine("<b>Shell Project v1.0</b>");
    IOController.writeLine("Digite <b>'help'</b> para obter ajuda ou <b>'exit'</b> para sair.\n");
    
    Scanner scan = new Scanner(System.in);

    isRunning = true;

    while (isRunning) {
      this.writeCommandLine();
      String str = "";
      if (scan.hasNext()) {
        str = scan.nextLine();
      }
      if (str != null && str.equals("exit"))
        isRunning = false;
      IOController.writeLine(str);
    }
  }
}