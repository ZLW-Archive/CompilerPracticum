package visitor;

import symbol.*;
import syntaxtree.*;

public class TypeCheckVisitor extends GJDepthFirst <MType, MType> {
    /*
    * TODO checks:
    *  1. naive type requirement (if, while, print ...)
    *  2. undefined variable, class, method
    * */

    private MClassList allClassList;

    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    public MType visit(Goal n, MType argu) {
        MType _ret = null;
        boolean setAllExtendClassFlag;
        boolean existExtendLoopFlag;
        boolean existOverrideFlag;

        allClassList = (MClassList)argu;
        setAllExtendClassFlag = allClassList.setAllExtendClass();
        existExtendLoopFlag = allClassList.findExtendClassLoop();
        existOverrideFlag = allClassList.findAllOverride();

//        if (! setAllExtendClassFlag) {
//            System.out.println("extend class error");
//        }
        if (existExtendLoopFlag) {
            System.out.println("Exist extend loop");
        }
        if (existOverrideFlag) {
            System.out.println("Exist override");
        }

        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
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
    public MType visit(MainClass n, MType argu) {
        /*
        * TODO: no check, but pass main method down
        * */
        MType _ret = null;

        MIdentifier classIdentifier;
        MClass mainClass;
        MMethod mainMethod;

        classIdentifier = (MIdentifier) n.f1.accept(this, argu);
        mainClass = ((MClassList)argu).getClass(classIdentifier.getName());
        mainMethod = mainClass.getMethod("main");

        n.f14.accept(this, mainMethod);
        n.f15.accept(this, mainMethod);

        return _ret;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public MType visit(ClassDeclaration n, MType argu) {
        /*
         * TODO: no check, but pass sth down
         * */
        MType _ret = null;

        MIdentifier classIdentifier;
        MClass curClass;

        classIdentifier = (MIdentifier) n.f1.accept(this, argu);
        curClass = ((MClassList)argu).getClass(classIdentifier.getName());
        n.f3.accept(this, curClass);
        n.f4.accept(this, curClass);

        return _ret;
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
    public MType visit(ClassExtendsDeclaration n, MType argu) {
        /*
        * TODO check:
        *  1. the identifier extended must be a class and defined
        * */
        MType _ret=null;

        MIdentifier curClassIdentifier;
        MIdentifier extendClassIdentifier;
        MClass curClass;

        curClassIdentifier = (MIdentifier) n.f1.accept(this, argu);
        extendClassIdentifier = (MIdentifier)n.f3.accept(this, argu);

        if (extendClassIdentifier.getName().equals(curClassIdentifier.getName()) |      // self extend
                ((MClassList)argu).getClass(extendClassIdentifier.getName()) == null    // extend to an unknown class
        ) {
            System.out.printf("Extend error of %s at (%d, %d)\n", curClassIdentifier.getName(), curClassIdentifier.getRow(), curClassIdentifier.getCol());
        }

        curClass = ((MClassList)argu).getClass(curClassIdentifier.getName());

        n.f5.accept(this, curClass);
        n.f6.accept(this, curClass);
        return _ret;
    }


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
    public MType visit(MethodDeclaration n, MType argu) {
        /*
        * TODO: checks:
        *  1. return type check
        *  2. override
        * */
        MType _ret=null;

        MClass ownerClass;
        MIdentifier curMethodIdentifier;
        MMethod curMethod;

        MType declareReturnType;
        MType realReturnType;

        ownerClass = (MClass)argu;
        curMethodIdentifier = (MIdentifier) n.f2.accept(this, argu);
        curMethod = ownerClass.getMethod(curMethodIdentifier.getName());

        n.f7.accept(this, curMethod);
        n.f8.accept(this, curMethod);

        declareReturnType = curMethod.getReturnType();
        realReturnType = n.f10.accept(this, curMethod);

        if (! (declareReturnType.getType()).equals(realReturnType.getType())) {
            System.out.printf("Return type not match of %s at (%d, %d)\n", curMethod.getName(), curMethod.getRow(), curMethod.getCol());
        }

        return _ret;
    }


    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public MType visit(Type n, MType argu) {
//        simple type return directly, identifier type: find in allClassList
        return n.f0.accept(this, allClassList);
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public MType visit(ArrayType n, MType argu) {
        return new MType("IntArray");
    }

    /**
     * f0 -> "boolean"
     */
    public MType visit(BooleanType n, MType argu) {
        return new MType("Boolean");

    }

    /**
     * f0 -> "int"
     */
    public MType visit(IntegerType n, MType argu) {
        return new MType("Int");

    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public MType visit(Identifier n, MType argu) {
        MType _ret;

        String name;
        String curType;

        name = n.f0.toString();
        curType = name;

        if (argu instanceof MMethod) {
            if (((MMethod)argu).getVar(name) != null) {
                curType = ((MMethod)argu).getVar(name).getType();
            }
            else {
                System.out.printf("Unknown variable %s at (%d, %d) in method\n", name, n.f0.beginLine, n.f0.beginColumn);
            }
        }
        else if (argu instanceof MClass) {
            if (((MClass)argu).getVar(name) != null) {
                curType = ((MClass)argu).getVar(name).getType();
            }
            else if (((MClass)argu).getMethod(name) != null) {
                curType = "Method";
            }
            else {
                System.out.printf("Unknown variable or method %s at (%d, %d) in class\n", name, n.f0.beginLine, n.f0.beginColumn);
            }
        }
        else if (argu instanceof MClassList) {
            if (((MClassList)argu).getClass(name) == null) {
                System.out.printf("Unknown class %s at (%d, %d)\n", name, n.f0.beginLine, n.f0.beginColumn);
            }
            else {
                curType = name;
            }
        }

        _ret = new MIdentifier(curType, name, n.f0.beginColumn, n.f0.beginLine);

        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public MType visit(AssignmentStatement n, MType argu) {
        MType _ret = null;

        MIdentifier varIdentifier;
        MType exprReturnType;

        varIdentifier = (MIdentifier)n.f0.accept(this, argu);
        exprReturnType = n.f2.accept(this, argu);

        if (! varIdentifier.getType().equals(exprReturnType.getType())) {
            System.out.printf("Assign type not match of %s at (%d, %d)\n", varIdentifier.getName(), varIdentifier.getRow(), varIdentifier.getCol());
        }

        return _ret;
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
    public MType visit(ArrayAssignmentStatement n, MType argu) {
        MType _ret = null;

        MIdentifier arrayVarIdentifier;
        MType arrayIndexType;
        MType assignVarType;

        arrayVarIdentifier = (MIdentifier)n.f0.accept(this, argu);
        arrayIndexType = n.f2.accept(this, argu);
        assignVarType = n.f5.accept(this, argu);

        if (! arrayVarIdentifier.getType().equals("IntArray")) {
            System.out.printf("Array assign for un-array variable of %s at (%d, %d)\n", arrayVarIdentifier.getName(), arrayVarIdentifier.getRow(), arrayVarIdentifier.getCol());
        }

        if (! assignVarType.getType().equals("Int")) {
            System.out.printf("Assign type not match of %s at (%d, %d)\n", arrayVarIdentifier.getName(), arrayVarIdentifier.getRow(), arrayVarIdentifier.getCol());
        }

        if (! arrayIndexType.getType().equals("Int")) {
            System.out.printf("Array index is not int in %s at (%d, %d)\n", arrayVarIdentifier.getName(), arrayVarIdentifier.getRow(), arrayVarIdentifier.getCol());
        }

        return _ret;
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
    public MType visit(IfStatement n, MType argu) {
        MType _ret = null;

        MType exprReturnType;

        exprReturnType = n.f2.accept(this, argu);
        n.f4.accept(this, argu);
        n.f6.accept(this, argu);

        if (! exprReturnType.getType().equals("Boolean")) {
            System.out.println("If expression is not boolean");
        }

        return _ret;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public MType visit(WhileStatement n, MType argu) {
        MType _ret = null;

        MType exprReturnType;

        exprReturnType = n.f2.accept(this, argu);
        n.f4.accept(this, argu);

        if (! exprReturnType.getType().equals("Boolean")) {
            System.out.println("While expression is not boolean");
        }

        return _ret;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public MType visit(PrintStatement n, MType argu) {
        MType _ret = null;

        MType exprReturnType;

        exprReturnType = n.f2.accept(this, argu);

        if (! exprReturnType.getType().equals("Int")) {
            System.out.println("Print expression is not int");
        }

        return _ret;
    }

    /**
     * f0 -> AndExpression()
     * | CompareExpression()
     * | PlusExpression()
     * | MinusExpression()
     * | TimesExpression()
     * | ArrayLookup()
     * | ArrayLength()
     * | MessageSend()
     * | PrimaryExpression()
     */
    public MType visit(Expression n, MType argu) {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    public MType visit(AndExpression n, MType argu) {
        MType _ret = new MType("Boolean");

        MType leftType;
        MType rightType;

        leftType = n.f0.accept(this, argu);
        rightType = n.f2.accept(this, argu);

        if (! leftType.getType().equals("Boolean")) {
            System.out.println("And expression is not boolean");
        }
        if (! rightType.getType().equals("Boolean")) {
            System.out.println("And expression is not boolean");
        }

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public MType visit(CompareExpression n, MType argu) {
        MType _ret = new MType("Boolean");

        MType leftType;
        MType rightType;

        leftType = n.f0.accept(this, argu);
        rightType = n.f2.accept(this, argu);

        if (! leftType.getType().equals("Int")) {
            System.out.println("Arithmetic expression is not int");
        }
        if (! rightType.getType().equals("Int")) {
            System.out.println("Arithmetic expression is not int");
        }

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public MType visit(PlusExpression n, MType argu) {
        MType _ret = new MType("Int");

        MType leftType;
        MType rightType;

        leftType = n.f0.accept(this, argu);
        rightType = n.f2.accept(this, argu);

        if (! leftType.getType().equals("Int")) {
            System.out.println("Arithmetic expression is not int");
        }
        if (! rightType.getType().equals("Int")) {
            System.out.println("Arithmetic expression is not int");
        }

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public MType visit(MinusExpression n, MType argu) {
        MType _ret = new MType("Int");

        MType leftType;
        MType rightType;

        leftType = n.f0.accept(this, argu);
        rightType = n.f2.accept(this, argu);

        if (! leftType.getType().equals("Int")) {
            System.out.println("Arithmetic expression is not int");
        }
        if (! rightType.getType().equals("Int")) {
            System.out.println("Arithmetic expression is not int");
        }

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public MType visit(TimesExpression n, MType argu) {
        MType _ret = new MType("Int");

        MType leftType;
        MType rightType;

        leftType = n.f0.accept(this, argu);
        rightType = n.f2.accept(this, argu);

        if (! leftType.getType().equals("Int")) {
            System.out.println("Arithmetic expression is not int");
        }
        if (! rightType.getType().equals("Int")) {
            System.out.println("Arithmetic expression is not int");
        }

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public MType visit(ArrayLookup n, MType argu) {
        MType _ret = new MType("Int");

        MType curType;
        MType indexType;

        curType = n.f0.accept(this, argu);
        indexType = n.f2.accept(this, argu);

        if (! curType.getType().equals("IntArray")) {
            System.out.println("Look up an un-array variable");
        }
        if (! indexType.getType().equals("Int")) {
            System.out.println("Array index is not int");
        }

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public MType visit(ArrayLength n, MType argu) {
        MType _ret = new MType("Int");

        MType curType;

        curType = n.f0.accept(this, argu);

        if (! curType.getType().equals("IntArray")) {
            System.out.println("Acquire length to an un-array variable");
        }

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public MType visit(MessageSend n, MType argu) {
        /*
        * TODO: checks:
        *  1. undefined method, object, class
        * */
        MType _ret = null;

        MType primaryExprReturn;
        MIdentifier callMethodIdentifier;
        MType callMethodReturnType = null;

        primaryExprReturn = n.f0.accept(this, argu);
//        callMethodIdentifier = (MIdentifier)n.f2.accept(this, (MClass));

        /*
        * Three condition:
        *   1. a variable -> identifier -> look up in the hash map
        *   2. this -> class
        *   3. allocation call -> temp variable
        * */

        if (primaryExprReturn instanceof MVar) {
            MVar curVar = (MVar) primaryExprReturn;
            MClass curVarClass = allClassList.getClass(curVar.getType());
            callMethodIdentifier = (MIdentifier)n.f2.accept(this, curVarClass);
            callMethodReturnType = (curVarClass.getMethod(callMethodIdentifier.getName())).getReturnType();
        }
        else if (primaryExprReturn instanceof MClass) {
            MClass curVarClass = (MClass) primaryExprReturn;
            callMethodIdentifier = (MIdentifier)n.f2.accept(this, curVarClass);
            callMethodReturnType = (curVarClass.getMethod(callMethodIdentifier.getName())).getReturnType();
        }
        else if (primaryExprReturn instanceof MIdentifier) {
            MIdentifier curVarIdentifier = (MIdentifier)primaryExprReturn;
            MClass curVarClass = this.allClassList.getClass(curVarIdentifier.getType());
            callMethodIdentifier = (MIdentifier)n.f2.accept(this, curVarClass);
            callMethodReturnType = (curVarClass.getMethod(callMethodIdentifier.getName())).getReturnType();

        }
        else if (allClassList.getClass(primaryExprReturn.getType()) != null) {
            MClass curVarClass = this.allClassList.getClass(primaryExprReturn.getType());
            callMethodIdentifier = (MIdentifier)n.f2.accept(this, curVarClass);
            callMethodReturnType = (curVarClass.getMethod(callMethodIdentifier.getName())).getReturnType();
        }
        else {
            System.out.println("The obj cannot call a method.");
        }

        n.f4.accept(this, argu);

        _ret = callMethodReturnType;

        return _ret;
    }

    /**
     * f0 -> IntegerLiteral()
     * | TrueLiteral()
     * | FalseLiteral()
     * | Identifier()
     * | ThisExpression()
     * | ArrayAllocationExpression()
     * | AllocationExpression()
     * | NotExpression()
     * | BracketExpression()
     */
    public MType visit(PrimaryExpression n, MType argu) {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public MType visit(IntegerLiteral n, MType argu) {
        return new MType("Int");
    }

    /**
     * f0 -> "true"
     */
    public MType visit(TrueLiteral n, MType argu) {
        return new MType("Boolean");

    }

    /**
     * f0 -> "false"
     */
    public MType visit(FalseLiteral n, MType argu) {
        return new MType("Boolean");
    }



    /**
     * f0 -> "this"
     */
    public MType visit(ThisExpression n, MType argu) {
        MType _ret;

        MMethod ownerMethod;
        MClass ownerClass;

        ownerMethod = (MMethod)argu;
        ownerClass = ownerMethod.getOwnerClass();

        _ret = ownerClass;

        return _ret;
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public MType visit(ArrayAllocationExpression n, MType argu) {
        MType _ret = new MType("IntArray");

        MType indexType;

        indexType = n.f3.accept(this, argu);

        if (! indexType.getType().equals("Int")) {
            System.out.printf("Array index is not int at (%d, %d)\n", n.f2.beginLine, n.f2.beginColumn);
        }

        return _ret;
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public MType visit(AllocationExpression n, MType argu) {
        MType _ret;

        MIdentifier classIdentifier;

        classIdentifier = (MIdentifier)n.f1.accept(this, allClassList);

        _ret = new MVar(classIdentifier.getName(), null, classIdentifier.getCol(), classIdentifier.getRow(), null, true);

        return _ret;
    }

//    TODO: here !!!

    /**
     * f0 -> "!"
     * f1 -> Expression()
     */
    public MType visit(NotExpression n, MType argu) {
        MType _ret = new MType("Boolean");

        MType exprReturnType;

        exprReturnType = n.f1.accept(this, argu);
        if (! exprReturnType.getType().equals("Boolean")) {
            System.out.printf("Not expression of an un-boolean target at (%d, %d)\n", n.f0.beginLine, n.f0.beginColumn);
        }

        return _ret;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public MType visit(BracketExpression n, MType argu) {
        return n.f1.accept(this, argu);
    }

}
