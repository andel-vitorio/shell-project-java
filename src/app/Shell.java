package app;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.Utils;

public class Shell {

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

    if (matcher.find()) {
      cmd = matcher.group(1);
      params = matcher.group(2);
    }

    Command<?> command = null;

    if (text.contains(">")) {
      int index = text.indexOf(">");
      String[] values = text.substring(index + 1).trim().split("\\s+");
      if (values.length > 0 && values[0].trim() != "")
        IOController.setOutputStream(values[0]);
      else
        System.out.println(
            IOController.parseTags("<red><b>Erro:<reset><red> É necessário informar o arquivo de saída.<reset>\n"));
    } else
      IOController.setOutputStream("stdout");

    if (text.contains("<")) {
      int index = text.indexOf("<");
      String[] values = text.substring(index + 1).trim().split("\\s+");
      if (values.length > 0 && values[0].trim() != "")
        IOController.setInputStream(values[0]);
      else
        System.out.println(
            IOController.parseTags("<red><b>Erro:<reset><red> É necessário informar o arquivo de entrada.<reset>\n"));
    } else
      IOController.setInputStream("stdin");

    if (cmd.equals("exit"))
      this.isRunning = false;
    else if (cmd.equals("username")) {
      command = CommandManager.getUsernameCommand();
      command.execute();
    } else if (cmd.equals("hostname")) {
      command = CommandManager.getHostnameCommand();
      command.execute();
    } else if (cmd.equals("pwd")) {
      command = CommandManager.getPwdCommand();
      command.execute();
    } else if (cmd.equals("ls")) {
      String mode = "", path = "";

      if (params != null) {
        Pattern modePattern = Pattern.compile("-(la|al|l|a)\\s*");
        Matcher modeMatcher = modePattern.matcher(params);

        while (modeMatcher.find())
          mode += modeMatcher.group(1);

        String paramsWithoutMode = params.replaceAll("-(la|al|l|a)\\s*", "");

        String[] parts = paramsWithoutMode.split("\\s+");

        if (parts.length > 0) {
          path = parts[0];

          if (path.startsWith("/")) {
            path = "." + path;
          } else if (!path.startsWith("~")) {
            path = "./" + path;
          }
        }
      }

      command = CommandManager.getLsCommand();

      if (mode != "")
        command.addArguments("mode", mode);
      if (path != "")
        command.addArguments("path", path);

      command.execute();
    }

    if (command != null)
      command.clear();
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