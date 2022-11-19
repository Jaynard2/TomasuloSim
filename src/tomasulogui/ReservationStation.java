package tomasulogui;

public class ReservationStation {
  PipelineSimulator simulator;

  int tag1;
  int tag2;
  int data1;
  int data2;
  boolean data1Valid = false;
  boolean data2Valid = false;
  // destTag doubles as branch tag
  int destTag;
  IssuedInst.INST_TYPE function = IssuedInst.INST_TYPE.NOP;

  // following just for branches
  int addressTag;
  boolean addressValid = false;
  int address;
  boolean predictedTaken = false;

  int oldCDBTag;
  int oldCDBValue;
  boolean cbdWasValid = false;

  public ReservationStation(PipelineSimulator sim) {
    simulator = sim;
  }

  public int getDestTag() {
    return destTag;
  }

  public int getData1() {
    return data1;
  }

  public int getData2() {
    return data2;
  }

  public boolean isPredictedTaken() {
    return predictedTaken;
  }

  public IssuedInst.INST_TYPE getFunction() {
    return function;
  }

  public void snoop(CDB cdb) {
    if(!cdb.dataValid)
      return;

    int tag = cdb.getDataTag();
    if(tag == tag1)
    {
      data1 = cdb.getDataValue();
      data1Valid = true;
    }
    if(tag == tag2)
    {
      data2 = cdb.getDataValue();
      data2Valid = true;
    }

  }

  public boolean isReady() {
    return data1Valid && data2Valid;
  }

  public void loadInst(IssuedInst inst) {
    destTag = inst.getRegDestTag();
    tag1 = inst.getRegSrc1Tag();
    tag2 = inst.getRegSrc2Tag();

    data1Valid = false;
    data2Valid = false;

    int reg1 = inst.getRegSrc1();
    int reg2 = inst.getRegSrc2();
    RegisterFile regs = simulator.regs;
    if(inst.regSrc1Used && tag1 == -1)
    {
      data1 = regs.getReg(reg1);
      if(cbdWasValid && oldCDBTag == tag1)
        data1 = oldCDBValue;
      data1Valid = true;
    }
    if(inst.regSrc2Used)
    {
      if(tag2 == -1)
      {
        data2 = regs.getReg(reg2);
        if(cbdWasValid && oldCDBTag == tag2)
          data2 = oldCDBValue;
        data2Valid = true;
      }
    }
    else
    {
      data2 = inst.getImmediate();
      data2Valid = true;
    }

    function = inst.getOpcode();

    cbdWasValid = simulator.cdb.dataValid;
    oldCDBTag = simulator.cdb.dataTag;
    oldCDBValue = simulator.cdb.dataValue;
  }
}
