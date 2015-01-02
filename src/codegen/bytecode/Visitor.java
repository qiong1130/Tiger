package codegen.bytecode;


import codegen.bytecode.Ast.Dec;
import codegen.bytecode.Ast.MainClass;
import codegen.bytecode.Ast.Method;
import codegen.bytecode.Ast.Program;
import codegen.bytecode.Ast.Stm.Aload;
import codegen.bytecode.Ast.Stm.Areturn;
import codegen.bytecode.Ast.Stm.Astore;
import codegen.bytecode.Ast.Stm.Goto;
import codegen.bytecode.Ast.Stm.Ificmplt;
import codegen.bytecode.Ast.Stm.Ifne;
import codegen.bytecode.Ast.Stm.Iload;
import codegen.bytecode.Ast.Stm.Imul;
import codegen.bytecode.Ast.Stm.Invokevirtual;
import codegen.bytecode.Ast.Stm.Ireturn;
import codegen.bytecode.Ast.Stm.Istore;
import codegen.bytecode.Ast.Stm.Isub;
import codegen.bytecode.Ast.Stm.LabelJ;
import codegen.bytecode.Ast.Stm.Ldc;
import codegen.bytecode.Ast.Stm.New;
import codegen.bytecode.Ast.Stm.Print;
import codegen.bytecode.Ast.Type;
import codegen.bytecode.Ast.Class;


public interface Visitor
{
  // statements
  public void visit(Aload s);

  public void visit(Areturn s);

  public void visit(Astore s);

  public void visit(Goto s);

  public void visit(Ificmplt s);

  public void visit(Ifne s);

  public void visit(Iload s);

  public void visit(Imul s);

  public void visit(Ireturn s);

  public void visit(Istore s);

  public void visit(Isub s);

  public void visit(Invokevirtual s);

  public void visit(LabelJ s);

  public void visit(Ldc s);

  public void visit(Print s);

  public void visit(New s);

  // type
  public void visit(Type.ClassType t);

  public void visit(Type.Int t);

  public void visit(Type.IntArray t);

  // dec
  public void visit(Dec.DecSingle d);

  // method
  public void visit(Method.MethodSingle m);

  // class
  public void visit(Class.ClassSingle c);

  // main class
  public void visit(MainClass.MainClassSingle c);

  // program
  public void visit(Program.ProgramSingle p);
}
