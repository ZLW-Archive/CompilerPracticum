package symbol;

import java.util.HashMap;

public class MClass extends MIdentifier {

    protected HashMap<String, MVar> varHashMap = new HashMap<String, MVar>();
    protected HashMap<String, MMethod> methodHashMap = new HashMap<String, MMethod>();

    protected String extendClassName; // this is parent class's name

    public MClass(String _name, int _col, int _row, String _extendClassName) {
        super("Class", _name,  _col, _row);

        extendClassName = _extendClassName; // if no extend, the _extendClassName is null
    }

}
