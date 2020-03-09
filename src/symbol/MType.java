package symbol;

import java.util.HashMap;

public class MType {
//    type: Class, Method ...
    protected String type;

//    private HashMap<String, Object> specialReturn = new HashMap<>();

    public MType(String _type) {
        type = _type;
    }

    public String getType() {
        return type;
    }

    public void setType(String _type) {
        type = _type;
    }

//    public void insertSpecialReturn(String _key, Object _val) {
//        specialReturn.put(_key, _val);
//    }
//
//    public Object getSpecialReturn(String _key) {
//        if (! specialReturn.containsKey(_key)) {
//            return null;
//        }
//        return specialReturn.get(_key);
//    }

}
