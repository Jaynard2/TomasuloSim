package tomasulogui;

import tomasulogui.IssuedInst.INST_TYPE;

public class ROBEntry {
  ReorderBuffer rob;

  // TODO - add many more fields into entry
  // I deleted most, and only kept those necessary to compile GUI
  boolean complete = false;
  boolean predictTaken = false;
  boolean mispredicted = false;
  int instPC = -1;
  int writeReg = -1;
  int writeValue = -1;
  int tag = -1;
  int locTag = -1;
  int storeOffset = -1;
  int storeLoc = -1;
  boolean isBranch = false;

  IssuedInst.INST_TYPE opcode;

  public ROBEntry(ReorderBuffer buffer) {
    rob = buffer;
  }

  public boolean isComplete() {
    return complete;
  }

  public boolean branchMispredicted() {
    return mispredicted;
  }

  public boolean getPredictTaken() {
    return predictTaken;
  }

  public int getInstPC() {
    return instPC;
  }

  public IssuedInst.INST_TYPE getOpcode () {
    return opcode;
  }

  public boolean isHaltOpcode() {
    return (opcode == IssuedInst.INST_TYPE.HALT);
  }

  public void setBranchTaken(boolean result) {
    // TODO - maybe more than simple set
  }

  public int getWriteReg() {
    return writeReg;
  }

  public int getWriteValue() {
    return writeValue;
  }

  public void setWriteValue(int value) {
    writeValue = value;
  }

  public void copyInstData(IssuedInst inst, int frontQ) {
    instPC = inst.getPC();
    inst.setRegDestTag(frontQ);

    // TODO - This is a long and complicated method, probably the most complex
    // of the project.  It does 2 things:
    // 1. update the instruction, as shown in 2nd line of code above
    // 2. update the fields of the ROBEntry, as shown in the 1st line of code above
    instPC = inst.pc;
    writeReg = inst.regDest;
    tag = inst.regDestTag;
    opcode = inst.getOpcode();
    isBranch = inst.isBranch();
    if(isBranch)
    {
      predictTaken = inst.branchPrediction;
      writeValue = inst.getPC() + 4;
    }
    if(inst.getOpcode() == INST_TYPE.STORE)
    {
      storeOffset = inst.getImmediate();
      tag = inst.getRegSrc2Tag();
      locTag = inst.getRegSrc1Tag();
      if(locTag == -1)
      {
        storeLoc = inst.getRegSrc1Value();
      }

      if(tag == -1)
      {
        if(locTag == -1)
          complete = true;
        writeValue = inst.getRegSrc2Value();
      }
    }

  }

}
