package spiglet.symbol;

import java.util.HashMap;

public class RegSelect {

    public HashMap<String, String> regStackMove;
    public HashMap<Integer, String> curTempId2Reg;
    public HashMap<Integer, String> curTempId2Stack;

    public RegSelect() {
        regStackMove = new HashMap<>();
    }

    public RegSelect(IntervalAnalysis intervalAnalysis) {
        curTempId2Stack = new HashMap<>(intervalAnalysis.curTempId2Stack);
        curTempId2Reg = new HashMap<>(intervalAnalysis.curTempId2Reg);
    }

    public void init(IntervalAnalysis intervalAnalysis) {
        curTempId2Stack = new HashMap<>(intervalAnalysis.curTempId2Stack);
        curTempId2Reg = new HashMap<>(intervalAnalysis.curTempId2Reg);
    }

    public HashMap<Integer, String> generateAllMap () {
        HashMap<Integer, String> ret = new HashMap<>();
        for (Integer key : curTempId2Reg.keySet()) {
            String val = curTempId2Reg.get(key);
            ret.put(key, val);
        }
        for (Integer key : curTempId2Stack.keySet()) {
            String val = curTempId2Stack.get(key);
            ret.put(key, val);
        }
        return ret;
    }

    public HashMap<String, Integer> generateReverseMap () {
        HashMap<String, Integer> ret = new HashMap<>();
        for (Integer key : curTempId2Reg.keySet()) {
            String val = curTempId2Reg.get(key);
            ret.put(val, key);
        }
        for (Integer key : curTempId2Stack.keySet()) {
            String val = curTempId2Stack.get(key);
            ret.put(val, key);
        }
        return ret;
    }

    public String tempId2Pos(Integer _tempId) {
        String _ret = null;
        if (curTempId2Reg.containsKey(_tempId)) {
            _ret = curTempId2Reg.get(_tempId);
        } else if (curTempId2Stack.containsKey(_tempId)) {
            _ret = curTempId2Stack.get(_tempId);
        }
        return _ret;
    }

}
