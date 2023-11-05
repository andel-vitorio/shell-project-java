package app;

import java.io.*;
import java.net.*;
import java.util.*;

import utils.Utils;

public final class CommandManager {
  private static Command<String> usernameCommand;
  private static Command<String> hostnameCommand;
  private static Command<String> pwdCommand;
  private static Command<ArrayList<File>> lsCommand;

  private static Map<String, String> parseArguments(String input) {
    Map<String, String> arguments = new HashMap<>();
    String[] parts = input.split("\\s+");

    String option = null;
    boolean isQuotedValue = false;
    StringBuilder quotedValue = new StringBuilder();

    for (String part : parts) {
      if (part.startsWith("--") && !isQuotedValue) {
        option = part.substring(2);
      } else if (option != null) {
        if (isQuotedValue) {
          quotedValue.append(" ").append(part);

          if (part.endsWith("\"")) {
            isQuotedValue = false;
            arguments.put(option, quotedValue.toString().replace("\"", ""));
            option = null;
            quotedValue.setLength(0);
          }
        } else {
          if (part.startsWith("\"")) {
            isQuotedValue = true;
            quotedValue.append(part);

            if (part.endsWith("\"")) {
              isQuotedValue = false;
              arguments.put(option, quotedValue.toString().replace("\"", ""));
              option = null;
              quotedValue.setLength(0);
            }
          } else {
            arguments.put(option, part);
            option = null;
          }
        }
      }
    }

    return arguments;
  }

  public static void setup() {

    /* ------------ Username Setup -------------- */
    usernameCommand = new Command<String>("username");
    usernameCommand.setDocumentation("<b>Username<reset>\n\nObtém o usuário atual do sistema.");
    usernameCommand.setAction(cmd -> {
      try {
        String username = System.getProperty("user.name");
        if (!cmd.getOptions().contains("-q"))
          IOController.writeLine(username + '\n');
        cmd.addResult(username);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    /* ------------ Hostname Setup -------------- */
    hostnameCommand = new Command<String>("hostname");
    hostnameCommand.setDocumentation("");
    hostnameCommand.setAction(cmd -> {
      try {
        InetAddress localHost = InetAddress.getLocalHost();
        String hostName = localHost.getHostName();
        if (!cmd.getOptions().contains("-q"))
          IOController.writeLine(hostName + '\n');
        cmd.addResult(hostName);
      } catch (UnknownHostException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    /* ------------ PWD Setup -------------- */
    pwdCommand = new Command<String>("pwd");
    pwdCommand.setDocumentation("");
    pwdCommand.setAction(cmd -> {
      File currentDir = new File(System.getProperty("user.dir"));
      try {
        String currentDirPath = currentDir.getCanonicalPath();
        if (!cmd.getOptions().contains("-q"))
          IOController.writeLine(currentDirPath + '\n');
        cmd.addResult(currentDirPath);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    /* ------------ LS Setup -------------- */
    lsCommand = new Command<ArrayList<File>>("ls");
    lsCommand.setDocumentation("");
    lsCommand.setAction(cmd -> {
      String path = "./";
      String mode = "";

      if (cmd.hasArguments()) {
        var args = CommandManager.parseArguments(cmd.getArguments());

        path = args.get("path");
        mode = args.get("mode");

        if (path == null)
          path = "./";
        else
          path = Utils.expandTilde(path);

        if (mode == null) mode = "";
      }

      File dir = new File(path);
      ArrayList<File> files = new ArrayList<>(Arrays.asList(dir.listFiles()));

      boolean list = mode.contains("l");
      boolean all = mode.contains("a");

      List<File> dirs = new ArrayList<File>();

      for (File file : files) {
        if (!all && file.getName().startsWith("."))
          continue;

        dirs.add(file);
      }

      dirs.sort((a, b) -> a.getName().compareTo(b.getName()));

      String symbol = "/";

      if (!mode.contains("x")) {
        for (File dirFile : dirs) {
          if (dirFile == null)
            continue;

          if (dirFile.isDirectory()) {
            IOController.write("<magenta>");
            symbol = "/";
          } else symbol = "";

          if (list) {
            try {
              IOController.writeLine(dirFile.getName() + symbol);
            } catch (IOException e) {
              e.printStackTrace();
            }
          } else {
            IOController.write(dirFile.getName() + symbol + "\t");
          }
          IOController.write("<reset>");
        }


        if (!list) {
          try {
            IOController.writeLine("");
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

        cmd.addResult(files);
      }

    });
  }

  public static Command<String> getUsernameCommand() {
    return usernameCommand;
  }

  public static Command<String> getHostnameCommand() {
    return hostnameCommand;
  }

  public static Command<String> getPwdCommand() {
    return pwdCommand;
  }

  public static Command<ArrayList<File>> getLsCommand() {
    return lsCommand;
  }
}
