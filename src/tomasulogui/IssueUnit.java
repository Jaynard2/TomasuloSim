package tomasulogui;

import java.util.function.IntUnaryOperator;

public class IssueUnit {
  private enum EXEC_TYPE {
    NONE, LOAD, ALU, MULT, DIV, BRANCH} ;

    PipelineSimulator simulator;
    IssuedInst issuee;
    Object fu;
    int srcTag = 0;
    int stallLength = 0;
    int addr;
    int code;

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
      if(stallLength == 0)
      {
        addr = simulator.getPCStage().getPC();
        Instruction inst = simulator.getMemory().getInstAtAddr(addr);
        code = inst.getOpcode();
        issuee = IssuedInst.createIssuedInst(inst);
        issuee.pc = simulator.pc.pc - 4;
        fu = null;

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
          break;
        case Instruction.INST_JAL:
        case Instruction.INST_JALR:
          stallLength = 30; // Stall until saved in reg
        case Instruction.INST_BEQ:
        case Instruction.INST_BGEZ:
        case Instruction.INST_BGTZ:
        case Instruction.INST_BLEZ:
        case Instruction.INST_BLTZ:
        case Instruction.INST_BNE:
        case Instruction.INST_J:
        case Instruction.INST_JR:
          fu = simulator.branchUnit;
          issuee.branch = true;
          simulator.btb.predictBranch(issuee);
          break;
        case Instruction.INST_HALT:
          stallLength = 30;
        default:
          fu = simulator.alu;
        }
        if(!simulator.getROB().isFull())
        {
          issuee.pc = addr;
          handleTags(issuee);
          
          issuee.regDestTag = simulator.getROB().rearQ;
          if(issuee.regDest != -1)
            simulator.regs.setSlotForReg(issuee.regDest, issuee.regDestTag);
          
          if((fu instanceof FunctionalUnit) && !((FunctionalUnit)fu).isFull())
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
          else if(code == Instruction.INST_SW)
          {
            simulator.getPCStage().incrPC();
            simulator.getROB().updateInstForIssue(issuee);
          }
          else // Stall
            stallLength++;
        }          
      }
      else
        stallLength--;

    }

    private void handleTags(IssuedInst issue)
    {
      if (issue.regSrc1Used)
      {
        issue.regSrc1Tag = -1;
        if (simulator.regs.getSlotForReg(issue.regSrc1) != -1)
        {
          issue.regSrc1Tag = simulator.getROB().getTagForReg(issue.regSrc1);
          ROBEntry entry = simulator.getROB().getEntryByTag(issue.regSrc1Tag);
          if(entry.isComplete())
          {
            issue.regSrc1Tag = -1;
            issue.regSrc1Value = entry.writeValue;
            issue.regSrc1Valid = true;
          }

        }
        else
        {
          issue.regSrc1Value = simulator.regs.getReg(issue.regSrc1);
          issue.regSrc1Valid = true;
        }
      }

      if (issue.regSrc2Used)
      {
        issue.regSrc2Tag = -1;
        if (simulator.regs.getSlotForReg(issue.regSrc2) != -1)
        {
          issue.regSrc2Tag = simulator.getROB().getTagForReg(issue.regSrc2);
          ROBEntry entry = simulator.getROB().getEntryByTag(issue.regSrc2Tag);
          if(entry.isComplete())
          {
            issue.regSrc2Tag = -1;
            issue.regSrc2Value = entry.writeValue;
            issue.regSrc2Valid = true;
          }
        }
        else
        {
          issue.regSrc2Value = simulator.regs.getReg(issue.regSrc2);
          issue.regSrc2Valid = true;
        }
      }
    }

  }
