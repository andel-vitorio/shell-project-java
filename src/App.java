import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import app.Command;
import app.CommandManager;
import app.IOController;
import app.Shell;

public class App {
  public static void main(String[] args) throws IOException, InterruptedException {

    Shell shell = new Shell();
    CommandManager.setup();

    if (args.length > 1) {
      if (args[0].equals("background")) {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String processName = runtimeMXBean.getName();
        long processID = Long.parseLong(processName.split("@")[0]);
        System.out.println("\nProcesso em background criado [id:" + processID + "]: " + args[1]);
        shell.executeCommandFromText(args[1].trim(), false);
        System.out.println("Processo em background finalizado [id:" + processID + "]: " + args[1]);
      } else if (args[0].equals("pipeline")) {

        boolean fromPipeline = false;

        var cd = CommandManager.getCdCommand();
        cd.setParams(args[2]);
        cd.execute();
        cd.clear();

        for (String str : args)
          if (str.equals("redirected entry"))
            fromPipeline = true;

        shell.executeCommandFromText(args[1].trim(), fromPipeline);
        System.out.println();
      }
    } else {
      shell.run();
    }

  }
}