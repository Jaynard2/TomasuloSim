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
      FunctionalUnit fu;
      switch(code)
      {
      case Instruction.INST_MUL:
        fu = simulator.multiplier;
        break;
      case Instruction.INST_DIV:
        fu = simulator.divider;
        break;
      default:
        fu = simulator.alu;
      }

      if(!simulator.getROB().isFull() && !fu.isFull())
      {
        issuee = IssuedInst.createIssuedInst(inst);
        issuee.pc = addr;
        
        issuee.regSrc1Tag = srcTag++;
        issuee.regSrc2Tag = srcTag++;
        issuee.regDestTag = simulator.getROB().rearQ;

        fu.acceptIssue(issuee);
        simulator.getROB().updateInstForIssue(issuee);

        simulator.getPCStage().incrPC();
      }

    }

  }
