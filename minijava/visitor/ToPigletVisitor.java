package minijava.visitor;

import minijava.minijava.*;
import minijava.symbol.*;
import minijava.syntaxtree.*;
import minijava.minijava.ErrorPrint;

import minijava.piglet.*;

import java.util.HashMap;
import java.util.Vector;

public class ToPigletVisitor extends GJDepthFirst<PigletRet,PigletLabels>
{
    //The symbol List
    MClassList symbolTable;

    //Counters for variables and label assignment
    Integer temp;
    Integer label;

    //hash maps for method list and varlist of a class
    HashMap<String, Pair<Integer, HashMap<String, Pair<String, Integer>>>> methodList = new HashMap<>();
    HashMap<String, HashMap<String, Integer>> varLists = new HashMap<>();


    /* TEMP 0 --- THIS POINTER
     * TEMP 1 --- GLOBAL CLASS LIST
     * TEMP 2 - 16 PARAMETERS
     * TEMP 17 --- EXTENDED PARAM POINTER
     * TEMP 18 --- NOT USED
     * TEMP 19 --- NOT USED
     */
    static final int EXTENDED_PARAM_POINTER = 17;
    static final int GLOBAL_CLASS_LIST = 1;
    static final int THIS_POINTER = 0;

    public ToPigletVisitor(MClassList _sl)
    {
        symbolTable = _sl;
        temp = 21;
        label = 0;
    }

    //Build global class list
    public void printMethodTable()
    {
        System.out.print("Initial_methodlist [ 1 ]\n");
        System.out.print("BEGIN\n");
        Vector<String> v = symbolTable.getAllClassNames();
        int length = v.size();

        //Here we allocate the space for the global class list and then build each class
        System.out.printf("    MOVE TEMP %d HALLOCATE %d\n", GLOBAL_CLASS_LIST, length*4);
        for (int i = 0; i < v.size(); ++i)
        {
            String s = v.get(i);
            buildList(s, i);
        }
        System.out.printf("    RETURN TEMP %d\n", GLOBAL_CLASS_LIST);
        System.out.print("END\n");
    }

    //build the method list of a class
    public void buildList(String x, int num)
    {
        MClass mc = symbolTable.getClass(x);
        HashMap<String, Pair<String, Integer>> methodlist = mc.getMethodTable();

        //first allocate some space for the method table
        int newtemp = newTemp();
        System.out.printf("    MOVE TEMP %d HALLOCATE %d\n", newtemp, methodlist.size()*4);

        //fill in the table, here subclass methods overrides
        for (String s: methodlist.keySet())
            System.out.printf("    HSTORE TEMP %d %d %s\n", newtemp, methodlist.get(s).getSecond(), methodlist.get(s).getFirst());

        //put the list into the global method lists
        System.out.printf("    HSTORE TEMP %d %d TEMP %d\n", GLOBAL_CLASS_LIST, num * 4, newtemp);
        methodList.put(x, new Pair<>(num, methodlist));
        HashMap<String, Integer> sl = mc.getVarTable();
        varLists.put(x, sl);
    }

    /* All VAR are named as CLASSNAME_VARNAME
     * here we solve the var in the methods, find the bias in the Vtable
     */
    public int getVar(String s, HashMap<String, Integer> list, MClass mc)
    {
        String className = mc.getName();
        while (!list.containsKey(className+"."+s))
        {
            mc = symbolTable.getClass(mc.getExtendClassName());
            if (mc == null)
                return -1;
            className = mc.getName();
        }
        return list.get(className+"."+s);
    }


    // to assign a new label
    public String newLabel()
    {
        label++;
        return "L" + Integer.toString(label);
    }

    //to assign a new label
    public int newTemp()
    {
        return temp++;
    }

