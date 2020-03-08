package symbol;

import java.util.HashMap;

public class MMethod extends MIdentifier {

    protected MClass owner;
    protected String returnType;
    protected HashMap<String, MVar> formalParaHashMap = new HashMap<String, MVar>();
    protected HashMap<String, MVar> varHashMap = new HashMap<String, MVar>();

    public MMethod(String _name, int _col, int _row, MClass _owner, String _returnType) {
        super("Method", _name, _col, _row);
        owner = _owner;
        returnType = _returnType;
    }

    public boolean insertFormalPara(MVar var) {
        String name = var.getName();
        if (formalParaHashMap.containsKey(name)) {
            return false;
        }
        formalParaHashMap.put(name, var);
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
        if (!varHashMap.containsKey(key)) {
            return null;
        }
        return varHashMap.get(key);
    }

    public MVar getFormalPara(String key) {
        if (!formalParaHashMap.containsKey(key)) {
            return null;
        }
        return formalParaHashMap.get(key);
    }

}
