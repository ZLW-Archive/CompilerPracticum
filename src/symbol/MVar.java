package symbol;

public class MVar extends MIdentifier {

    protected String parent; // who declare this var
    protected String realType; // type is the type when declaration, realType is the "real" type

    public MVar(String _type, String _name, int _col, int _row, String _parent, String _realType) {
        super(_type, _name, _col, _row);

        parent = _parent;
        realType = _realType;
    }

}
