package app;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

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

  public static void pipe(String pipeline) throws IOException, InterruptedException {
    {
      String[] commands = pipeline.split("\\|");
      ProcessBuilder[] builders = new ProcessBuilder[commands.length];
      
      var pwd = CommandManager.getPwdCommand();
      pwd.addOption("-q");
      pwd.execute();

      String directory = pwd.getResults();

      pwd.clear();

      for (int i = 0; i < commands.length; i++) {
        List<String> params = new ArrayList<>();
        params.add("java");
        params.add("-cp");
        params.add("./bin");
        params.add("App");
        params.add("pipeline");
        params.add(commands[i]);
        params.add(directory);
        if (i!=0) params.add("redirected entry");
        builders[i] = new ProcessBuilder(params);
      }


      List<Process> processes = ProcessBuilder.startPipeline(Arrays.asList(builders));
      Process last = (Process) processes.get(processes.size() - 1);
      try (InputStream is = last.getInputStream();
          Reader isr = new InputStreamReader(is);
          BufferedReader r = new BufferedReader(isr)) {
        String line;
        while ((line = r.readLine()) != null) {
          System.out.println(line);
        }
      }

    }
  }

  public void executeCommandFromText(String text, boolean fromPipeline) throws IOException, InterruptedException {
    Pattern pattern = Pattern.compile("(\\S+)(?:\\s+(.*))?");
    Matcher matcher = pattern.matcher(text);
    String cmd = "", params = "";
    boolean redirectionInput = false;

    if (text.trim().endsWith("&")) {
      String javaPath = "java";
      String classPath = "-cp";
      String classPathValue = "./bin";
      String className = "App";
      String flag = "background";

      text = text.substring(0, text.length() - 1).trim();

      List<String> command = new ArrayList<>();
      command.add(javaPath);
      command.add(classPath);
      command.add(classPathValue);
      command.add(className);
      command.add(flag);
      command.add(text);

      try {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.inheritIO();
        processBuilder.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else if (text.contains("|")) {
      pipe(text);
      return;
    } else {

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
        } else
          System.out.println(
              Utils.parseTags("<red><b>Erro:<reset><red> É necessário informar o arquivo de saída.<reset>\n"));
      } else
        IOController.setOutputStream("stdout");

      if (params != null && params.contains("<")) {
        int index = params.indexOf("<");
        String[] values = params.substring(index + 1).trim().split("\\s+");
        if (values.length > 0 && values[0].trim() != "") {
          params = params.replace(values[0], "");
          params = params.replace("<", "");
          redirectionInput = IOController.setInputStream(values[0]);
        } else
          System.out.println(
              Utils.parseTags("<red><b>Erro:<reset><red> É necessário informar o arquivo de entrada.<reset>\n"));
      } else
        IOController.setInputStream("stdin");

      if (cmd.equals("exit"))
        this.isRunning = false;
      else if ((command = CommandManager.getCommandByName(cmd.trim())) != null) {
        if (params != null)
          command.setParams(params.trim() + '\n');
        command.setFromPipeline(fromPipeline);
        command.setRedirectedInput(redirectionInput);
        command.execute();
        command.clear();
      } else {
        IOController.writeLine("<red>Comando Inexistente: <b>" + cmd + "<reset>\n");
      }

    }

    if (!fromPipeline)
      System.out.println();
    IOController.reset();
  }

  public void run() throws IOException, InterruptedException {
    IOController.writeLine("<b>Shell Project v1.0<reset>");
    IOController.writeLine("Digite <b>'help'<reset> para obter ajuda ou <b>'exit'<reset> para sair.\n");

    isRunning = true;

    while (isRunning) {
      this.writeCommandLine();
      String commandLine = IOController.readLine();
      executeCommandFromText(commandLine, false);
    }

  }
}