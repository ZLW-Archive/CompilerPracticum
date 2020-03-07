package symbol;

public class MVar extends MIdentifier {

    protected MType owner; // who declare this var
    protected String realType; // type is the type when declaration, realType is the "real" type (type = new realType)
    protected boolean init;

    public MVar(String _type, String _name, int _col, int _row, MType _owner, boolean _init) {
        super(_type, _name, _col, _row);

        owner = _owner;
        init = _init;

        if (_init) {
            realType = _type;
        }
        else {
            realType = null;
        }
    }

    public void init() {
        init = true;
    }

}
