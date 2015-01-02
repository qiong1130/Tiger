package ast;

import ast.Ast.Class.ClassSingle;
import ast.Ast.Dec.DecSingle;
import ast.Ast.Exp;
import ast.Ast.MainClass;
import ast.Ast.Method;
import ast.Ast.Program;
import ast.Ast.Stm;
import ast.Ast.Class;
import ast.Ast.Type;

public interface Visitor
{
  // expressions
  public void visit(Exp.Add e);

  public void visit(Exp.And e);

  public void visit(Exp.ArraySelect e);

  public void visit(Exp.Call e);

  public void visit(Exp.False e);

  public void visit(Exp.Id e);

  public void visit(Exp.Length e);

  public void visit(Exp.Lt e);

  public void visit(Exp.NewIntArray e);

  public void visit(Exp.NewObject e);

  public void visit(Exp.Not e);

  public void visit(Exp.Num e);

  public void visit(Exp.Sub e);

  public void visit(Exp.This e);

  public void visit(Exp.Times e);

  public void visit(Exp.True e);

  // statements
  public void visit(Stm.Assign s);

  public void visit(Stm.AssignArray s);

  public void visit(Stm.Block s);

  public void visit(Stm.If s);

  public void visit(Stm.Print s);

  public void visit(Stm.While s);

  // type
  public void visit(Type.Boolean t);

  public void visit(Type.ClassType t);

  public void visit(Type.Int t);

  public void visit(Type.IntArray t);

  // dec
  public void visit(DecSingle d);

  // method
  public void visit(Method.MethodSingle m);

  // class
  public void visit(Class.ClassSingle c);

  // main class
  public void visit(MainClass.MainClassSingle c);

  // program
  public void visit(Program.ProgramSingle p);
}
