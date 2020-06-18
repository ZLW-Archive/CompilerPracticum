package minijava.symbol;

import java.util.HashMap;
import java.util.Vector;
import minijava.minijava.*;

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

    public HashMap<String, Pair<String, Integer>> getMethodTable()
    {
        HashMap<String, Pair<String, Integer>> ret;
        if (extendClass == null)
            ret = new HashMap<>();
        else
            ret = extendClass.getMethodTable();
        int cnt = ret.size()*4;
        for (String x: methodHashMap.keySet())
        {
            if (ret.containsKey(x))
            {
                Pair<String, Integer> t = ret.get(x);
                ret.replace(x, t, new Pair<>(getName()+"_"+x, t.getSecond()));
            }
            else
            {
                ret.put(x, new Pair<>(getName()+"_"+x, cnt));
                cnt += 4;
            }
        }
        return  ret;
    }

    public HashMap<String, Integer> getVarTable()
    {
        HashMap<String, Integer> ret;
        if (extendClass == null)
            ret = new HashMap<>();
        else
            ret = extendClass.getVarTable();
        int cnt = ret.size()*4 + 4;
        for (String x: varHashMap.keySet())
        {
            ret.put(name+"."+x, cnt);
            cnt += 4;
        }
        return ret;
    }

    public void printSymbolList(int intend)
    {
        for (int i = 0;i < intend; ++i)
            System.out.print("| ");

        System.out.print("Mclass " + name);
        if (extendClassName != null)
            System.out.print(" extends " + extendClassName);
        System.out.print("\n");

        for (MVar x: varHashMap.values())
            x.printSymbolList(intend + 1);

        for (MMethod x:methodHashMap.values())
            x.printSymbolList(intend + 1);

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
        //  BUG!!! We cannot say if ancestors have the same-name method, the cur method cannot be inserted, since
        //  the method can be overwrite. BUT this method is only used before we set all the extend relation,
        //  so the extendClass here is always null.
        //  FIX FINISH !
//        if (methodHashMap.containsKey(name) || (extendClass != null && extendClass.getMethod(name) != null)) {
//            return false;
//        }
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
        if (varHashMap.containsKey(name)) {
            return varHashMap.get(name);
        }
        else if (extendClass != null && extendClass.getVar(name) != null) {
            return extendClass.getVar(name);
        }
        return null;
    }

    public MMethod getMethod(String _key) {
        if (methodHashMap.containsKey(_key)) {
            return methodHashMap.get(_key);
        }
        else if (extendClass != null && extendClass.getMethod(_key) != null) {
            return extendClass.getMethod(_key);
        }
        return null;
     }

     public boolean findOverload(MClassList allClassList) {
        if (extendClass == null) {
            return false;
        }
        for (String methodName: methodHashMap.keySet()) {
            MMethod curMethod = methodHashMap.get(methodName);
            if (extendClass.getMethod(methodName) != null) {
                MMethod extendClassMethod = extendClass.getMethod(methodName);

                if (! curMethod.getReturnType().getType().equals(extendClassMethod.getReturnType().getType())) {
//                if (! (allClassList.checkExtendAssign(extendClassMethod.getReturnType().getType(), curMethod.getReturnType().getType()))) {
                    return true;
                }

                Vector<String> curTypeVector = curMethod.formalParaTypeVector;
                Vector<String> extendTypeVector = extendClassMethod.formalParaTypeVector;

                if (curTypeVector.size() != extendTypeVector.size()) {
                    return true;
                }
                else {
                    int formalParaNum = curTypeVector.size();
                    for (int i = 0; i < formalParaNum; i ++) {
                        if (!curTypeVector.elementAt(i).equals(extendTypeVector.elementAt(i))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
     }

}
