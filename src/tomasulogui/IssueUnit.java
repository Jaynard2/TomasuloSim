package tomasulogui;

public class IssueUnit {
  private enum EXEC_TYPE {
    NONE, LOAD, ALU, MULT, DIV, BRANCH} ;

    PipelineSimulator simulator;
    IssuedInst issuee;
    Object fu;
    int srcTag = 0;

    public IssueUnit(PipelineSimulator sim) {
      simulator = sim;
    }

    public void execCycle() {
      // an execution cycle involves:
      // 1. checking if ROB and Reservation Station avail
      // 2. issuing to reservation station, if no structural hazard

      // to issuee, we make an IssuedInst, filling in what we know
      // We check the BTB, and put prediction if branch, updating PC
      //     if pred taken, incr PC otherwise
      // We then send this to the ROB, which fills in the data fields
      // We then check the CDB, and see if it is broadcasting data we need,
      //    so that we can forward during issuee

      // We then send this to the FU, who stores in reservation station
      int addr = simulator.getPCStage().getPC();
      Instruction inst = simulator.getMemory().getInstAtAddr(addr);
      int code = inst.getOpcode();
      issuee = IssuedInst.createIssuedInst(inst);
      Object fu = null;

      switch(code)
      {
      case Instruction.INST_MUL:
        fu = simulator.multiplier;
        break;
      case Instruction.INST_DIV:
        fu = simulator.divider;
        break;
      case Instruction.INST_LW:
        fu = simulator.loader;
        break;
      case Instruction.INST_SW:
      case Instruction.INST_BEQ:
      case Instruction.INST_BGEZ:
      case Instruction.INST_BGTZ:
      case Instruction.INST_BLEZ:
      case Instruction.INST_BLTZ:
      case Instruction.INST_BNE:
        break;
      default:
        fu = simulator.alu;
      }

      if(!simulator.getROB().isFull())
      {
        issuee.pc = addr;
        handleTags(issuee);
        
        issuee.regDestTag = simulator.getROB().rearQ;
        simulator.regs.setSlotForReg(issuee.regDest, issuee.regDestTag);
        
        if(fu instanceof FunctionalUnit && !((FunctionalUnit)fu).isFull())
        {
          ((FunctionalUnit)fu).acceptIssue(issuee);
          simulator.getPCStage().incrPC();
          simulator.getROB().updateInstForIssue(issuee);
        }
        else if(fu instanceof LoadBuffer && ((LoadBuffer)fu).isReservationStationAvail())
        {
          ((LoadBuffer)fu).acceptIssue(issuee);
          simulator.getPCStage().incrPC();
          simulator.getROB().updateInstForIssue(issuee);
        }
      }

    }

    private void handleTags(IssuedInst issue)
    {
      if (issue.regSrc1Used)
      {
        issue.regSrc1Tag = -1;
        if (simulator.regs.getSlotForReg(issue.regSrc1) != -1)
          issue.regSrc1Tag = simulator.getROB().getTagForReg(issue.regSrc1);
      }

      if (issue.regSrc2Used)
      {
        issue.regSrc2Tag = -1;
        if (simulator.regs.getSlotForReg(issue.regSrc2) != -1)
          issue.regSrc2Tag = simulator.getROB().getTagForReg(issue.regSrc2);
      }
    }

  }
