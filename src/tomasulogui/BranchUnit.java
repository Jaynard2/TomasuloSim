package tomasulogui;

public class BranchUnit
        extends FunctionalUnit {

    public static final int EXEC_CYCLES = 1;

    public BranchUnit(PipelineSimulator sim) {
        super(sim);
    }

    public int calculateResult(int station) {
        
        ReservationStation cur = stations[station];
        IssuedInst.INST_TYPE inst = cur.getFunction();

        ROBEntry entr = simulator.reorder.buff[cur.getDestTag()];
        entr.complete = true;
        boolean taken = false;
        
        switch(inst)
        {
            case BEQ:
                taken = cur.data1 == cur.data2;
                break;
            case BGEZ:
                taken = cur.data1 >= cur.data2;
                break;
            case BGTZ:
                taken = cur.data1 > cur.data2;
                break;
            case BLEZ:
                taken = cur.data1 <= cur.data2;
                break;
            case BLTZ:
                taken = cur.data1 < cur.data2;
                break;
            case BNE:
                taken = cur.data1 != cur.data2;
                break;
            case JR:
            case JALR:
                simulator.btb.setBranchAddress(entr.instPC, cur.data1);
                if(cur.data1 == cur.branchAddr)
                {
                    entr.mispredicted = false;
                    return simulator.pc.pc;
                }
                entr.mispredicted = true;
                simulator.squashAllInsts();
                return cur.data1;
                
            // Do nothing - predict always correct
            case J:
            case JAL:
            default:
                return simulator.pc.pc;
        }

        entr.mispredicted = taken != entr.predictTaken;
        simulator.btb.setBranchResult(entr.instPC, taken);
        if(entr.mispredicted)
        {
            simulator.squashAllInsts();
            if(taken)
                return cur.getData2() + simulator.pc.getPC() - 4;
            return simulator.pc.pc;
        }

        return simulator.pc.pc;
    }

    public int getExecCycles() {
        return EXEC_CYCLES;
    }
}
