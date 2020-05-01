package symbol;

import java.util.HashSet;

public class FlowNode {
    public int nodeId;

    public HashSet<Integer> inTempHashSet = new HashSet<>();
    public HashSet<Integer> outTempHashSet = new HashSet<>();

    public HashSet<Integer> defTempHashSet = new HashSet<>();
    public HashSet<Integer> useTempHashSet = new HashSet<>();

    public FlowGraph ownerFlowGraph;
    public HashSet<FlowNode> preNodeHashSet = new HashSet<>();
    public HashSet<FlowNode> nextNodeHashSet = new HashSet<>();

    public FlowNode(int _nodeId, FlowGraph _owner) {
        nodeId = _nodeId;
        ownerFlowGraph = _owner;
    }

    public void addPreNode(FlowNode _node) {
        preNodeHashSet.add(_node);
    }

    public void addNextNode(FlowNode _node) {
        nextNodeHashSet.add(_node);
    }

    public void addDefTemp(Integer _temp) {
        defTempHashSet.add(_temp);
    }

    public void addUseTemp(Integer _temp) {
        useTempHashSet.add(_temp);
    }
}
