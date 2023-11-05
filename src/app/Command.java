package app;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Command<ResultType> {
  private String name;
  private String arguments = "";
  private String options;
  private String documentation;
  private Consumer<Command<ResultType>> action;
  private ResultType results;

  public Command(String name) {
    this.name = name;
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

  public void addArguments(String name, String value) {
    this.arguments += ("--" + name + " " + value + ' ');
  }

  public String getArguments() {
    return arguments;
  }

  public void addOption(String op) {
    this.options = op;
  }

  public String getOptions() {
    return options;
  }

  public void setAction(Consumer<Command<ResultType>> action) {
    this.action = action;
  }

  public void clear() {
    this.options = "";
    this.arguments = "";
    this.results = null;
  }

  public void execute() {
    if (action != null)
      action.accept(this);
    else
      IOController.write("<b><red>" + this.name + ":</b><red> Nenhuma ação foi definida para este comando!</red>");
  }

  public void addResult(ResultType result) {
    this.results = result;
  }

  public ResultType getResults() {
    return results;
  }

  public boolean hasArguments() {
    return this.arguments != null && this.arguments.length() > 0; 
  }
}