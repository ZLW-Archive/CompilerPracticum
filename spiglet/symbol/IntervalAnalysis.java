package symbol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class IntervalAnalysis {
    public static String[] allRegNames = {"s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
                                          // "t0", "t1", "t2",
                                          "t3", "t4", "t5", "t6", "t7", "t8", "t9"};

    public Integer curNodePointer;
    public HashMap<String, Integer> curReg2tempId = new HashMap<>();
    public HashMap<Integer, String> curTempId2Reg = new HashMap<>();

    public Integer curStackPos;
    public Vector<Integer> stackMiddleAvailable = new Vector<>();
    public HashMap<Integer, String> curTempId2Stack = new HashMap<>();

    public HashMap<Integer, Integer> tempStartNode = new HashMap<>();
    public HashMap<Integer, Integer> tempEndNode = new HashMap<>();

    public HashMap<Integer, Vector<Integer>> nodeIdStartTemps = new HashMap<>();
    public HashMap<Integer, Vector<Integer>> nodeIdEndTemps = new HashMap<>();


    public IntervalAnalysis() {
        curNodePointer = 0;
        curStackPos = -1;
        for (String s : allRegNames) {
            curReg2tempId.put(s, -1);
        }
    }

    public void setTempStartEnd(FlowGraph _graph) {
        for (Integer nodeId : _graph.nodeId2flowNode.keySet()) {
            FlowNode node = _graph.getFlowNode(nodeId);
            for (Integer i : node.inTempHashSet) {
                if (! tempStartNode.containsKey(i)) {
                    tempStartNode.put(i, nodeId);
                }
                tempEndNode.put(i, nodeId);
            }
            for (Integer i : node.outTempHashSet) {
                if (! tempStartNode.containsKey(i)) {
                    tempStartNode.put(i, nodeId);
                }
                tempEndNode.put(i, nodeId);
            }
        }

        for (Integer temp : tempStartNode.keySet()) {
            Integer nodeId = tempStartNode.get(temp);
            if (! nodeIdStartTemps.containsKey(nodeId)) {
                nodeIdStartTemps.put(nodeId, new Vector<>());
            }
            nodeIdStartTemps.get(nodeId).add(temp);
        }
        for (Integer temp : tempEndNode.keySet()) {
            Integer nodeId = tempEndNode.get(temp);
            if (! nodeIdEndTemps.containsKey(nodeId)) {
                nodeIdEndTemps.put(nodeId, new Vector<>());
            }
            nodeIdEndTemps.get(nodeId).add(temp);
        }
    }

    public void assignStack(int temp) {
        if (stackMiddleAvailable.size() != 0) {
            Integer stackPosIndex = stackMiddleAvailable.elementAt(0);
            stackMiddleAvailable.removeElementAt(0);
            String stackPos = "X" + stackPosIndex.toString();
            curTempId2Stack.put(temp, stackPos);
        } else {
            curStackPos++;
            String stackPos = "X" + curStackPos.toString();
            curTempId2Stack.put(temp, stackPos);
        }
    }

    public void assignReg(int temp, String reg) {
        curTempId2Reg.put(temp, reg);
        curReg2tempId.put(reg, temp);
    }


    public String getNewReg(int curTemp) {
        String reg = null;
        for (String s : allRegNames) {
            Integer curAssignTemp = curReg2tempId.get(s);
            if (curAssignTemp == -1) {
                reg = s;
                break;
            }
        }
        if (reg != null) {
            return reg;
        } else {
            int lastEndTemp = getLastEndTemp(curTemp);
            if (lastEndTemp == curTemp) {
                return null;
            } else {
                reg = curTempId2Reg.get(lastEndTemp);
                curTempId2Reg.remove(lastEndTemp);
                curReg2tempId.put(reg, -1);
//                for (String s : curReg2tempId.keySet()) {
//                    if (curReg2tempId.get(s).equals(lastEndTemp)) {
//                        curReg2tempId.put(s, -1);
//                    }
//                }
                assignStack(lastEndTemp);
                return reg;
            }
        }
    }

    public int getLastEndTemp(int curTemp) {
        int ret = curTemp;
        for (int i : curTempId2Reg.keySet()) {
            if (tempEndNode.get(i) > tempEndNode.get(ret)) {
                ret = i;
            }
        }
        return ret;
    }

    public void setRegSelect(FlowGraph _graph) {
        stackMiddleAvailable.clear();

        for (Integer nodeId : _graph.nodeId2flowNode.keySet()) {
            FlowNode node = _graph.getFlowNode(nodeId);

            if (nodeIdStartTemps.containsKey(nodeId)) {
                for (Integer i : nodeIdStartTemps.get(nodeId)) {
                    String newReg = getNewReg(i);
                    if (newReg != null) {
                        assignReg(i, newReg);
                    } else {
                        assignStack(i);
                    }
                }
            }

            node.copyRegState(this);

            if (nodeIdEndTemps.containsKey(nodeId)) {
                for (Integer i : nodeIdEndTemps.get(nodeId)) {
                    if (curTempId2Reg.containsKey(i)) {
                        curReg2tempId.put(curTempId2Reg.get(i), -1);
                        curTempId2Reg.remove(i);
                    }
                    else if (curTempId2Stack.containsKey(i)) {
                        String stackPos = curTempId2Stack.get(i);
                        curTempId2Stack.remove(i);
                        int stackPosIndex = Integer.parseInt(stackPos.substring(1));
                        if (stackPosIndex == curStackPos) {
                            curStackPos --;
                        } else {
                            stackMiddleAvailable.add(stackPosIndex);
                        }
                    }
                }
            }
//            for (String reg : allRegNames) {
//                Integer temp = curReg2tempId.get(reg);
//                if (temp != -1) {
//                    if (! reg.equals(curTempId2Reg.get(temp))) {
//                        System.out.println("Wrong!");
//                    }
//                }
//            }
        }
    }
}
