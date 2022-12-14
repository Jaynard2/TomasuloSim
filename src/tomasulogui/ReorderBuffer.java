package tomasulogui;

import tomasulogui.IssuedInst.INST_TYPE;

public class ReorderBuffer {
  public static final int size = 30;
  int frontQ = 0;
  int rearQ = 0;
  ROBEntry[] buff = new ROBEntry[size];
  int numRetirees = 0;

  PipelineSimulator simulator;
  RegisterFile regs;
  boolean halted = false;

  public ReorderBuffer(PipelineSimulator sim, RegisterFile registers) {
    simulator = sim;
    regs = registers;
  }


  public void squashAll()
  {
    buff = new ROBEntry[size];
    frontQ = 0;
    rearQ = 0;
    numRetirees = 0;
  }

  public void squashFromTag(int tag)
  {
    int rear = rearQ;
      if(frontQ > rearQ && tag > rearQ)
      {
        rear = rearQ + 30;
      }
    for(int i = tag; i < rear; i++)
    {
      buff[i % 30] = null;

      simulator.regs.squashTag(i % 30);
      simulator.loader.squashTag(i % 30);
      simulator.alu.squashTag(i % 30);
      simulator.multiplier.squashTag(i % 30);
      simulator.divider.squashTag(i % 30);
      simulator.branchUnit.squashTag(i % 30);
      simulator.cdb.squashTag(i % 30);
    }
    rearQ = tag;
  }

  public ROBEntry getEntryByTag(int tag) {
    return buff[tag];
  }

  public int getInstPC(int tag) {
    return buff[tag].getInstPC();
  }

  public boolean isHalted() {
    return halted;
  }

  public boolean isFull() {
    return (frontQ == rearQ && buff[frontQ] != null);
  }

  public int getNumRetirees() {
    return numRetirees;
  }

  public boolean retireInst() {
    // 3 cases
    // 1. regular reg dest inst
    // 2. isBranch w/ mispredict
    // 3. isStore
    ROBEntry retiree = buff[frontQ];

    if (retiree == null || !retiree.complete) {
      return false;
    }

    if (retiree.isHaltOpcode()) {
      halted = true;
      return true;
    }

    boolean shouldAdvance = true;

    if(retiree.getOpcode() == INST_TYPE.STORE)
    {
      int storeLocation = retiree.storeLoc + retiree.storeOffset;
      simulator.getMemory().setIntDataAtAddr(storeLocation, retiree.writeValue);
    }
    else if(!retiree.isBranch && retiree.getOpcode() != INST_TYPE.NOP)
    {
      if(getTagForReg(retiree.getWriteReg()) == retiree.tag)
      {
        regs.setReg(retiree.getWriteReg(), retiree.getWriteValue());
        setTagForReg(retiree.getWriteReg(), -1);
      }
    }
    else if(retiree.getOpcode() == INST_TYPE.JAL || retiree.getOpcode() == INST_TYPE.JALR)
    {
      regs.setReg(31, retiree.writeValue);
      setTagForReg(31, -1);
      simulator.issue.stallLength = 0;
    }

      // if mispredict branch, won't do normal advance
      if (shouldAdvance) {
        numRetirees++;
        buff[frontQ] = null;
        frontQ = (frontQ + 1) % size;
      }

    return false;
  }

  public void readCDB(CDB cdb) {
    // check entire CDB for someone waiting on this data
    // could be destination reg
    // could be store address source

    if(cdb.dataValid)
    {
      // Loop to handle stores
      int rear = rearQ;
      if(frontQ > rearQ)
        rear = rearQ + 30;
      for(int i = frontQ; i < rear; i++)
      {
        ROBEntry entry = buff[i % 30];
        if(entry == null)
          continue;
        if(entry.tag == cdb.dataTag)
        {
          if(entry.locTag == -1)
            entry.complete = true;
          entry.writeValue = cdb.dataValue;
        }
        if(entry.locTag == cdb.dataTag)
        {
          entry.storeLoc = cdb.dataValue;
          if(entry.writeValue != -1)
            entry.complete = true;
        }
      }
    }
  }

  public void updateInstForIssue(IssuedInst inst) {
    // the task is to simply annotate the register fields
    // the dest reg will be assigned a tag, which is just our slot#
    // all src regs will either be assigned a tag, read from reg, or forwarded from ROB

    // TODO - possibly nothing if you use my model
    // I use the call to copyInstData below to do 2 things:
    // 1. update the Issued Inst
    // 2. fill in the ROB entry

    // first get a ROB slot
    if (buff[rearQ] != null) {
      throw new MIPSException("updateInstForIssue: no ROB slot avail");
    }
    ROBEntry newEntry = new ROBEntry(this);
    buff[rearQ] = newEntry;
    newEntry.copyInstData(inst, rearQ);

    rearQ = (rearQ + 1) % size;
  }

  public int getTagForReg(int regNum) {
    return (regs.getSlotForReg(regNum));
  }

  public int getDataForReg(int regNum) {
    return (regs.getReg(regNum));
  }

  public void setTagForReg(int regNum, int tag) {
    regs.setSlotForReg(regNum, tag);
  }

}
