package symbol;

import java.util.HashMap;

public class RegSelect {

    public HashMap<Integer, String> curTempId2Reg;
    public HashMap<Integer, String> curTempId2Stack;

    public RegSelect (IntervalAnalysis intervalAnalysis) {
        curTempId2Stack = new HashMap<>(intervalAnalysis.curTempId2Stack);
        curTempId2Reg = new HashMap<>(intervalAnalysis.curTempId2Reg);
    }

    public String tempId2Pos (Integer _tempId) {
        String _ret = null;
        if (curTempId2Reg.containsKey(_tempId)) {
            _ret = curTempId2Reg.get(_tempId);
        }
        else if (curTempId2Stack.containsKey(_tempId)) {
            _ret = curTempId2Stack.get(_tempId);
        }
        return _ret;
    }

}
