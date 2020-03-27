package piglet;

import symbol.*;

import java.util.HashMap;

public class PigletLabels
{
    public PigletLabels(int _intend)
    {
        intend = _intend;
    }

    public PigletLabels(PigletLabels _p)
    {
        varList = _p.varList;
        mc = _p.mc;
        intend = _p.intend;
    }
    public PigletLabels(){}
    public HashMap<String, Integer> varList;
    public MClass mc;
    public MMethod mm;
    public int intend;
}