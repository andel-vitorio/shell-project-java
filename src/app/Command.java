package app;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Command<ResultType> {
  private String name;
  private String params;
  private String options;
  private String documentation;
  private Consumer<Command<ResultType>> action;
  private ResultType results;
  private boolean redirectedInput;
  private boolean fromPipeline;

  public Command(String name) {
    this.name = name;
    options = new String();
    redirectedInput = false;
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

  public void setParams(String params) {
    this.params = params;
  }

  public String getParams() {
    return params;
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
    this.params = "";
    this.results = null;
    fromPipeline = false;
    redirectedInput = false;
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
    return this.params != null && this.params.length() > 0; 
  }

  public void setRedirectedInput(boolean redirectedInput) {
    this.redirectedInput = redirectedInput;
  }

  public boolean isRedirectedInput() {
    return this.redirectedInput;
  }

  public void setFromPipeline(boolean fromPipeline) {
    this.fromPipeline = fromPipeline;
  }

  public boolean fromPipeline() {
    return this.fromPipeline;
  }

}