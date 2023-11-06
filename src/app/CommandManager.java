package app;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.Utils;

public final class CommandManager {

  private static Map<String, Command<?>> commandMap = new HashMap<>();

  private static Command<String> usernameCommand;
  private static Command<String> hostnameCommand;
  private static Command<String> pwdCommand;
  private static Command<ArrayList<File>> lsCommand;
  private static Command<String> manCommand;

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

      String params = cmd.getParams();

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

      path = Utils.expandTilde(path);

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
          } else
            symbol = "";

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

        try {
          IOController.writeLine("");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      cmd.addResult(files);
    });

    manCommand = new Command<String>("man");
    manCommand.setDocumentation("");
    manCommand.setAction(cmd -> {
      int i = 1;
      try {
        IOController.writeLine("Comandos Disponiveis: \n");
      } catch (IOException e) {
        e.printStackTrace();
      }
      for (Map.Entry<String, Command<?>> entry : commandMap.entrySet()) {
        String commandName = entry.getKey();
        try {
          IOController.writeLine((i++) + " " + commandName);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      try {
        IOController.writeLine("");
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    commandMap.put(usernameCommand.getName(), usernameCommand);
    commandMap.put(hostnameCommand.getName(), hostnameCommand);
    commandMap.put(pwdCommand.getName(), pwdCommand);
    commandMap.put(lsCommand.getName(), lsCommand);    
    commandMap.put(manCommand.getName(), manCommand);

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

  public static Command<?> getCommandByName(String name) {
    return commandMap.get(name);
  }
}
