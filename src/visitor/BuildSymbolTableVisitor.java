package visitor;

import symbol.*;
import syntaxtree.*;

public class BuildSymbolTableVisitor extends GJDepthFirst<MType, MType> {

    /*
      MTypes that need give a return
      Identifier()
      Type()
     */

//    /**
//     * f0 -> MainClass()
//     * f1 -> ( TypeDeclaration() )*
//     * f2 -> <EOF>
//     */
//    public MType visit(Goal n, MType argu) {
//        MType _ret=null;
//        n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        n.f2.accept(this, argu);
//        return _ret;
//    }

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
        MType _ret = null;
        boolean insertMainClassFlag;

        MIdentifier classIdentifier;
        MClass mainClass;
        MIdentifier mainIdentifier;
        MIdentifier formalParaIdentifier;
        MMethod mainMethod;
        MVar formalParaVar;

        n.f0.accept(this, argu);
        classIdentifier = (MIdentifier) n.f1.accept(this, argu);
        mainClass = new MClass(classIdentifier.getName(), classIdentifier.getCol(), classIdentifier.getRow(), null);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        mainIdentifier = new MIdentifier("Identifier", "main", n.f6.beginColumn, n.f6.beginLine);
        formalParaIdentifier = (MIdentifier) n.f11.accept(this, argu);
        mainMethod = new MMethod("main", mainIdentifier.getCol(), mainIdentifier.getRow(), mainClass, "void");
        formalParaVar = new MVar("StringArray", formalParaIdentifier.getName(), formalParaIdentifier.getCol(), formalParaIdentifier.getRow(), mainMethod, true);
        mainMethod.insertFormalPara(formalParaVar);
        mainMethod.insertVar(formalParaVar);
        n.f12.accept(this, argu);
        n.f13.accept(this, argu);
        n.f14.accept(this, mainClass);
        n.f15.accept(this, mainClass);
        n.f16.accept(this, argu);
        n.f17.accept(this, argu);
        mainClass.insertMethod(mainMethod);
        insertMainClassFlag = ((MClassList) argu).insertClass(mainClass);
        if (!insertMainClassFlag) {
            System.out.printf("Duplicate class declaration of MainClass at (%d, %d)\n", mainClass.getRow(), mainClass.getCol());
        }
        return _ret;
    }

//    /**
//     * f0 -> ClassDeclaration()
//     *       | ClassExtendsDeclaration()
//     */
//    public MType visit(TypeDeclaration n, MType argu) {
//        MType _ret=null;
//        n.f0.accept(this, argu);
//        return _ret;
//    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public MType visit(ClassDeclaration n, MType argu) {
        MType _ret = null;
        boolean insertCurClassFlag;

        MIdentifier classIdentifier;
        MClass curClass;

        n.f0.accept(this, argu);
        classIdentifier = (MIdentifier) n.f1.accept(this, argu);
        curClass = new MClass(classIdentifier.getName(), classIdentifier.getCol(), classIdentifier.getRow(), null);
        n.f2.accept(this, argu);
        n.f3.accept(this, curClass);
        n.f4.accept(this, curClass);
        n.f5.accept(this, argu);

        insertCurClassFlag = ((MClassList) argu).insertClass(curClass);
        if (!insertCurClassFlag) {
            System.out.printf("Duplicate class declaration of %s at (%d, %d)\n", curClass.getName(), curClass.getRow(), curClass.getCol());
        }

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
        MType _ret = null;
        boolean insertCurClassFlag;

        MIdentifier classIdentifier;
        MIdentifier extendClassIdentifier;
        MClass curClass;

        n.f0.accept(this, argu);
        classIdentifier = (MIdentifier) n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        extendClassIdentifier = (MIdentifier) n.f3.accept(this, argu);
        curClass = new MClass(classIdentifier.getName(), classIdentifier.getCol(), classIdentifier.getRow(), extendClassIdentifier.getName());
        n.f4.accept(this, argu);
        n.f5.accept(this, curClass);
        n.f6.accept(this, curClass);
        n.f7.accept(this, argu);

        insertCurClassFlag = ((MClassList) argu).insertClass(curClass);
        if (!insertCurClassFlag) {
            System.out.printf("Duplicate class declaration of %s at (%d, %d)\n", curClass.getName(), curClass.getRow(), curClass.getCol());
        }

        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public MType visit(VarDeclaration n, MType argu) {
        MType _ret = null;
        boolean insertCurVarFlag;

        String curVarType;
        MIdentifier varIdentifier;
        MVar curVar;
        String ownerType;

        curVarType = n.f0.accept(this, argu).getType();
        varIdentifier = (MIdentifier)n.f1.accept(this, argu);
        curVar = new MVar(curVarType, varIdentifier.getName(), varIdentifier.getCol(), varIdentifier.getRow(), argu, false);
        n.f2.accept(this, argu);

        ownerType = argu.getType();
        if (ownerType.equals("Method")) {
            insertCurVarFlag = ((MMethod) argu).insertVar(curVar);
        } else {
//            ownerType must be MClass
            insertCurVarFlag = ((MClass) argu).insertVar(curVar);
        }
        if (!insertCurVarFlag) {
            System.out.printf("Duplicate variable declaration of %s at (%d, %d)\n", curVar.getName(), curVar.getRow(), curVar.getCol());
        }

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
        MType _ret = null;
        boolean insertCurMethodFlag;

        String declareReturnType;
        MIdentifier methodIdentifier;
        MMethod curMethod;

        n.f0.accept(this, argu);
        declareReturnType = n.f1.accept(this, argu).getType();
        methodIdentifier = (MIdentifier) n.f2.accept(this, argu);
        curMethod = new MMethod(methodIdentifier.getName(), methodIdentifier.getCol(), methodIdentifier.getRow(), (MClass) argu, declareReturnType);

        n.f3.accept(this, argu);
        n.f4.accept(this, curMethod);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, curMethod);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);

