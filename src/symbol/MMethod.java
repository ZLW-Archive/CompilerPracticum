package symbol;

import java.util.HashMap;

public class MMethod extends MIdentifier {

    protected MClass owner;
    protected String returnType;
    protected HashMap<String, MVar> localVarHashMap = new HashMap<String, MVar>();
    protected HashMap<String, MVar> formalParaHashMap = new HashMap<String, MVar>();
//    TODO: merge two hash map together. there is no difference. add another hash map for "temp" variable

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

    public boolean insertLocalVar(MVar var) {
        String name = var.getName();
        if (localVarHashMap.containsKey(name)) {
            return false;
        }
        localVarHashMap.put(name, var);
        return true;
    }

    public MVar getLocalVar(String key) {
        if (!localVarHashMap.containsKey(key)) {
            return null;
        }
        return localVarHashMap.get(key);
    }

    public MVar getFormalPara(String key) {
        if (!formalParaHashMap.containsKey(key)) {
            return null;
        }
        return formalParaHashMap.get(key);
    }

}
