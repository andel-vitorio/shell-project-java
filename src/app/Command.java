package app;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Command {
  private String name;
  private ArrayList<String> arguments;
  private String options;
  private String documentation;
  private Consumer<Command> action;
  private ArrayList<String> results;

  public Command(String name) {
    this.name = name;
    this.arguments = new ArrayList<>();
    this.results = new ArrayList<>();
    options = new String();
  }

  public String getName() {
    return name;
  }

  public void setDocumentation(String doc) {
    this.documentation = doc;
  }

  public String getDocumentation() {
    return documentation;
  }

  public void addArgument(String arg) {
    this.arguments.add(arg);
  }

  public ArrayList<String> getArguments() {
    return arguments;
  }

  public void addOption(String op) {
    this.options = op;
  }

  public String getOptions() {
    return options;
  }

  public void setAction(Consumer<Command> action) {
    this.action = action;
  }

  public void clear() {
    arguments.clear();
    results.clear();
    this.options = "";
  }

  public void execute() {
    if (action != null)
      action.accept(this);
    else
      IOController.write("<b><red>" + this.name + ":</b><red> Nenhuma ação foi definida para este comando!</red>");
  }

  public void addResult(String result) {
    this.results.add(result);
  }

  public ArrayList<String> getResults() {
    return results;
  }
}