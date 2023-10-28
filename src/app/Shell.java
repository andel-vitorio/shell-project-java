package app;

import java.io.IOException;
import java.util.*;

public class Shell {

  private boolean isRunning = false;

  private void writeCommandLine() {
    Command username = CommandManager.getUsernameCommand();
    username.addOption("--no-print");
    username.execute();

    IOController.write("<b><green>" + username.getResults().get(0) + "<reset>");
    IOController.write(" <b>$<reset> ");

    username.clear();
  }

  private void executeCommandFromText(String text) throws IOException {
    String[] items = text.split(" "); 
    Command command = null;

    if (text.contains(">")) {
      int index = text.indexOf(">");
      String[] values = text.substring(index + 1).trim().split("\\s+");
      if (values.length > 0 && values[0].trim() != "") IOController.setOutputStream(values[0]);
      else System.out.println(IOController.parseTags("<red><b>Erro:<reset><red> É necessário informar o arquivo de saída.<reset>\n"));
    } else IOController.setOutputStream("stdout");

    if (text.contains("<")) {
      int index = text.indexOf("<");
      String[] values = text.substring(index + 1).trim().split("\\s+");
      if (values.length > 0  && values[0].trim() != "") IOController.setInputStream(values[0]); 
      else System.out.println(IOController.parseTags("<red><b>Erro:<reset><red> É necessário informar o arquivo de entrada.<reset>\n"));
    } else IOController.setInputStream("stdin");

    if (items[0].equals("exit")) this.isRunning = false;
    else if (items[0].equals("username")) {
      command = CommandManager.getUsernameCommand();
      command.execute();
    }

    if (command != null) command.clear();
  }

  public void run() throws IOException {
    IOController.writeLine("<b>Shell Project v1.0<reset>");
    IOController.writeLine("Digite <b>'help'<reset> para obter ajuda ou <b>'exit'<reset> para sair.\n");

    CommandManager.setup();
    
    isRunning = true;

    while (isRunning) {
      this.writeCommandLine();
      String commandLine = IOController.readLine();
      executeCommandFromText(commandLine);
    }
  }
}