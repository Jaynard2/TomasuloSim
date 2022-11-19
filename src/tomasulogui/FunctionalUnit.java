package tomasulogui;

import tomasulogui.IssuedInst.INST_TYPE;

public abstract class FunctionalUnit {
  PipelineSimulator simulator;
  ReservationStation[] stations = new ReservationStation[2];
  boolean needsCDB = false;
  int currCycleCount = 0;
  int curStation = -1;
  
  public FunctionalUnit(PipelineSimulator sim) {
    simulator = sim;
    stations[0] = new ReservationStation(sim);
    stations[1] = new ReservationStation(sim);
  }
 
  public void squashAll() {
    stations[0].loadInst(IssuedInst.createIssuedInst(Instruction.getInstructionFromName("NOP")));
    stations[1].loadInst(IssuedInst.createIssuedInst(Instruction.getInstructionFromName("NOP")));
    currCycleCount = 0;
    curStation = -1;
    needsCDB = false;
  }

  public abstract int calculateResult(int station);

  public abstract int getExecCycles();

  public void execCycle(CDB cdb) {
    //todo - start executing, ask for CDB, etc.
    stations[0].snoop(cdb);
    stations[1].snoop(cdb);

    if(curStation != -1 && currCycleCount < getExecCycles())
    {
      currCycleCount++;
    } 
    else if(currCycleCount == getExecCycles())
    {
      needsCDB = true;
    }
  }

  public int getCurrentStation()
  {
    needsCDB = false;
    currCycleCount = 0;
    int oldCurStation = curStation;
    if(oldCurStation != -1 && stations[(oldCurStation + 1) % 2].isReady())
      curStation = (curStation + 1) % 2;
    else
      curStation = -1;

    stations[oldCurStation].loadInst(IssuedInst.createIssuedInst(Instruction.getInstructionFromName("NOP")));

    return curStation;
  }

  public int getTag(int station)
  {
    return stations[station].destTag;
  }

  public void acceptIssue(IssuedInst inst) 
  {
    for(int i = 0; i < 2; i++)
    {
      if(stations[i].function == INST_TYPE.NOP)
      {
        stations[i].loadInst(inst);
        if(curStation == -1)
          curStation = i;
        break;
      }
    }
  }

  public boolean isFull()
  {
    return stations[0].function != INST_TYPE.NOP && stations[1].function != INST_TYPE.NOP;
  }

}
