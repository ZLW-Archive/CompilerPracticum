package visitor;

import symbol.FlowGraph;
import symbol.FlowNode;
import symbol.Printer;
import syntaxtree.*;

import java.util.Enumeration;
import java.util.HashMap;

public class ToKangaVisitor extends GJDepthFirst<Object, Object>  {
    private Printer printer = new Printer();

    public HashMap<String, FlowGraph> label2flowGraph;
    public FlowGraph curFlowGraph;
    public FlowNode curFlowNode;

    public boolean lineLabelFlag;

    public ToKangaVisitor(HashMap<String, FlowGraph> _label2flowGraph) {
        label2flowGraph = new HashMap<>(_label2flowGraph);
    }

    //
    // Auto class visitors--probably don't need to be overridden.
    //
    public Object visit(NodeList n, Object argu) {
        Object _ret = null;
        int _count = 0;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this, argu);
            _count++;
        }
        return _ret;
    }

    public Object visit(NodeListOptional n, Object argu) {
        if (n.present()) {
            Object _ret = null;
            int _count = 0;
            for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                e.nextElement().accept(this, argu);
                _count++;
            }
            return _ret;
        } else
            return null;
    }

    public Object visit(NodeOptional n, Object argu) {
        if (n.present())
            return n.node.accept(this, argu);
        else
            return null;
    }

    public Object visit(NodeSequence n, Object argu) {
        if (n.nodes.size() == 2) {
            Node first = n.nodes.get(0);
            Node second = n.nodes.get(1);
            if (first instanceof NodeOptional && second instanceof Stmt) {
                if (((NodeOptional)first).node != null) {
                    lineLabelFlag = true;
                    first.accept(this, argu);
                    lineLabelFlag = false;
                }
                else {
                    printer.print("    ");
                }
                second.accept(this, argu);
                return null;
            }
        }

        Object _ret = null;
        int _count = 0;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this, argu);
            _count++;
        }
        return _ret;
    }

    public Object visit(NodeToken n, Object argu) {
        return null;
    }

    //
    // User-generated visitor methods below
    //

    /**
     * f0 -> "MAIN"
     * f1 -> StmtList()
     * f2 -> "END"
     * f3 -> ( Procedure() )*
     * f4 -> <EOF>
     */
    public Object visit(Goal n, Object argu) {
        Object _ret = null;
        n.f0.accept(this, argu);
        printer.print("MAIN\n");
        //TODO: the three factors for a method
        curFlowGraph = label2flowGraph.get("MAIN");
        curFlowNode = curFlowGraph.getEntryNode();

        n.f1.accept(this, argu);
        n.f2.accept(this, argu);

        printer.print("END\n");

//        n.f3.accept(this, argu);
//        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> ( ( Label() )? Stmt() )*
     */
    public Object visit(StmtList n, Object argu) {
        Object _ret = null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Label()
     * f1 -> "["
     * f2 -> IntegerLiteral()
     * f3 -> "]"
     * f4 -> StmtExp()
     */
    public Object visit(Procedure n, Object argu) {
        Object _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> NoOpStmt()
     * | ErrorStmt()
     * | CJumpStmt()
     * | JumpStmt()
     * | HStoreStmt()
     * | HLoadStmt()
     * | MoveStmt()
     * | PrintStmt()
     */
    public Object visit(Stmt n, Object argu) {
        Object _ret = null;
        curFlowNode = curFlowGraph.getNextNode(curFlowNode);
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "NOOP"
     */
    public Object visit(NoOpStmt n, Object argu) {
        Object _ret = null;
        n.f0.accept(this, argu);
        printer.print("NOOP\n");
        return _ret;
    }

    /**
     * f0 -> "ERROR"
     */
    public Object visit(ErrorStmt n, Object argu) {
        Object _ret = null;
        n.f0.accept(this, argu);
        printer.print("ERROR\n");
        return _ret;
    }

    /**
     * f0 -> "CJUMP"
     * f1 -> Temp()
     * f2 -> Label()
     */
    public Object visit(CJumpStmt n, Object argu) {
        Object _ret = null;
        n.f0.accept(this, argu);
        Integer tempId = (Integer) n.f1.accept(this, argu);
        String label = (String) n.f2.accept(this, argu);

        String reg = curFlowNode.regSelect.tempId2Pos(tempId);
        printer.print("CJUMP %s %s\n", reg, label);

        return _ret;
    }

    /**
     * f0 -> "JUMP"
     * f1 -> Label()
     */
    public Object visit(JumpStmt n, Object argu) {
        Object _ret = null;
        n.f0.accept(this, argu);
        String label = (String) n.f1.accept(this, argu);
        printer.print("JUMP %s\n", label);
        return _ret;
    }

    /**
     * f0 -> "HSTORE"
     * f1 -> Temp()
     * f2 -> IntegerLiteral()
     * f3 -> Temp()
     */
    public Object visit(HStoreStmt n, Object argu) {
        Object _ret = null;
        n.f0.accept(this, argu);
        Integer tempId_1 = (Integer) n.f1.accept(this, argu);
        Integer integer = (Integer) n.f2.accept(this, argu);
        Integer tempId_2 = (Integer) n.f3.accept(this, argu);

        String reg_1 = curFlowNode.regSelect.tempId2Pos(tempId_1);
        String reg_2 = curFlowNode.regSelect.tempId2Pos(tempId_2);

        printer.print("HSTORE %s %d %s\n", reg_1, integer, reg_2);

        return _ret;
    }

    /**
     * f0 -> "HLOAD"
     * f1 -> Temp()
     * f2 -> Temp()
     * f3 -> IntegerLiteral()
     */
    public Object visit(HLoadStmt n, Object argu) {
        Object _ret = null;
        n.f0.accept(this, argu);
        Integer tempId_1 = (Integer) n.f1.accept(this, argu);
        Integer tempId_2 = (Integer) n.f2.accept(this, argu);
        Integer integer = (Integer) n.f3.accept(this, argu);

        String reg_1 = curFlowNode.regSelect.tempId2Pos(tempId_1);
        String reg_2 = curFlowNode.regSelect.tempId2Pos(tempId_2);

        printer.print("HLOAD %s %s %d\n", reg_1, reg_2, integer);

        return _ret;
    }

    /**
     * f0 -> "MOVE"
     * f1 -> Temp()
     * f2 -> Exp()
     */
    public Object visit(MoveStmt n, Object argu) {

        //TODO: two visits for exp: 1. prepare for the reg 2. print contents

        Object _ret = null;
        n.f0.accept(this, argu);
        Integer tempId = (Integer) n.f1.accept(this, argu);
        String reg = curFlowNode.regSelect.tempId2Pos(tempId);
        printer.print("MOVE %s EXP()\n", reg);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "PRINT"
     * f1 -> SimpleExp()
     */
    public Object visit(PrintStmt n, Object argu) {
        Object _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);

        printer.print("PRINT EXP()\n");
        return _ret;
    }

    /**
     * f0 -> Call()
     * | HAllocate()
     * | BinOp()
     * | SimpleExp()
     */
    public Object visit(Exp n, Object argu) {
        Object _ret = null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "BEGIN"
     * f1 -> StmtList()
     * f2 -> "RETURN"
     * f3 -> SimpleExp()
     * f4 -> "END"
     */
    public Object visit(StmtExp n, Object argu) {
        Object _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "CALL"
     * f1 -> SimpleExp()
     * f2 -> "("
     * f3 -> ( Temp() )*
     * f4 -> ")"
     */
    public Object visit(Call n, Object argu) {
        Object _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "HALLOCATE"
     * f1 -> SimpleExp()
     */
    public Object visit(HAllocate n, Object argu) {
        Object _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Operator()
     * f1 -> Temp()
     * f2 -> SimpleExp()
     */
    public Object visit(BinOp n, Object argu) {
        Object _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "LT"
     * | "PLUS"
     * | "MINUS"
     * | "TIMES"
     */
    public Object visit(Operator n, Object argu) {
        Object _ret = null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Temp()
     * | IntegerLiteral()
     * | Label()
     */
    public Object visit(SimpleExp n, Object argu) {
        Object _ret = null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "TEMP"
     * f1 -> IntegerLiteral()
     */
    public Object visit(Temp n, Object argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return Integer.parseInt(n.f1.f0.tokenImage);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public Object visit(IntegerLiteral n, Object argu) {
        n.f0.accept(this, argu);
        return Integer.parseInt(n.f0.tokenImage);
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public Object visit(Label n, Object argu) {
        n.f0.accept(this, argu);
        String label = n.f0.tokenImage;
        if (lineLabelFlag) {
            printer.print(label);
            for (int i = 0; i < 4 - label.length(); i++) {
                printer.print(" ");
            }
        }
        return label;
    }
}