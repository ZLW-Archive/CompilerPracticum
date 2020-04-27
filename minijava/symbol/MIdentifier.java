package symbol;

public class MIdentifier extends MType {

    protected String name;
    protected int col;
    protected int row;

    public MIdentifier(String _type, String _name, int _col, int _row) {
        super(_type);
        name = _name;
        col = _col;
        row = _row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int _col) {
        col = _col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int _row) {
        row = _row;
    }

    public String getName() {
        return name;
    }

}
