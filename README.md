# 编译实习 课程项目

王子龙、费天一

 ***miniJava -> piglet -> sPiglet -> kanga -> mips***  

## 一、miniJava 类型检查

1. 定义符号表：
    - `MType`：记录当前对象的类型；
    - `MClassList (extends MType)`：记录从Class名称到MClass的映射
    - `MIdentifier (extends MType)`：记录当前对象的名称、行、列
    - `MClass (extends MIdentifier)`：记录当前类中从名称到变量MVar或方法MMethod的映射
    - `MMethod (extends MIdentifier)`：记录所属类、返回类型、参数变量列表、内部变量列表
    - `MVar (extends MIdentifier)`：记录当前变量所属对象（MMethod 或 MClass）

2. 构造符号表 (`BuildSymbolTableVisitor extends GJDepthFirst<MType, MType>`)：
    - 通过深度优先搜索将对应的类与对象保存在MClassList类型中
    - 在构造MClassList时只检查是否存在重复定义的类、方法、变量
    - 由于类与类之间的继承关系不一定遵循时序，可能继承时，父类尚未声明，于是在先不生成继承关系

3. 类型检查 (`TypeCheckVisitor extends GJDepthFirst <MType, MType>`)：
    - 完成类之间继承关系；检查循环继承；检查方法重载
    - 检查方法的返回值和声明的返回值是否匹配（在检查类之间匹配时，考虑继承关系，见`MClass::checkExtendAssign(String left, String right)`）
    - 在各个算数运算和分支、循环、打印等结构处检查变量的类型
    - 在遇到`<IDENTIFIER>`时，检查是否是未声明的变量，通过参数`argu`的类型分类讨论
    - 检查函数调用时，通过全局变量`curFormalParaCheckMethod`传递当前需要检查的方法，通过`MClass::startCheckFormalPara(), checkingFormalPara(String curParaType, MClassList allClassList), endCheckFormalPara()`，以及构造符号表时记录的参数顺序，完成检查实际参数和声明参数是否匹配

## 三、sPiglet -> kanga

1. 定义符号表：
    - `FlowNode`：流图中的基本块（由于大作业时编译正课还没学相关知识，于是这里的基本块由单条语句组成），包含`in`、`out`、`def`、`use`等临时变量信息和`pre`、`next`流图结构信息
    - `FlowGraph`：流图，由若干`FlowNode`组成，每个过程生成一个流图
    - `IntervalAnalysis`：通过线性分析，分配寄存器，在`FlowGraph`中生成
    - `RegSelect`：当前寄存器分配的对应关系（临时变量映射到寄存器），记录在每个`FlowNode`中

2. 构造基本块和流图 (`BuildGraphVisitor extends GJNoArguDepthFirst<Object>`)：
    - 对每行语句生成基本块，同时记录当前块的`def`、`use`信息，并记录每个块之间的时序关系，注意`CJump`、`Jump`等语句的影响，由于跳转的label可能尚未生成，于是将跳转关系记录在`FlowGraph::pendingEdges`中
    - 在一个流图的若干基本块头和尾添加两个独立的基本块`Entry`、`Exit`
    - 在`FlowGraph::finishGraph()`中完成生成流图在最后工作：
      - 根据`FlowGraph::pendingEdges`回填跳转语句的前驱、后继信息
      - 分析活跃变量，生成每个基本块的`in`、`out`
      - 通过`IntervalAnalysis()`分配寄存器，在我们的实现中`s0-s7`、`t3-t9`都是被调用者保存寄存器，只有`t0-t2`用于临时变量寄存器，因为一条语句最多用到三个不同的临时变量：
        - 通过每个基本块的`in`、`out`信息，计算每个临时变量在当前流图中的开始活跃时间和停止活跃时间，即对应基本块的标号
        - 遍历每个基本块，为每个临时变量分配寄存器，当寄存器不够时，将当前活跃的最晚结束的寄存器入栈，在栈中的位置记为`Xi`，与寄存器命名一致，当有弹栈导致栈中间有空位，将这些空位记录在`stackMiddleAvailable`中，在下次入栈时优先考虑中间的空缺，否则扩大所需栈空间的容量
        - 在完成当前基本块的寄存器分配后，将当前寄存器分配情况记录在当前基本块的`RegSelect`中
      - 在`RegSelect`中，`tempId2Pos(Integer _tempId)`方法返回临时变量的位置，若t、s开头，则得到寄存器，若X开头，则说明当前临时变量在栈中
      - 计算Kanga过程声明中需要的三个参数

3. 生成Kanga语句 (`ToKangaVisitor extends GJDepthFirst<Object, Object>`)：
    - 由于Kanga语法和sPiglet十分相似，所以基本上只需结合当前基本块的`RegSelect`将对应临时变量替换为对应位置即可，若变量得到寄存器分配，直接替换为寄存器即可，若变量在栈中，则将其弹栈到`t0-t2`中，在填写对应临时寄存器
    - 在进入每个基本块时，结合当前基本块寄存器分配结果，将若干寄存器的值入栈，这些寄存器时活跃变量分析时替换入栈的寄存器，这是为了保证进入基本块之后，各个临时变量的对应关系符合`RegSelect`中记录的情况
    - 在生成Kanga语句时有几个难点：
       1. 生成`Exp`类型时的准备：如上所述，我们可能需要弹栈等操作为语句准备寄存器，所以在访问`Exp`类型时，分为两种情况，第一种情况作为存储器准备阶段，生成语句为当前语句准备寄存器，并将存储相关结果的寄存器名称记录在全局变量`exprNotes`中；第二种情况从`exprNotes`中读取对应结果，并打印出来，在两种情况之间打印语句的其他部分，如`MOVE`。
       2. 函数调用的翻译：Kanga和sPiglet在函数调用时的语法区别很大，实际上函数调用也是一种`Exp`类型，我们首先挑选一个临时寄存器(`t0-t2`)，传递参数到`a`寄存器，超过4个参数的部分通过`PASSARG`传递，因为我们将`s0-s7`、`t3-t9`都作为被调用者保存寄存器，所以这里不需要保存寄存器，调用函数后，将结果从`v0`寄存器中拷贝到之前的临时寄存器中作为结果寄存器，这个过程中，只需要一个临时寄存器即可。
       3. 进入一个新的过程的准备：这是Kanga和sPiglet另外一个很大的区别，在进入一个新的过程（或者叫做“函数”）时需要读取参数、保存寄存器等操作。在之前流图分析的寄存器分配的过程中，我们记录了当前过程所用到的所有寄存器，这里首先保存在栈中，注意此时入栈的位置从多余(>4)参数占据的位置之上开始计算，这里计算已占据的栈的空间，在这个过程中涉及到栈的位置的时候要加上这个基址，然后将参数从栈中或者`a`寄存器中传递给`s0-s7`、`t3-t9`，开始如下计算，最后返回值保存到`v0`中，并从栈中恢复`s0-s7`、`t3-t9`。
    
