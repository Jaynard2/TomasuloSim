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
    switch(inst)
    {
    case ADD:
    case ADDI:
      return cur.getData1() + cur.getData2();
    case SUB:
      return cur.getData1() - cur.getData2();
    case SLL:
      return cur.getData1() << cur.getData2();
    case SRL:
      return cur.getData1() >> cur.getData2();
    case SRA:
      return cur.getData1() >>> cur.getData2();
    default:
      return 0;
    }
  }

  public int getExecCycles() {
    return EXEC_CYCLES;
  }
}
