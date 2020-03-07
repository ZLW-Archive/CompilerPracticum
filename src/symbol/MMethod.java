package symbol;

import java.util.HashMap;

public class MMethod extends MIdentifier {

    protected MClass parent;
    protected String returnType;
    protected HashMap<String, MVar> localVarHashMap = new HashMap<String, MVar>();
    protected HashMap<String, MVar> formalParaHashMap = new HashMap<String, MVar>();

    public MMethod(String _name, int _col, int _row, MClass _parent, String _returnType) {
        super("Method", _name, _col, _row);
        parent = _parent;
        returnType = _returnType;
    }
}
