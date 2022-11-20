package tomasulogui;

public class IntDivide extends FunctionalUnit {

    public static final int EXEC_CYCLES = 7;

    public IntDivide(PipelineSimulator sim) {
        super(sim);
    }

    public int calculateResult(int station) {
        
        ReservationStation cur = stations[station];
        IssuedInst.INST_TYPE inst = cur.getFunction();

        if(inst == IssuedInst.INST_TYPE.DIV)
        {
            return (int)(cur.data1 / cur.data2);
        }

        return 0;
    }

    public int getExecCycles() {
        return EXEC_CYCLES;
    }
}
