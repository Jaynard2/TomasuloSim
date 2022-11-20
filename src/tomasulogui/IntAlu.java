package tomasulogui;

import tomasulogui.IssuedInst.INST_TYPE;

public class IntAlu extends FunctionalUnit{
  public static final int EXEC_CYCLES = 1;

  public IntAlu(PipelineSimulator sim) {
    super(sim);
  }


  public int calculateResult(int station) {
     // just placeholder code

    ReservationStation cur = stations[station];
    INST_TYPE inst = cur.getFunction();
    int d1 = cur.getData1();
    int d2 = cur.getData2();
    switch(inst)
    {
    case ADD:
    case ADDI:
      return d1 + d2;
    case SUB:
      return d1 - d2;
    case SLL:
      return d1 << d2;
    case SRL:
      return d1 >> d2;
    case SRA:
      return d1 >>> d2;
    case AND:
    case ANDI:
      return d1 & d2;
    case OR:
    case ORI:
      return d1 | d2;
    case XOR:
    case XORI:
      return d1 ^ d2;
    default:
      return 0;
    }
  }

  public int getExecCycles() {
    return EXEC_CYCLES;
  }
}
