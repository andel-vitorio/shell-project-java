package app;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.nio.file.attribute.*;

import utils.Utils;

public final class CommandManager {

  private static Map<String, Command<?>> commandMap = new HashMap<>();

  private static Command<String> usernameCommand;
  private static Command<String> hostnameCommand;
  private static Command<String> pwdCommand;
  private static Command<ArrayList<File>> lsCommand;
  private static Command<String> manCommand;
  private static Command<String> echoCommand;
  private static Command<Void> touchCommand;
  private static Command<Void> mkdirCommand;
  private static Command<Void> rmCommand;
  private static Command<Void> mvCommand;
  private static Command<Void> catCommand;
  private static Command<Void> cdCommand;

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

  public static void setup() throws IOException {

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
      String path = System.getProperty("user.dir");
      String mode = "";


      String params = cmd.getParams();

      if (params != null && params.trim().length() > 0) {
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

    /* ------------ man Setup -------------- */
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

    /* ------------ echo Setup -------------- */
    echoCommand = new Command<String>("echo");
    echoCommand.setDocumentation("");
    echoCommand.setAction(cmd -> {
      String msg = cmd.getParams(), line;

      if (cmd.isRedirectedInput()) {
        msg = "";
        while ((line = IOController.readLine()) != null) {
          msg += line + '\n';
        }
      }
      try {
        IOController.writeLine(Utils.removeQuotes(msg));
      } catch (IOException e) {
        e.printStackTrace();
      }
      cmd.addResult(msg);
    });

    /* ------------ touch Setup -------------- */
    touchCommand = new Command<Void>("touch");
    touchCommand.setDocumentation("");
    touchCommand.setAction(cmd -> {
      String params = cmd.getParams();

      if (cmd.isRedirectedInput()) {
        String line;
        while ((line = IOController.readLine()) != null) {
          params += (line + '\n');
        }
      }

      ArrayList<String> filepaths = Utils.extractQuotedStrings(params);

      for (String path : filepaths) {
        File file = new File(path);

        Set<PosixFilePermission> permissions = EnumSet.of(PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE, PosixFilePermission.GROUP_READ, PosixFilePermission.OTHERS_READ);

        try {
          FileOutputStream fos = new FileOutputStream(file);
          java.nio.file.Files.setPosixFilePermissions(file.toPath(), permissions);
          fos.close();
          IOController.writeLine("Arquivo " + file + " criado com sucesso");
        } catch (IOException e) {
          try {
            IOController.writeLine(e.getMessage());
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
      }

      System.out.println("");
    });

    /* ------------ mkdir Setup -------------- */
    mkdirCommand = new Command<Void>("mkdir");
    mkdirCommand.setDocumentation("");
    mkdirCommand.setAction(cmd -> {
      String params = cmd.getParams();

      if (cmd.isRedirectedInput()) {
        String line;
        while ((line = IOController.readLine()) != null) {
          params += (line + '\n');
        }
      }

      ArrayList<String> dirpaths = Utils.extractQuotedStrings(params);

      for (String path : dirpaths) {

        path = Utils.expandTilde(path);
        String p = "";

        for (String str : path.split("/")) {
          p += str + "/";
          File directory = new File(p);
          if (directory.mkdirs()) {
            try {
              IOController.writeLine("Diretório " + directory.getName() + " criado com sucesso");
            } catch (IOException e) {
              e.printStackTrace();
            }
          } else
            System.out.println("Falha ao criar o diretório " + directory.getName());
        }
      }

      System.out.println("");
    });

    /* ------------ mkdir Setup -------------- */
    rmCommand = new Command<Void>("rm");
    rmCommand.setDocumentation("");
    rmCommand.setAction(cmd -> {
      String params = cmd.getParams();

      if (cmd.isRedirectedInput()) {
        String line;
        while ((line = IOController.readLine()) != null) {
          params += (line + '\n');
        }
      }

      ArrayList<String> dirpaths = Utils.extractQuotedStrings(params);

      for (String path : dirpaths) {

        path = Utils.expandTilde(path);

        File item = new File(path);

        if (!item.exists()) {
          System.out.println("O arquivo ou diretório não existe.");
          return;
        }

        if (item.isFile()) {
          if (item.delete()) {
            try {
              IOController.writeLine("Arquivo \"" + item.getName() + "\" deletado com sucesso");
            } catch (IOException e) {
              e.printStackTrace();
            }
          } else {
            System.out.println("Falha ao excluir o arquivo \"" + item + "\"");
          }
        } else if (item.isDirectory()) {
          File[] contents = item.listFiles();
          if (contents != null) {
            for (File content : contents) {
              cmd.setParams(content.getAbsolutePath());
              cmd.execute();
              cmd.clear();
            }
          }

          if (item.delete()) {
            try {
              IOController.writeLine("Diretório \"" + item.getName() + "\" apagado com sucesso");
            } catch (IOException e) {
              e.printStackTrace();
            }
          } else {
            try {
              IOController.writeLine("Falha ao excluir o diretório \"" + item.getName());
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        } else {
          try {
            IOController.writeLine("Arquivo ou Diretório inexistente");
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    });

    /* ------------ mv Setup -------------- */
    mvCommand = new Command<Void>("mv");
    mvCommand.setDocumentation("");
    mvCommand.setAction(cmd -> {
      String params = cmd.getParams();

      if (cmd.isRedirectedInput()) {
        String line;
        while ((line = IOController.readLine()) != null) {
          params += (line + '\n');
        }
      }

      ArrayList<String> paths = Utils.extractQuotedStrings(params);
      paths.set(0, Utils.expandTilde(paths.get(0)));
      paths.set(1, Utils.expandTilde(paths.get(1)));

      if (paths.size() > 1) {
        File sourceFile = new File(paths.get(0));
        File targetFile = new File(paths.get(1));

        if (!sourceFile.exists()) {
          try {
            IOController.writeLine("Arquivo não encontrado!");
          } catch (IOException e) {
            e.printStackTrace();
          }
          return;
        }

        try {
          if (targetFile.exists() && sourceFile.getCanonicalFile().equals(targetFile.getCanonicalFile())) {
            try {
              IOController.writeLine("O arquivo de origem é o mesmo de destino");
            } catch (IOException e) {
              e.printStackTrace();
            }
            return;
          }
        } catch (IOException e) {
          e.printStackTrace();
        }

        if (targetFile.exists()) {
          if (targetFile.isDirectory()) {
            File sourceFileName = new File(paths.get(0));
            String fileName = sourceFileName.getName();
            File targetPath = new File(paths.get(1), fileName);

            try {
              java.nio.file.Files.move(sourceFileName.toPath(), targetPath.toPath());
            } catch (IOException e) {
              e.printStackTrace();
            }
          } else {
            try {
              java.nio.file.Files.move(sourceFile.toPath(), targetFile.toPath());
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        } else {
          try {
            java.nio.file.Files.move(sourceFile.toPath(), targetFile.toPath());
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

      }

    });

    /* ------------ cat Setup -------------- */
    catCommand = new Command<Void>("cat");
    catCommand.setDocumentation("");
    catCommand.setAction(cmd -> {
      String params = cmd.getParams();

      if (cmd.isRedirectedInput()) {
        String line;
        while ((line = IOController.readLine()) != null) {
          params += (line + '\n');
        }
      }

      ArrayList<String> paths = Utils.extractQuotedStrings(params);

      for (String path : paths) {
        path = Utils.expandTilde(path);
        try {
          BufferedReader br = new BufferedReader(new FileReader(path));
          String line;
          while ((line = br.readLine()) != null) {
            System.out.println(line);
          }
          br.close();
        } catch (IOException e) {
          System.err.println("Erro ao ler o arquivo: " + path);
        }
      }

    });

    /* ------------ cat Setup -------------- */
    cdCommand = new Command<Void>("cd");
    cdCommand.setDocumentation("");
    cdCommand.setAction(cmd -> {
      String params = cmd.getParams();

      if (cmd.isRedirectedInput()) {
        String line;
        while ((line = IOController.readLine()) != null) {
          params += (line + '\n');
        }
      }

      ArrayList<String> paths = Utils.extractQuotedStrings(params);

      File dir = new File(Utils.resolvePath(paths.get(0)));

      if (dir.exists() && dir.isDirectory()) {
        System.setProperty("user.dir", dir.getAbsolutePath());
      } else {
        System.out.println("Diretório não encontrado: " + dir.getName());
      }

    });

    commandMap.put(usernameCommand.getName(), usernameCommand);
    commandMap.put(hostnameCommand.getName(), hostnameCommand);
    commandMap.put(pwdCommand.getName(), pwdCommand);
    commandMap.put(lsCommand.getName(), lsCommand);
    commandMap.put(manCommand.getName(), manCommand);
    commandMap.put(echoCommand.getName(), echoCommand);
    commandMap.put(touchCommand.getName(), touchCommand);
    commandMap.put(mkdirCommand.getName(), mkdirCommand);
    commandMap.put(rmCommand.getName(), rmCommand);
    commandMap.put(mvCommand.getName(), mvCommand);
    commandMap.put(catCommand.getName(), catCommand);
    commandMap.put(cdCommand.getName(), cdCommand);
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
