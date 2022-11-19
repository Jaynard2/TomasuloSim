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
    stations[0] = null;
    stations[1] = null;
  }
 
  public void squashAll() {
    stations[0] = null;
    stations[1] = null;
    currCycleCount = 0;
    curStation = -1;
    needsCDB = false;
  }

  public abstract int calculateResult(int station);

  public abstract int getExecCycles();

  public void execCycle(CDB cdb) {
    //todo - start executing, ask for CDB, etc.
    if(stations[0] != null)
      stations[0].snoop(cdb);
    if(stations[1] != null)
      stations[1].snoop(cdb);

    if(curStation != -1 && currCycleCount < getExecCycles())
    {
      currCycleCount++;
      if(currCycleCount == getExecCycles())
        needsCDB = true;
    } 
  }

  public int getCurrentStation()
  {
    return curStation;
  }

  public void retireStation(int station)
  {
    needsCDB = false;
    currCycleCount = 0;
    int oldCurStation = curStation;
    if(oldCurStation != -1 && stations[(oldCurStation + 1) % 2] != null && stations[(oldCurStation + 1) % 2].isReady())
      curStation = (curStation + 1) % 2;
    else
      curStation = -1;
    System.out.println("Removing " + station);
    stations[oldCurStation] = null;
  }

  public int getTag(int station)
  {
    return stations[station].destTag;
  }

  public void acceptIssue(IssuedInst inst) 
  {
    for(int i = 0; i < 2; i++)
    {
      if(stations[i] == null)
      {
        stations[i] = new ReservationStation(simulator);
        stations[i].loadInst(inst);
        if(curStation == -1)
          curStation = i;
        break;
      }
    }
  }

  public boolean isFull()
  {
    return stations[0] != null && stations[1] != null;
  }

}