    //print the intend for the output
    public void PrintIntend(int intend, String s)
    {
        int length = s.length();
        if (length + 1 < intend)
        {
            System.out.print(s);
            for (int i = 0;i<intend-length;++i)
                System.out.print(' ');
        }
        else
        {
            System.out.print(s+"\n");
            for (int i=0;i<intend;++i)
                System.out.print(' ');
        }
    }

    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    //create the main method, first initialize the global method table, then invoke the original main method
    public PigletRet visit(Goal n, PigletLabels argu)
    {
        PigletLabels p = new PigletLabels(0);
        n.f0.accept(this, p);
        n.f1.accept(this, p);
        n.f2.accept(this, p);
        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */

    public PigletRet visit(MainClass n, PigletLabels argu)
    {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);
        n.f13.accept(this, argu);
        n.f14.accept(this, argu);
        System.out.printf("MAIN\n");
        System.out.printf("    MOVE TEMP %d CALL Initial_methodlist (0)\n", GLOBAL_CLASS_LIST);
        System.out.printf("    MOVE TEMP 0 CALL %s_main (0 TEMP %d)\n", n.f1.f0.tokenImage, GLOBAL_CLASS_LIST);
        System.out.printf("END\n");
        printMethodTable();
        //get main method info from the symbol table
        String className = n.f1.f0.tokenImage;
        argu.mc = symbolTable.getClass(className);
        MMethod method = argu.mc.getMethod("main");

        //assign all vars in the main method and set up argu
        Pair<Integer, HashMap<String, Integer>> methodVarList = method.getVarList(temp);
        temp += method.varcnt();
        argu.varList = methodVarList.getSecond();
        argu.mm = argu.mc.getMethod("main");
        PigletLabels p = new PigletLabels(argu);
        p.intend += 4;

        System.out.printf("%s_main [2]\n", n.f1.f0.tokenImage);
        System.out.printf("BEGIN\n");

        n.f15.accept(this, p);
        n.f16.accept(this, p);
        n.f17.accept(this, p);
        PrintIntend(p.intend, "");

        //This is a fake main, so return is needed
        System.out.printf("RETURN 0\n");
        System.out.printf("END\n");
        argu.varList = null;
        return null;
    }

