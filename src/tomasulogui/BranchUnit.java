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
        
        switch(inst)
        {
            case BEQ:
                if(cur.data1 == cur.data2)
                    return cur.getDestTag() - 4;
                break;
            case BGEZ:
                if(cur.data1 >= 0)
                    return cur.getDestTag() - 4;
                break;
            case BGTZ:
                if(cur.data1 > 0)
                    return cur.getDestTag() - 4;
                break;
            case BLEZ:
                if(cur.data1 <= 0)
                    return cur.getDestTag() - 4;
                break;
            case BLTZ:
                if(cur.data1 < 0)
                    return cur.getDestTag() - 4;
                break;
            case BNE:
                if(cur.data1 != cur.data2)
                    return cur.getDestTag() - 4;
                break;
            case J:
            case JAL:
            case JR:
            case JALR:
                return cur.getDestTag() - 4;
            default:
                return 0;
        }

        return simulator.pc.pc;
    }

    public int getExecCycles() {
        return EXEC_CYCLES;
    }
}
