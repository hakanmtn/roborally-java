package io.github.hakanmtn.roborally.model.program;

public class Program {

  private final Register[] registers = new Register[5];

  public Program() {
    for (int index = 0; index < registers.length; index++) {
      registers[index] = new Register();
    }
  }

  public Register getRegister(int index) {
    return registers[index];
  }
}
