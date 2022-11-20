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
/*
        // Returning target address??
        switch(inst)
        {
            case J:
            case JAL:
            case JR:
            case JALR:
            case BEQ:
            case BGEZ:
            case BGTZ:
            case BLEZ:
            case BLTZ:
            case BNE:
                return cur.getDestTag();
            default:
                return 0;
        }
*/

        int result = 1;
        return result;
    }

    public int getExecCycles() {
        return EXEC_CYCLES;
    }
}
