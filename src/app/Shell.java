package app;

import java.io.IOException;
import java.util.*;

public class Shell {

  private boolean isRunning;

  private void writeCommandLine() {
    Command username = CommandManager.getUsernameCommand();
    username.addOption("--no-print");
    username.execute();

    IOController.write("<b><green>" + username.getResults().get(0) + "<reset>");
    IOController.write(" <b>$<reset> ");

    username.clear();
  }

  public Shell() {
    isRunning = false;
  }


  public void run() throws IOException {
    //IOController.setInputStream("input");
    IOController.writeLine("<b>Shell Project v1.0<reset>");
    IOController.writeLine("Digite <b>'help'<reset> para obter ajuda ou <b>'exit'<reset> para sair.\n");

    CommandManager.setup();
    
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