package app;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.Utils;

public class Shell {

  private static Map<String, Command<?>> commandMap = new HashMap<>();
  private boolean isRunning = false;

  private void writeCommandLine() {
    var username = CommandManager.getUsernameCommand();
    username.addOption("-q");
    username.execute();

    var hostname = CommandManager.getHostnameCommand();
    hostname.addOption("-q");
    hostname.execute();

    var pwd = CommandManager.getPwdCommand();
    pwd.addOption("-q");
    pwd.execute();

    IOController.write("<b><green>" + username.getResults() + "<reset>");
    IOController.write("<blue>@<reset>");
    IOController.write("<b><green>" + hostname.getResults() + "<reset> ");
    IOController.write("<b><blue>" + Utils.contractTilde((String) pwd.getResults()) + "<reset>");
    IOController.write(" <b>$<reset> ");

    username.clear();
    hostname.clear();
    pwd.clear();
  }

  private void executeCommandFromText(String text) throws IOException {
    Pattern pattern = Pattern.compile("(\\S+)(?:\\s+(.*))?");
    Matcher matcher = pattern.matcher(text);
    String cmd = "", params = "";
    boolean redirectionInput = false;

    if (matcher.find()) {
      cmd = matcher.group(1);
      params = matcher.group(2);
    }

    Command<?> command = null;

    if (params != null && params.contains(">")) {
      int index = params.indexOf(">");
      String[] values = params.substring(index + 1).trim().split("\\s+");
      if (values.length > 0 && values[0].trim() != "") {
        IOController.setOutputStream(values[0]); 
        params = params.replace(values[0], "");        
        params = params.replace(">", "");
      }
      else
        System.out.println(
            IOController.parseTags("<red><b>Erro:<reset><red> É necessário informar o arquivo de saída.<reset>\n"));
    } else
      IOController.setOutputStream("stdout");

    if (params != null && params.contains("<")) {
      int index = params.indexOf("<");
      String[] values = params.substring(index + 1).trim().split("\\s+");
      if (values.length > 0 && values[0].trim() != "") {
        params = params.replace(values[0], "");        
        params = params.replace("<", "");
        redirectionInput = IOController.setInputStream(values[0]);
      }
      else
        System.out.println(
            IOController.parseTags("<red><b>Erro:<reset><red> É necessário informar o arquivo de entrada.<reset>\n"));
    } else
      IOController.setInputStream("stdin");

    if (cmd.equals("exit"))
      this.isRunning = false;
    else if ((command = CommandManager.getCommandByName(cmd.trim())) != null) {
      if (params != null) command.setParams(params.trim() + '\n');
      command.setRedirectedInput(redirectionInput);
      command.execute();
      command.clear();
    } else {
      IOController.writeLine("<red>Comando Inexistente: <b>" + cmd + "<reset>\n");
    }

    IOController.reset();
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