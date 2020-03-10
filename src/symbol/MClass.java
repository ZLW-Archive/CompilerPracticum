package symbol;

import java.util.HashMap;

public class MClass extends MIdentifier {

    protected HashMap<String, MVar> varHashMap = new HashMap<String, MVar>();
    protected HashMap<String, MMethod> methodHashMap = new HashMap<String, MMethod>();

    protected String extendClassName; // this is parent class's name
    protected MClass extendClass;

    public MClass(String _name, int _col, int _row, String _extendClassName) {
        super(_name, _name,  _col, _row);

        extendClassName = _extendClassName; // if no extend, the _extendClassName is null
        extendClass = null;
    }

    public boolean setExtendClass(MClassList allClassList) {
        if (extendClassName == null) {
            return true;
        }
        else if (allClassList.getClass(extendClassName) == null) {
            return false;
        }
        else {
            extendClass = allClassList.getClass(extendClassName);
            return true;
        }
    }

    public String getExtendClassName(){
        return extendClassName;
    }

    public boolean insertMethod(MMethod method) {
        String name = method.getName();
        if (methodHashMap.containsKey(name)) {
            return false;
        }
        methodHashMap.put(name, method);
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

    public MVar getVar(String name) {
        if (! varHashMap.containsKey(name)) {
            return null;
        }
        return varHashMap.get(name);
    }

    public MMethod getMethod(String _key) {
        if (! methodHashMap.containsKey(_key)) {
            return null;
        }
        return methodHashMap.get(_key);
    }

}
