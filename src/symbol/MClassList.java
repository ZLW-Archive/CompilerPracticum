package symbol;

import java.util.Collection;
import java.util.HashMap;

public class MClassList extends MType {
    protected  HashMap<String, MClass> classHashMap = new HashMap<String, MClass>();

    public MClassList() {
        super("ClassList");
    }

    public boolean insertClass(MClass _value) {
        String _key = _value.getName();
        if (classHashMap.containsKey(_key)) {
            return false;
        }
        classHashMap.put(_key, _value);
        return true;
    }

    public MClass getClass(String _key) {
        if (! classHashMap.containsKey(_key)) {
            return null;
        }
        return classHashMap.get(_key);
    }

    public boolean setAllExtendClass() {
        boolean _ret = true;
        Collection<MClass> classCollection = classHashMap.values();
        for (MClass x: classCollection ) {
            _ret &= x.setExtendClass(this);
        }
        return _ret;
    }

}