//        if (!declareReturnType.equals(realReturnType)) {
//            System.out.printf("Return type not match in %s at (%d, %d)", curMethod.getName(), curMethod.getRow(), curMethod.getCol());
//        }

        insertCurMethodFlag = ((MClass) argu).insertMethod(curMethod);
        if (!insertCurMethodFlag) {
            System.out.printf("Duplicate method declaration of %s at (%d, %d)\n", curMethod.getName(), curMethod.getRow(), curMethod.getCol());
        }

        return _ret;
    }

//    /**
//     * f0 -> FormalParameter()
//     * f1 -> ( FormalParameterRest() )*
//     */
//    public MType visit(FormalParameterList n, MType argu) {
//        MType _ret=null;
//        n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        return _ret;
//    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public MType visit(FormalParameter n, MType argu) {
        MType _ret = null;
        boolean insertFormalParaFlag;

        String curFormalParaType;
        MIdentifier formalParaIdentifier;
        MVar curFormalPara;

        curFormalParaType = n.f0.accept(this, argu).getType();
        formalParaIdentifier = (MIdentifier) n.f1.accept(this, argu);
        curFormalPara = new MVar(curFormalParaType, formalParaIdentifier.getName(), formalParaIdentifier.getCol(), formalParaIdentifier.getRow(), argu, true);

        insertFormalParaFlag = ((MMethod) argu).insertFormalPara(curFormalPara) && ((MMethod) argu).insertVar(curFormalPara);
        if (!insertFormalParaFlag) {
            System.out.printf("Duplicate formal parameter of %s at (%d, %d)\n", curFormalPara.getName(), curFormalPara.getRow(), curFormalPara.getCol());
        }

        return _ret;
    }

