package app;

import java.io.IOException;

public final class CommandManager {
  private static Command usernameCommand;

  public static void setup() {
    usernameCommand = new Command("username"); 
    usernameCommand.setDocumentation("<b>Username<reset>\n\nObtém o usuário atual do sistema.");
    usernameCommand.setAction(cmd -> {
      try {
        String username = System.getProperty("user.name");
        if (!cmd.getOptions().contains("--no-print"))
          IOController.writeLine(username);
        cmd.addResult(username);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    
  }

  public static Command getUsernameCommand() {
    return usernameCommand;
  }
}
