package minijava.piglet;

import minijava.symbol.*;

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
        mm = _p.mm;
        intend = _p.intend;
    }
    public PigletLabels(){}
    public HashMap<String, Integer> varList;
    public int paramlength;
    public int paramextend;
    public int paramtot;
    public MClass mc;
    public MMethod mm;
    public int intend;
}