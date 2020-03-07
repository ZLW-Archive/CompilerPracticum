package symbol;

import java.util.HashMap;

public class MClassList extends MType {
    protected  HashMap<String, MClass> classHashMap = new HashMap<String, MClass>();

    public MClassList() {
        super("ClassList");
    }

    public boolean insert(String _key, MClass _value) {
        if (classHashMap.containsKey(_key)) {
            return false;
        }
        classHashMap.put(_key, _value);
        return true;
    }

    public MClass get(String _key) {
        if (! classHashMap.containsKey(_key)) {
            return null;
        }
        return classHashMap.get(_key);
    }



}
