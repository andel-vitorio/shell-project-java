import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import app.CommandManager;
import app.Shell;

public class App {
  public static void main(String[] args) throws IOException, InterruptedException {

    Shell shell = new Shell();
    CommandManager.setup();

    if (args.length == 2) {
      if (args[0].equals("background")) {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String processName = runtimeMXBean.getName();
        long processID = Long.parseLong(processName.split("@")[0]);
        System.out.println("\nProcesso em background criado [id:" + processID + "]: " + args[1]);
        shell.executeCommandFromText(args[1]);
        System.out.println("Processo em background finalizado [id:" + processID + "]: " + args[1]);

      }
    } else {
      shell.run();
    }

  }
}