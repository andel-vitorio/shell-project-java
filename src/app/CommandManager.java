package app;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public final class CommandManager {
  private static Command usernameCommand;
  private static Command hostnameCommand;
  private static Command pwdCommand;

  public static void setup() {

    /* ------------ Username Setup -------------- */
    usernameCommand = new Command("username");
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
    hostnameCommand = new Command("hostname");
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
    pwdCommand = new Command("pwd");
    pwdCommand.setDocumentation("");
    pwdCommand.setAction(cmd -> {
      File currentDir = new File(System.getProperty("user.dir"));
      try {
        String currentDirPath = currentDir.getCanonicalPath();
        if ( !cmd.getOptions().contains("-q") ) IOController.writeLine(currentDirPath + '\n');
        cmd.addResult(currentDirPath);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  public static Command getUsernameCommand() {
    return usernameCommand;
  }

  public static Command getHostnameCommand() {
    return hostnameCommand;
  }

  public static Command getPwdCommand() {
    return pwdCommand;
  }
}
