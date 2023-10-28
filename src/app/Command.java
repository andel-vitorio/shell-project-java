package app;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Command {
  private String name;
  private ArrayList<String> arguments;
  private ArrayList<String> options;
  private String documention;
  private Consumer<Command> action;

  public Command(String name) {
    this.name = name;
    this.arguments = new ArrayList<>();
    this.options = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public void setDocumention(String documention) {
    this.documention = documention;
  }

  public String getDocumention() {
    return documention;
  }

  public void addArgument(String arg) {
    this.arguments.add(arg);
  }

  public ArrayList<String> getArguments() {
    return arguments;
  }

  public void addOption(String op) {
    this.options.add(op);
  }

  public void setAction(Consumer<Command> action) {
    this.action = action;
  }

  public void clear() {
    arguments.clear();
    options.clear();
  }

  public void execute() {
    if (action != null)
      action.accept(this);
    else
      IOController.write("<b><red>" + this.name + ":</b><red> Nenhuma ação foi definida para este comando!</red>");
  }
}