//    /**
//     * f0 -> ","
//     * f1 -> FormalParameter()
//     */
//    public MType visit(FormalParameterRest n, MType argu) {
//        MType _ret=null;
//        n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        return _ret;
//    }

    /**
     * f0 -> ArrayType()
     * | BooleanType()
     * | IntegerType()
     * | Identifier()
     */
    public MType visit(Type n, MType argu) {
        MType _ret;
        _ret = n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public MType visit(ArrayType n, MType argu) {
        MType _ret = new MType("IntArray");
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "boolean"
     */
    public MType visit(BooleanType n, MType argu) {
        MType _ret = new MType("Boolean");
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "int"
     */
    public MType visit(IntegerType n, MType argu) {
        MType _ret = new MType("Int");
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public MType visit(Identifier n, MType argu) {
        String name = n.f0.toString();
        return new MIdentifier("Identifier", name, n.f0.beginColumn, n.f0.beginLine);
    }


//    Some code for type check ... move to separate type check visitor.
//    /**
//     * f0 -> Block()
//     * | AssignmentStatement()
//     * | ArrayAssignmentStatement()
//     * | IfStatement()
//     * | WhileStatement()
//     * | PrintStatement()
//     */
//    public MType visit(Statement n, MType argu) {
//        MType _ret = null;
//        n.f0.accept(this, argu);
//        return _ret;
//    }

//    /**
//     * f0 -> "{"
//     * f1 -> ( Statement() )*
//     * f2 -> "}"
//     */
//    public MType visit(Block n, MType argu) {
//        MType _ret = null;
//        n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        n.f2.accept(this, argu);
//        return _ret;
//    }

//    /**
//     * f0 -> Identifier()
//     * f1 -> "="
//     * f2 -> Expression()
//     * f3 -> ";"
//     */
//    public MType visit(AssignmentStatement n, MType argu) {
//        MType _ret = null;
//
//        MIdentifier varIdentifier;
//        MType exprReturnType;
//
//        varIdentifier = (MIdentifier)n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        exprReturnType = n.f2.accept(this, argu);
//        n.f3.accept(this, argu);
//
//        if (! varIdentifier.getType().equals(exprReturnType.getType())) {
//            System.out.printf("Assign type not match of %s at (%d, %d)", varIdentifier.getName(), varIdentifier.getRow(), varIdentifier.getCol());
//        }
//
//        return _ret;
//    }
//
//    /**
//     * f0 -> Identifier()
//     * f1 -> "["
//     * f2 -> Expression()
//     * f3 -> "]"
//     * f4 -> "="
//     * f5 -> Expression()
//     * f6 -> ";"
//     */
//    public MType visit(ArrayAssignmentStatement n, MType argu) {
//        MType _ret = null;
//
//        MIdentifier arrayVarIdentifier;
//        MType arrayIndexType;
//        MType assignVarType;
//
//        arrayVarIdentifier = (MIdentifier)n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        arrayIndexType = n.f2.accept(this, argu);
//        n.f3.accept(this, argu);
//        n.f4.accept(this, argu);
//        assignVarType = n.f5.accept(this, argu);
//        n.f6.accept(this, argu);
//
//        if (! arrayVarIdentifier.getType().equals("IntArray")) {
//            System.out.printf("Array assign for un-array variable of %s at (%d, %d)", arrayVarIdentifier.getName(), arrayVarIdentifier.getRow(), arrayVarIdentifier.getCol());
//        }
//
//        if (! arrayVarIdentifier.getType().equals(assignVarType.getType())) {
//            System.out.printf("Assign type not match of %s at (%d, %d)", arrayVarIdentifier.getName(), arrayVarIdentifier.getRow(), arrayVarIdentifier.getCol());
//        }
//
//        if (! arrayIndexType.getType().equals("Int")) {
//            System.out.printf("Array index is not int in %s at (%d, %d)", arrayVarIdentifier.getName(), arrayVarIdentifier.getRow(), arrayVarIdentifier.getCol());
//        }
//
//        return _ret;
//    }
//
//    /**
//     * f0 -> "if"
//     * f1 -> "("
//     * f2 -> Expression()
//     * f3 -> ")"
//     * f4 -> Statement()
//     * f5 -> "else"
//     * f6 -> Statement()
//     */
//    public MType visit(IfStatement n, MType argu) {
//        MType _ret = null;
//
//        MType exprReturnType;
//
//        n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        exprReturnType = (MVar) n.f2.accept(this, argu);
//        n.f3.accept(this, argu);
//        n.f4.accept(this, argu);
//        n.f5.accept(this, argu);
//        n.f6.accept(this, argu);
//
//        if (! exprReturnType.getType().equals("Boolean")) {
//            System.out.println("If expression is not boolean");
//        }
//
//        return _ret;
//    }
//
//    /**
//     * f0 -> "while"
//     * f1 -> "("
//     * f2 -> Expression()
//     * f3 -> ")"
//     * f4 -> Statement()
//     */
//    public MType visit(WhileStatement n, MType argu) {
//        MType _ret = null;
//
//        MType exprReturnType;
//
//        n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        exprReturnType = n.f2.accept(this, argu);
//        n.f3.accept(this, argu);
//        n.f4.accept(this, argu);
//
//        if (! exprReturnType.getType().equals("Boolean")) {
//            System.out.println("While expression is not boolean");
//        }
//
//        return _ret;
//    }
//
//    /**
//     * f0 -> "System.out.println"
//     * f1 -> "("
//     * f2 -> Expression()
//     * f3 -> ")"
//     * f4 -> ";"
//     */
//    public MType visit(PrintStatement n, MType argu) {
//        MType _ret = null;
//
//        MType exprReturnType;
//
//        n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        exprReturnType = n.f2.accept(this, argu);
//        n.f3.accept(this, argu);
//        n.f4.accept(this, argu);
//
//        if (! exprReturnType.getType().equals("Int")) {
//            System.out.println("Print expression is not int");
//        }
//
//        return _ret;
//    }
//
//    /**
//     * f0 -> AndExpression()
//     * | CompareExpression()
//     * | PlusExpression()
//     * | MinusExpression()
//     * | TimesExpression()
//     * | ArrayLookup()
//     * | ArrayLength()
//     * | MessageSend()
//     * | PrimaryExpression()
//     */
//    public MType visit(Expression n, MType argu) {
//        return n.f0.accept(this, argu);
//    }
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "&&"
//     * f2 -> PrimaryExpression()
//     */
//    public MType visit(AndExpression n, MType argu) {
//        MType _ret = new MType("Boolean");
//
//        MType leftType;
//        MType rightType;
//
//        leftType = n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        rightType = n.f2.accept(this, argu);
//
//        if (! leftType.getType().equals("Boolean")) {
//            System.out.println("And expression is not boolean");
//        }
//        if (! rightType.getType().equals("Boolean")) {
//            System.out.println("And expression is not boolean");
//        }
//
//        return _ret;
//    }
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "<"
//     * f2 -> PrimaryExpression()
//     */
//    public MType visit(CompareExpression n, MType argu) {
//        MType _ret = new MType("Boolean");
//
//        MType leftType;
//        MType rightType;
//
//        leftType = n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        rightType = n.f2.accept(this, argu);
//
//        if (! leftType.getType().equals("Int")) {
//            System.out.println("Arithmetic expression is not int");
//        }
//        if (! rightType.getType().equals("Int")) {
//            System.out.println("Arithmetic expression is not int");
//        }
//
//        return _ret;
//    }
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "+"
//     * f2 -> PrimaryExpression()
//     */
//    public MType visit(PlusExpression n, MType argu) {
//        MType _ret = new MType("Int");
//
//        MType leftType;
//        MType rightType;
//
//        leftType = n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        rightType = n.f2.accept(this, argu);
//
//        if (! leftType.getType().equals("Int")) {
//            System.out.println("Arithmetic expression is not int");
//        }
//        if (! rightType.getType().equals("Int")) {
//            System.out.println("Arithmetic expression is not int");
//        }
//
//        return _ret;
//    }
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "-"
//     * f2 -> PrimaryExpression()
//     */
//    public MType visit(MinusExpression n, MType argu) {
//        MType _ret = new MType("Int");
//
//        MType leftType;
//        MType rightType;
//
//        leftType = n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        rightType = n.f2.accept(this, argu);
//
//        if (! leftType.getType().equals("Int")) {
//            System.out.println("Arithmetic expression is not int");
//        }
//        if (! rightType.getType().equals("Int")) {
//            System.out.println("Arithmetic expression is not int");
//        }
//
//        return _ret;
//    }
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "*"
//     * f2 -> PrimaryExpression()
//     */
//    public MType visit(TimesExpression n, MType argu) {
//        MType _ret = new MType("Int");
//
//        MType leftType;
//        MType rightType;
//
//        leftType = n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        rightType = n.f2.accept(this, argu);
//
//        if (! leftType.getType().equals("Int")) {
//            System.out.println("Arithmetic expression is not int");
//        }
//        if (! rightType.getType().equals("Int")) {
//            System.out.println("Arithmetic expression is not int");
//        }
//
//        return _ret;
//    }
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "["
//     * f2 -> PrimaryExpression()
//     * f3 -> "]"
//     */
//    public MType visit(ArrayLookup n, MType argu) {
//        MType _ret = new MType("Int");
//
//        MType curType;
//        MType indexType;
//
//        curType = n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        indexType = n.f2.accept(this, argu);
//        n.f3.accept(this, argu);
//
//        if (! curType.getType().equals("IntArray")) {
//            System.out.println("Look up an un-array variable");
//        }
//        if (! indexType.getType().equals("Int")) {
//            System.out.println("Array index is not int");
//        }
//
//        return _ret;
//    }
//
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "."
//     * f2 -> "length"
//     */
//    public MType visit(ArrayLength n, MType argu) {
//        MType _ret = new MType("Int");
//
//        MType curType;
//
//        curType = n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        n.f2.accept(this, argu);
//
//        if (! curType.getType().equals("IntArray")) {
//            System.out.println("Acquire length to an un-array variable");
//        }
//
//        return _ret;
//    }


// no change
//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "."
//     * f2 -> Identifier()
//     * f3 -> "("
//     * f4 -> ( ExpressionList() )?
//     * f5 -> ")"
//     */
//    public MType visit(MessageSend n, MType argu) {
//        MType _ret = null;
//        n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        n.f2.accept(this, argu);
//        n.f3.accept(this, argu);
//        n.f4.accept(this, argu);
//        n.f5.accept(this, argu);
//        return _ret;
//    }
//
//    /**
//     * f0 -> Expression()
//     * f1 -> ( ExpressionRest() )*
//     */
//    public MType visit(ExpressionList n, MType argu) {
//        MType _ret = null;
//        n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        return _ret;
//    }
//
//    /**
//     * f0 -> ","
//     * f1 -> Expression()
//     */
//    public MType visit(ExpressionRest n, MType argu) {
//        MType _ret = null;
//        n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        return _ret;
//    }
//
//    /**
//     * f0 -> IntegerLiteral()
//     * | TrueLiteral()
//     * | FalseLiteral()
//     * | Identifier()
//     * | ThisExpression()
//     * | ArrayAllocationExpression()
//     * | AllocationExpression()
//     * | NotExpression()
//     * | BracketExpression()
//     */
//    public MType visit(PrimaryExpression n, MType argu) {
//        MType _ret = null;
//        n.f0.accept(this, argu);
//        return _ret;
//    }
//
//    /**
//     * f0 -> <INTEGER_LITERAL>
//     */
//    public MType visit(IntegerLiteral n, MType argu) {
//        MType _ret = null;
//        n.f0.accept(this, argu);
//        return _ret;
//    }
//
//    /**
//     * f0 -> "true"
//     */
//    public MType visit(TrueLiteral n, MType argu) {
//        MType _ret = null;
//        n.f0.accept(this, argu);
//        return _ret;
//    }
//
//    /**
//     * f0 -> "false"
//     */
//    public MType visit(FalseLiteral n, MType argu) {
//        MType _ret = null;
//        n.f0.accept(this, argu);
//        return _ret;
//    }
//

//
//    /**
//     * f0 -> "this"
//     */
//    public MType visit(ThisExpression n, MType argu) {
//        MType _ret = null;
//        n.f0.accept(this, argu);
//        return _ret;
//    }
//
//    /**
//     * f0 -> "new"
//     * f1 -> "int"
//     * f2 -> "["
//     * f3 -> Expression()
//     * f4 -> "]"
//     */
//    public MType visit(ArrayAllocationExpression n, MType argu) {
//        MType _ret = null;
//        n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        n.f2.accept(this, argu);
//        n.f3.accept(this, argu);
//        n.f4.accept(this, argu);
//        return _ret;
//    }
//
//    /**
//     * f0 -> "new"
//     * f1 -> Identifier()
//     * f2 -> "("
//     * f3 -> ")"
//     */
//    public MType visit(AllocationExpression n, MType argu) {
//        MType _ret = null;
//        n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        n.f2.accept(this, argu);
//        n.f3.accept(this, argu);
//        return _ret;
//    }
//
//    /**
//     * f0 -> "!"
//     * f1 -> Expression()
//     */
//    public MType visit(NotExpression n, MType argu) {
//        MType _ret = null;
//        n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        return _ret;
//    }
//
//    /**
//     * f0 -> "("
//     * f1 -> Expression()
//     * f2 -> ")"
//     */
//    public MType visit(BracketExpression n, MType argu) {
//        MType _ret = null;
//        n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//        n.f2.accept(this, argu);
//        return _ret;
//    }

}
