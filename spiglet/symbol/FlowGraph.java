package spiglet.symbol;

import java.util.HashMap;
import java.util.HashSet;

public class FlowGraph {
    public String graphName;
    public int flowNodeId;
    public HashMap<Integer, FlowNode> nodeId2flowNode = new HashMap<>();
    public HashMap<String, Integer> label2nodeId = new HashMap<>();
    public HashMap<Integer, String> pendingEdges = new HashMap<>();

    public IntervalAnalysis linearChecker;

    public int paraNum = 0;
    public int stackNum = 0;
    public int maxParaNum = 0;

    public FlowGraph(String _name) {
        graphName = _name;
        flowNodeId = 0;

        FlowNode entryNode = new FlowNode(0, this);
        nodeId2flowNode.put(0, entryNode);

        paraNum = 0;
    }

    public FlowGraph(String _name, Integer _paraNum) {
        graphName = _name;
        flowNodeId = 0;

        FlowNode entryNode = new FlowNode(0, this);
        // in spiglet, when a function is called, the parameters will be in the first 20 temps.
        for (int i = 0; i < _paraNum; i++) {
            entryNode.addDefTemp(i);
        }
        nodeId2flowNode.put(0, entryNode);

        paraNum = _paraNum;
    }

    public FlowNode getEntryNode() {
        return nodeId2flowNode.get(0);
    }

    public FlowNode getNextNode(FlowNode _node) {
        int curNodeId = _node.nodeId;
        return getFlowNode(curNodeId + 1);
    }

    public void addExitNode() {
        flowNodeId++;
        FlowNode exitNode = new FlowNode(flowNodeId, this);
        nodeId2flowNode.put(flowNodeId, exitNode);
        addEdge(flowNodeId - 1, flowNodeId);
    }

    public void addExitNode(int _retTemp) {
        flowNodeId++;
        FlowNode exitNode = new FlowNode(flowNodeId, this);
        exitNode.addUseTemp(_retTemp);
        nodeId2flowNode.put(flowNodeId, exitNode);
        addEdge(flowNodeId - 1, flowNodeId);
    }

    public void finishGraph() {
        for (Integer _from : pendingEdges.keySet()) {
            String _toLabel = pendingEdges.get(_from);
            Integer _to = label2nodeId.get(_toLabel);
            addEdge(_from, _to);
        }

        int cnt = 0;
        while (true) {
            boolean change = updateInOutSet();
            if (!change) {
                break;
            }
//            System.out.println(cnt++);
        }

        linearChecker = new IntervalAnalysis();
        linearChecker.setTempStartEnd(this);
        linearChecker.setRegSelect(this);

//        printNodeRegSelect();
//        System.out.println("xxx");

        // calculate the three numbers for a procedure.
        int stackNumBuffer = 0;
        if (paraNum > 4) {
            stackNumBuffer += (paraNum - 4);
        }
        stackNumBuffer += linearChecker.maxStackPos;
        if (!graphName.equals("MAIN")) {
            stackNumBuffer += linearChecker.useSaveRegs.size();
        }

        stackNum = stackNumBuffer;
    }

    public HashSet<Integer> AND(HashSet<Integer> x, HashSet<Integer> y) {
        HashSet<Integer> ret = new HashSet<>(x);
        ret.retainAll(y);
        return ret;
    }

    public HashSet<Integer> OR(HashSet<Integer> x, HashSet<Integer> y) {
        HashSet<Integer> ret = new HashSet<>(x);
        ret.addAll(y);
        return ret;
    }

    public HashSet<Integer> MINUS(HashSet<Integer> x, HashSet<Integer> y) {
        HashSet<Integer> ret = new HashSet<>(x);
        ret.removeAll(y);
        return ret;
    }

    public boolean updateInOutSet() {
        boolean change = false;
        for (FlowNode node : nodeId2flowNode.values()) {
            HashSet<Integer> _in = OR(node.useTempHashSet, MINUS(node.outTempHashSet, node.defTempHashSet));
            HashSet<Integer> _out = new HashSet<>();
            for (FlowNode nextNode : node.nextNodeHashSet) {
                _out = OR(_out, nextNode.inTempHashSet);
            }

            if ((!node.inTempHashSet.equals(_in)) || (!node.outTempHashSet.equals(_out))) {
                change = true;
            }

            node.inTempHashSet = _in;
            node.outTempHashSet = _out;
        }
        return change;
    }

    public void addPendingEdge(Integer _nodeId, String _lineLabel) {
        pendingEdges.put(_nodeId, _lineLabel);
    }


    public int newFlowNode(String _lineLabel, boolean _seqEdgeFlag) {
        flowNodeId++;
        FlowNode _node = new FlowNode(flowNodeId, this);
        nodeId2flowNode.put(flowNodeId, _node);

        if (_lineLabel != null) {
            label2nodeId.put(_lineLabel, flowNodeId);
        }
        if (_seqEdgeFlag) {
            addSeqEdge(flowNodeId);
        }

        return flowNodeId;
    }

    public FlowNode getFlowNode(Integer _nodeId) {
        return nodeId2flowNode.get(_nodeId);
    }

    public void addEdge(Integer _from, Integer _to) {
        FlowNode fromNode = getFlowNode(_from);
        FlowNode toNode = getFlowNode(_to);

        fromNode.addNextNode(toNode);
        toNode.addPreNode(fromNode);
    }

    public void addSeqEdge(Integer _cur) {
        Integer pre = _cur - 1;
        addEdge(pre, _cur);
    }

    public void printNodeRegSelect() {
        for (Integer nodeId : nodeId2flowNode.keySet()) {
            FlowNode node = getFlowNode(nodeId);

            System.out.println("Node " + nodeId.toString() + ":");

            System.out.print("[");
            for (String reg : node.regSelect.regStackMove.keySet()) {
                System.out.printf("%s -> %s; ", reg, node.regSelect.regStackMove.get(reg));
            }
            System.out.println("]");

            for (Integer temp : node.regSelect.curTempId2Reg.keySet()) {
                System.out.println("\t" + temp + " -> " + node.regSelect.curTempId2Reg.get(temp));
            }
            for (Integer temp : node.regSelect.curTempId2Stack.keySet()) {
                System.out.println("\t" + temp + " -> " + node.regSelect.curTempId2Stack.get(temp));
            }

            System.out.println("===");
        }
    }


}
