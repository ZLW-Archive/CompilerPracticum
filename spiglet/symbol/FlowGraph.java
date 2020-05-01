package symbol;

import java.util.HashMap;
import java.util.Vector;

public class FlowGraph {
    public String graphName;
    public int flowNodeId;
    public HashMap<Integer, FlowNode> nodeId2flowNode = new HashMap<>();
    public HashMap<String, Integer> label2nodeId = new HashMap<>();
    public HashMap<Integer, String> pendingEdges = new HashMap<>();

    public FlowGraph(String _name) {
        graphName = _name;
        flowNodeId = 0;

        FlowNode entryNode = new FlowNode(0, this);
        nodeId2flowNode.put(0, entryNode);
    }

    public void addEndNode() {
        flowNodeId++;
        FlowNode exitNode = new FlowNode(flowNodeId, this);
        nodeId2flowNode.put(flowNodeId, exitNode);
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

}