    /**
     * f0 -> ClassDeclaration()
     *       | ClassExtendsDeclaration()
     */
    public PigletRet visit(TypeDeclaration n, PigletLabels argu)
    {
        n.f0.accept(this, argu);
        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public PigletRet visit(ClassDeclaration n, PigletLabels argu)
    {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);

        //Start the translation for a new class, empty the argu
        String className = n.f1.f0.tokenImage;
        MClass mc = symbolTable.getClass(className);
        argu.mc = mc;
        argu.mm = null;
        argu.varList = null;

        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    public PigletRet visit(ClassExtendsDeclaration n, PigletLabels argu)
    {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        //Start the translation for a new class, empty the argu
        String className = n.f1.f0.tokenImage;
        MClass mc = symbolTable.getClass(className);
        argu.mc = mc;
        argu.mm = null;
        argu.varList = null;


        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        return  null;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    //public PigletRet visit(VarDeclaration n, PigletLabels argu) {
    //    n.f0.accept(this, argu);
    //    n.f1.accept(this, argu);
    //    n.f2.accept(this, argu);
    //}

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    public PigletRet visit(MethodDeclaration n, PigletLabels argu)
    {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);

        //Start to translate a new method
        String methodName = n.f2.f0.tokenImage;
        MMethod method = argu.mc.getMethod(methodName);

        //assign all vars and set up argu
        Pair<Integer, HashMap<String, Integer>> methodVarList = method.getVarList(temp);
        temp += method.varcnt();
        PigletLabels argu1 = new PigletLabels(argu);
        argu1.mm = method;
        argu1.varList = methodVarList.getSecond();
        argu1.intend += 4;

        //count parameters, if more than 15, squeeze them into a list
        int paraNum = methodVarList.getFirst() + 2;
        if (paraNum > 18)
            paraNum = 18;

        System.out.printf("%s_%s [ %d ]\n", argu1.mc.getName(), methodName, paraNum);
        System.out.printf("BEGIN\n");

        //translate the method body
        n.f8.accept(this, argu1);
        n.f9.accept(this, argu1);

        //translate return
        PigletRet p = n.f10.accept(this, argu1);
        PrintIntend(argu1.intend, "");
        System.out.printf("RETURN TEMP %d\n", p.result);
        System.out.printf("END\n");


        n.f11.accept(this, argu1);
        n.f12.accept(this, argu1);
        return null;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> ( FormalParameterRest() )*
     */
    //public void visit(FormalParameterList n, A argu) {
    //    n.f0.accept(this, argu);
    //    n.f1.accept(this, argu);
    //}

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    //public void visit(FormalParameter n, A argu) {
    //   n.f0.accept(this, argu);
    //    n.f1.accept(this, argu);
    //}

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    //public void visit(FormalParameterRest n, A argu) {
    //   n.f0.accept(this, argu);
    //   n.f1.accept(this, argu);
    //}

    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    //public void visit(Type n, A argu) {
    //    n.f0.accept(this, argu);
    //}

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    //public void visit(ArrayType n, A argu) {
    //    n.f0.accept(this, argu);
    //   n.f1.accept(this, argu);
    //    n.f2.accept(this, argu);
    //}

    /**
     * f0 -> "boolean"
     */
    //public void visit(BooleanType n, A argu) {
    //    n.f0.accept(this, argu);
    //}

    /**
     * f0 -> "int"
     */
    //public void visit(IntegerType n, A argu) {
    //    n.f0.accept(this, argu);
    //}

    /**
     * f0 -> Block()
     *       | AssignmentStatement()
     *       | ArrayAssignmentStatement()
     *       | IfStatement()
     *       | WhileStatement()
     *       | PrintStatement()
     */
    //public PigletRet visit(Statement n, PigletLabels argu) {
    //    n.f0.accept(this, argu);
    //    return null;
    //}

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    //public PigletRet visit(Block n, PigletLabels argu) {
    //    n.f0.accept(this, argu);
    //    n.f1.accept(this, argu);
    //   n.f2.accept(this, argu);
    //    return null;
    //}

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public PigletRet visit(AssignmentStatement n, PigletLabels argu)
    {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);

        //first translate the Expression
        PigletRet p = n.f2.accept(this, argu);
        String varName = n.f0.f0.tokenImage;

        //if it is found in the varlist directly, then it is a local one
        if (argu.varList.containsKey(varName))
        {
            int bias = argu.varList.get(varName);
            //if bias is smaller than 0 means it is in the extended parameter list
            if (bias > 0)
            {
                PrintIntend(argu.intend, "");
                System.out.printf("MOVE TEMP %d TEMP %d\n", bias, p.result);
            }
            else
            {
                PrintIntend(argu.intend, "");
                int newtemp = newTemp();
                System.out.printf("HSTORE TEMP %d %d TEMP %d \n", EXTENDED_PARAM_POINTER, -bias*4, p.result);
            }
        }
        //otherwise this belongs to the class
        else
        {
            PrintIntend(argu.intend,"");
            int bias = getVar(varName, argu.varList, argu.mc);
            if (bias < 0)
                ErrorPrint.print("Could not solve var %s at (%d, %d)", varName, n.f0.f0.beginLine, n.f0.f0.beginColumn);
            System.out.printf("HSTORE TEMP %d %d TEMP %d\n", THIS_POINTER, bias, p.result);
        }

        n.f3.accept(this, argu);
        return null;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    public PigletRet visit(ArrayAssignmentStatement n, PigletLabels argu)
    {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        PigletRet p1 =  n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        PigletRet p2 = n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        //first translate the two expressions

        //similar to the last, it is a array here
        String varName = n.f0.f0.tokenImage;
        if (argu.varList.containsKey(varName))
        {
            int bias = argu.varList.get(varName);
            if (bias <= 0)
            {
                int newtemp = newTemp();
                PrintIntend(argu.intend, "");
                System.out.printf("HLOAD TEMP %d TEMP %d %d\n", newtemp, EXTENDED_PARAM_POINTER, -bias*4);
                bias = newtemp;
            }
            PrintIntend(argu.intend, "");
            System.out.printf("HSTORE PLUS TEMP %d TIMES TEMP %d 4 0 TEMP %d\n", bias, p1.result, p2.result);
        }
        else
        {
            PrintIntend(argu.intend,"");
            int bias = getVar(varName, argu.varList, argu.mc);
            int newtemp = newTemp();
            System.out.printf("HLOAD TEMP %d TEMP %d %d\n", newtemp, THIS_POINTER, bias);
            PrintIntend(argu.intend,"");
            System.out.printf("HSTORE PLUS TEMP %d TIMES TEMP %d 4 0 TEMP %d\n", newtemp, p1.result, p2.result);
        }
        return null;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    public PigletRet visit(IfStatement n, PigletLabels argu)
    {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        PigletRet p = n.f2.accept(this, argu);
        PrintIntend(argu.intend, "");

        //set up branch
        String BFalse = newLabel();
        System.out.printf("CJUMP TEMP %d %s\n", p.result, BFalse);

        //translate BTrue statement then jump to next
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        PrintIntend(argu.intend, "");
        String SNext = newLabel();
        System.out.printf("JUMP %s\n", SNext);

        //translate BFalse statement
        PrintIntend(argu.intend, BFalse);
        System.out.print("NOOP\n");
        n.f6.accept(this, argu);

        //set up Snext label
        PrintIntend(argu.intend, SNext);
        System.out.printf("NOOP\n");
        return null;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public PigletRet visit(WhileStatement n, PigletLabels argu)
    {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);

        //first set up label
        String newlabel = newLabel();
        PrintIntend(argu.intend, newlabel);
        System.out.print("NOOP\n");
        String SNext = newLabel();

        //translate expression
        PigletRet p = n.f2.accept(this, argu);
        PrintIntend(argu.intend, "");
        System.out.printf("CJUMP TEMP %d %s\n", p.result, SNext);

        n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        //jump back
        PrintIntend(argu.intend, "");
        System.out.printf("JUMP %s\n", newlabel);

        //set up  next statement
        PrintIntend(argu.intend, SNext);
        System.out.printf("NOOP\n");
        return null;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public PigletRet visit(PrintStatement n, PigletLabels argu)
    {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        PigletRet p = n.f2.accept(this, argu);
        PrintIntend(argu.intend, "");
        System.out.printf("PRINT TEMP %d\n", p.result);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return null;
    }

    /**
     * f0 -> AndExpression()
     *       | CompareExpression()
     *       | PlusExpression()
     *       | MinusExpression()
     *       | TimesExpression()
     *       | ArrayLookup()
     *       | ArrayLength()
     *       | MessageSend()
     *       | PrimaryExpression()
     */
    public PigletRet visit(Expression n, PigletLabels argu) {
        //translate a expression, needed to return its type and the temp id of its result
        PigletRet p = n.f0.accept(this, argu);
        return p;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    public PigletRet visit(AndExpression n, PigletLabels argu)
    {
        //translate first expression
        PigletRet p1 = n.f0.accept(this, argu);
        String newlabel = newLabel();

        //assign a new temp for the result
        int newtemp = newTemp();
        PrintIntend(argu.intend, "");
        System.out.printf("MOVE TEMP %d 0\n", newtemp);

        //if the first expression is false then shortccut
        PrintIntend(argu.intend, "");
        System.out.printf("CJUMP TEMP %d %s\n", p1.result, newlabel);
        n.f1.accept(this, argu);

        //translate second expression the final result is the same as the second
        PigletRet p2 = n.f2.accept(this, argu);
        PrintIntend(argu.intend, "");
        System.out.printf("MOVE TEMP %d TEMP %d\n", newtemp, p2.result);
        PrintIntend(argu.intend, newlabel);
        System.out.print("NOOP\n");

        //return the result
        PigletRet p = new PigletRet();
        p.result = newtemp;
        p.type = new MType("Boolean");
        return p;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public PigletRet visit(CompareExpression n, PigletLabels argu)
    {
        //translate the two expressions
        PigletRet p1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        PigletRet p2 = n.f2.accept(this, argu);

        //calculate the result
        int newtemp = newTemp();
        PrintIntend(argu.intend, "");
        System.out.printf("MOVE TEMP %d LT TEMP %d TEMP %d\n", newtemp, p1.result, p2.result);

        PigletRet p = new PigletRet();
        p.result = newtemp;
        p.type = new MType("Boolean");
        return p;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public PigletRet visit(PlusExpression n, PigletLabels argu)
    {
        //same as the last
        PigletRet p1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        PigletRet p2 = n.f2.accept(this, argu);
        int newtemp = newTemp();
        PrintIntend(argu.intend, "");
        System.out.printf("MOVE TEMP %d PLUS TEMP %d TEMP %d\n", newtemp, p1.result, p2.result);
        PigletRet p = new PigletRet();
        p.result = newtemp;
        p.type = new MType("Int");
        return p;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public PigletRet visit(MinusExpression n, PigletLabels argu)
    {
        //same as the last
        PigletRet p1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        PigletRet p2 = n.f2.accept(this, argu);
        int newtemp = newTemp();
        PrintIntend(argu.intend, "");
        System.out.printf("MOVE TEMP %d MINUS TEMP %d TEMP %d\n", newtemp, p1.result, p2.result);
        PigletRet p = new PigletRet();
        p.result = newtemp;
        p.type = new MType("Int");
        return p;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public PigletRet visit(TimesExpression n, PigletLabels argu)
    {
        //same as the last
        PigletRet p1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        PigletRet p2 = n.f2.accept(this, argu);
        int newtemp = newTemp();
        PrintIntend(argu.intend, "");
        System.out.printf("MOVE TEMP %d TIMES TEMP %d TEMP %d\n", newtemp, p1.result, p2.result);
        PigletRet p = new PigletRet();
        p.result = newtemp;
        p.type = new MType("Int");
        return p;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public PigletRet visit(ArrayLookup n, PigletLabels argu)
    {
        //calculate the bias then load
        PigletRet p1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        PigletRet p2 = n.f2.accept(this, argu);
        n.f3.accept(this, argu);

        int newtemp = newTemp();
        PrintIntend(argu.intend, "");
        System.out.printf("HLOAD TEMP %d PLUS TEMP %d TIMES TEMP %d 4 0\n", newtemp, p1.result, p2.result);

        PigletRet p = new PigletRet();
        p.result = newtemp;
        p.type = new MType("Int");
        return p;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public PigletRet visit(ArrayLength n, PigletLabels argu) {
        PigletRet p1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);


        int newtemp = newTemp();
        PrintIntend(argu.intend, "");
        System.out.printf("HLOAD TEMP %d PLUS TEMP %d MINUS 0 4 0\n", newtemp, p1.result);
        PigletRet p = new PigletRet();
        p.result = newtemp;
        p.type = new MType("Int");
        return p;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public PigletRet visit(MessageSend n, PigletLabels argu) {
        PigletRet p0 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        //get type of f0 then search the method list
        MType primaryExprReturn = p0.type;
        MClass mc = symbolTable.getClass(primaryExprReturn.getType());
        MType ret;

        //find the method and get its return type
        HashMap<String, Pair<String, Integer>> curMethodList = methodList.get(primaryExprReturn.getType()).getSecond();
        String methodName = n.f2.f0.tokenImage;
        Pair<String, Integer> method = curMethodList.get(methodName);
        int paramslength = mc.getMethod(methodName).paramcnt();
        ret = mc.getMethod(methodName).getReturnType();

        //reach the address of the method
        PrintIntend(argu.intend, "");
        int newtemp1 = newTemp();
        System.out.printf("HLOAD TEMP %d TEMP %d 0\n", newtemp1, p0.result);
        PrintIntend(argu.intend, "");
        int newtemp2 = newTemp();
        System.out.printf("HLOAD TEMP %d TEMP %d %d\n", newtemp2, newtemp1, method.getSecond());
        int newtemp3 = newTemp();

        //if parameters > 15 then prepare extended parameter list
        int newtemp4 = 0;
        argu.paramlength = 0;
        argu.paramtot = paramslength;
        if (paramslength > 15)
        {
            newtemp4 = newTemp();
            PrintIntend(argu.intend, "");
            System.out.printf("MOVE TEMP %d HALLOCATE %d\n", newtemp4, paramslength*4-60);
            argu.paramextend = newtemp4;
        }
        PrintIntend(argu.intend, "");

        //start traslate the method call  the new this   the global class list   the rest of the parameters
        System.out.printf("MOVE TEMP %d CALL TEMP %d (TEMP %d TEMP %d", newtemp3, newtemp2, p0.result, GLOBAL_CLASS_LIST);

        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        System.out.printf(")\n");
        PigletRet p = new PigletRet();
        p.type = ret;
        p.result = newtemp3;
        return p;
    }

    /**
     * f0 -> Expression()
     * f1 -> ( ExpressionRest() )*
     */
    public PigletRet visit(ExpressionList n, PigletLabels argu)
    {
        //translate the first parameter
        System.out.print(" BEGIN\n");
        argu.intend += 4;
        PigletRet p = n.f0.accept(this, argu);
        PrintIntend(argu.intend, "");
        System.out.printf("RETURN TEMP %d\n", p.result);
        argu.intend -= 4;
        PrintIntend(argu.intend, "");
        System.out.print("END");
        argu.paramlength += 1;
        n.f1.accept(this, argu);
        return null;
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public PigletRet visit(ExpressionRest n, PigletLabels argu)
    {
        //translate the rest paramters, if more than 15 put it into the extended parameter list
        if (argu.paramlength < 15)
        {
            System.out.print(" BEGIN\n");
            argu.intend += 4;
            n.f0.accept(this, argu);
            PigletRet p = n.f1.accept(this, argu);
            PrintIntend(argu.intend, "");
            System.out.printf("RETURN TEMP %d\n", p.result);
            argu.intend -= 4;
            PrintIntend(argu.intend, "");
            System.out.print("END");
            argu.paramlength += 1;
        }
        else
        {
            if (argu.paramlength == 15)
                System.out.print(" BEGIN\n");
            argu.intend += 4;
            n.f0.accept(this, argu);
            PigletRet p = n.f1.accept(this, argu);
            PrintIntend(argu.intend, "");
            System.out.printf("HSTORE TEMP %d %d TEMP %d\n", argu.paramextend, (argu.paramlength * 4 - 60), p.result);
            argu.paramlength += 1;
            if (argu.paramlength == argu.paramtot)
            {
                PrintIntend(argu.intend, "");
                System.out.printf("RETURN TEMP %d\n", argu.paramextend);
                PrintIntend(argu.intend - 4, "");
                System.out.print("END");
            }
            argu.intend -= 4;
        }
        return null;
    }

    /**
     * f0 -> IntegerLiteral()
     *       | TrueLiteral()
     *       | FalseLiteral()
     *       | Identifier()
     *       | ThisExpression()
     *       | ArrayAllocationExpression()
     *       | AllocationExpression()
     *       | NotExpression()
     *       | BracketExpression()
     */
    public PigletRet visit(PrimaryExpression n, PigletLabels argu)
    {
        PigletRet p = n.f0.accept(this, argu);
        return p;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public PigletRet visit(IntegerLiteral n, PigletLabels argu)
    {
        n.f0.accept(this, argu);
        String s = n.f0.toString();
        PrintIntend(argu.intend, "");
        int newtemp = newTemp();
        int val = Integer.parseInt(s);
        if (val>=0)
            System.out.printf("MOVE TEMP %d %s\n", newtemp, s);
        else
            System.out.printf("MOVE TEMP %d MINUS 0 TEMP %d\n", newtemp, -val);
        PigletRet p = new PigletRet();
        p.type = new MType("Int");
        p.result = newtemp;
        return p;
    }

    /**
     * f0 -> "true"
     */
    public PigletRet visit(TrueLiteral n, PigletLabels argu)
    {
        n.f0.accept(this, argu);
        PrintIntend(argu.intend, "");
        int newtemp = newTemp();
        System.out.printf("MOVE TEMP %d 1\n", newtemp);
        PigletRet p = new PigletRet();
        p.type = new MType("Boolean");
        p.result = newtemp;
        return p;
    }

    /**
     * f0 -> "false"
     */
    public PigletRet visit(FalseLiteral n, PigletLabels argu)
    {
        n.f0.accept(this, argu);
        int newtemp = newTemp();
        PrintIntend(argu.intend, "");
        System.out.printf("MOVE TEMP %d 0\n", newtemp);
        PigletRet p = new PigletRet();
        p.type = new MType("Boolean");
        p.result = newtemp;
        return p;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public PigletRet visit(Identifier n, PigletLabels argu)
    {
        String name;
        name = n.f0.toString();

        //mainly find the identifier and return its type
        n.f0.accept(this, argu);
        PigletRet p = new PigletRet();

        //first find out whether it is in a method. if not than do nothing
        if (argu.varList == null)
            return null;
        else
            //same as above
            if (argu.varList.containsKey(name))
            {
                int bias = argu.varList.get(name);
                if (bias <= 0)
                {
                    int newtemp = newTemp();
                    PrintIntend(argu.intend, "");
                    System.out.printf("HLOAD TEMP %d TEMP %d %d\n", newtemp, EXTENDED_PARAM_POINTER, -bias*4);
                    p.result = newtemp;
                }
                else
                    p.result = bias;
                if (argu.mm == null)
                    ErrorPrint.print("WRONG!\n");
                p.type = new MType(argu.mm.getVar(name).getType());
            } else
            {
                if (getVar(name, argu.varList, argu.mc) == -1)
                    return  null;
                int newtemp = newTemp();
                p.result = newtemp;
                p.type = argu.mm.getVar(name);
                PrintIntend(argu.intend, "");
                System.out.printf("HLOAD TEMP %d TEMP 0 %d\n", newtemp, getVar(name, argu.varList, argu.mc));
            }
        return p;
    }

    /**
     * f0 -> "this"
     */
    public PigletRet visit(ThisExpression n, PigletLabels argu)
    {
        n.f0.accept(this, argu);
        PigletRet p = new PigletRet();
        p.type = new MType(argu.mc.getType());
        p.result = THIS_POINTER;
        return p;
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public PigletRet visit(ArrayAllocationExpression n, PigletLabels argu)
    {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        PigletRet p0 = n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        //calculate the space needed
        int newtemp1 = newTemp();
        PrintIntend(argu.intend, "");
        System.out.printf("MOVE TEMP %d PLUS TIMES TEMP %d 4 4\n", newtemp1, p0.result);

        //allocate the space and put its length
        int newtemp2 = newTemp();
        PrintIntend(argu.intend, "");
        System.out.printf("MOVE TEMP %d HALLOCATE TEMP %d\n", newtemp2, newtemp1);
        PrintIntend(argu.intend, "");
        System.out.printf("HSTORE TEMP %d 0 TEMP %d\n", newtemp2, p0.result);

        //initialize the space to 0
        int newtemp3 = newTemp();
        String newlabel1 = newLabel();
        String newlabel2 = newLabel();
        PrintIntend(argu.intend, "");
        System.out.printf("MOVE TEMP %d 0\n", newtemp3);
        PrintIntend(argu.intend, newlabel1);
        System.out.printf("CJUMP LT TEMP %d TEMP %d %s\n", newtemp3, p0.result, newlabel2);
        PrintIntend(argu.intend, "");
        System.out.printf("HSTORE PLUS TEMP %d TIMES TEMP %d 4 4 0\n", newtemp2, newtemp3);
        PrintIntend(argu.intend, "");
        System.out.printf("MOVE TEMP %d PLUS TEMP %d 1\n", newtemp3, newtemp3);
        PrintIntend(argu.intend, "");
        System.out.printf("JUMP %s\n", newlabel1);

        //return its type
        int newtemp4 = newTemp();
        PrintIntend(argu.intend, newlabel2);
        System.out.printf("MOVE TEMP %d PLUS TEMP %d 4\n", newtemp4, newtemp2);
        PigletRet p = new PigletRet();
        p.type = new MType("IntArray");
        p.result = newtemp4;
        return p;
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public PigletRet visit(AllocationExpression n, PigletLabels argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);

        //fetch class info from symbol table
        String className = n.f1.f0.tokenImage;
        MClass mc = symbolTable.getClass(className);
        int ml = methodList.get(className).getFirst();
        HashMap<String, Integer> vl = varLists.get(className);
        int size = vl.size();

        //first get the pointer to its method table
        int newtemp0 = newTemp();
        PrintIntend(argu.intend, "");
        System.out.printf("HLOAD TEMP %d TEMP %d %d\n", newtemp0, GLOBAL_CLASS_LIST, ml * 4);

        //allocate space
        int newtemp1 = newTemp();
        PrintIntend(argu.intend, "");
        System.out.printf("MOVE TEMP %d HALLOCATE %d\n", newtemp1, size*4 + 4);
        PrintIntend(argu.intend, "");
        System.out.printf("HSTORE TEMP %d 0 TEMP %d\n", newtemp1, newtemp0);

        //initialize the space to 0
        int newtemp2 = newTemp();
        String newlabel1 = newLabel();
        String newlabel2 = newLabel();
        PrintIntend(argu.intend, "");
        System.out.printf("MOVE TEMP %d 0\n", newtemp2);
        PrintIntend(argu.intend, newlabel1);
        System.out.printf("CJUMP LT TEMP %d %d %s\n", newtemp2, size, newlabel2);
        PrintIntend(argu.intend, "");
        System.out.printf("HSTORE PLUS TEMP %d TIMES TEMP %d 4 4 0\n", newtemp1, newtemp2);
        PrintIntend(argu.intend, "");
        System.out.printf("MOVE TEMP %d PLUS TEMP %d 1\n", newtemp2, newtemp2);
        PrintIntend(argu.intend, "");
        System.out.printf("JUMP %s\n", newlabel1);
        PrintIntend(argu.intend, newlabel2);
        System.out.printf("NOOP\n");

        PigletRet p = new PigletRet();
        p.result = newtemp1;
        p.type = new MType(className);
        return p;
    }

    /**
     * f0 -> "!"
     * f1 -> Expression()
     */
    public PigletRet visit(NotExpression n, PigletLabels argu) {
        n.f0.accept(this, argu);
        PigletRet p0 = n.f1.accept(this, argu);
        PrintIntend(argu.intend, "");
        int newtemp1 = newTemp();
        System.out.printf("MOVE TEMP %d MINUS 1 TEMP %d\n", newtemp1, p0.result);
        PigletRet p = new PigletRet();
        p.result = newtemp1;
        p.type = new MType("Boolean");
        return p;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public PigletRet visit(BracketExpression n, PigletLabels argu) {
        n.f0.accept(this, argu);
        PigletRet p =  n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return p;
    }

}
