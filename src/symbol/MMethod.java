package symbol;

import java.util.HashMap;
import java.util.Vector;

public class MMethod extends MIdentifier {

    protected MClass ownerClass;
    protected String returnType;

    protected HashMap<String, MVar> formalParaHashMap = new HashMap<String, MVar>();
    protected Vector<String> formalParaTypeVector = new Vector<String>();
    protected int formalParaTypeCheckerIndex;

    protected HashMap<String, MVar> varHashMap = new HashMap<String, MVar>();

    public MMethod(String _name, int _col, int _row, MClass _owner, String _returnType) {
        super(_name, _name, _col, _row);
        ownerClass = _owner;
        returnType = _returnType;
    }

    public void printSymbolList(int intend)
    {
        for (int i = 0;i < intend; ++i)
        {
            System.out.print("| ");
        }
        System.out.print("MMethod " + name + "\n");
        for (MVar x: formalParaHashMap.values())
            x.printSymbolList(intend + 1);
    }
    public boolean insertFormalPara(MVar var) {
        String name = var.getName();
        String type = var.getType();
        if (formalParaHashMap.containsKey(name)) {
            return false;
        }
        formalParaHashMap.put(name, var);
        formalParaTypeVector.add(type);
        return true;
    }

    public boolean insertVar(MVar var) {
        String name = var.getName();
        if (varHashMap.containsKey(name)) {
            return false;
        }
        varHashMap.put(name, var);
        return true;
    }

    public MVar getVar(String key) {
        if (varHashMap.containsKey(key)) {
            return varHashMap.get(key);
        }
        else if (ownerClass.getVar(key) != null) {
            return ownerClass.getVar(key);
        }
        return null;
    }

    public MVar getFormalPara(String key) {
        if (!formalParaHashMap.containsKey(key)) {
            return null;
        }
        return formalParaHashMap.get(key);
    }

    public MType getReturnType() {
        return new MType(returnType);
    }

    public MClass getOwnerClass() {
        return ownerClass;
    }

    public void startCheckFormalPara() {
        formalParaTypeCheckerIndex = 0;
    }

    public void checkingFormalPara(String curParaType, MClassList allClassList) {
        if (formalParaTypeCheckerIndex == -1) {return;}

        if (formalParaTypeCheckerIndex >= formalParaTypeVector.size()) {
            formalParaTypeCheckerIndex = -1;
            return;
        }

        String requiredType = formalParaTypeVector.elementAt(formalParaTypeCheckerIndex);
//        if (!curParaType.equals(requiredType)) {
        if (! allClassList.checkExtendAssign(requiredType, curParaType)) {
            formalParaTypeCheckerIndex = -1;
        }
        else {
            formalParaTypeCheckerIndex += 1;
        }
    }

    public boolean endCheckFormalPara() {
        return formalParaTypeCheckerIndex == formalParaTypeVector.size() && formalParaTypeCheckerIndex != -1;
    }

}
