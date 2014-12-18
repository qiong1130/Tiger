package ast;

import ast.Ast.Class;
import ast.Ast.Exp.Add;
import ast.Ast.Type.Boolean;

public interface Visitor
{
  // expressions
  public void visit(ast.Ast.Exp.Add e);
  public void visit(ast.Ast.Exp.And e);

  public void visit(ast.Ast.Exp.ArraySelect e);

  public void visit(ast.Ast.Exp.Call e);

  public void visit(ast.Ast.Exp.False e);

  public void visit(ast.Ast.Exp.Id e);

  public void visit(ast.Ast.Exp.Length e);

  public void visit(ast.Ast.Exp.Lt e);

  public void visit(ast.Ast.Exp.NewIntArray e);

  public void visit(ast.Ast.Exp.NewObject e);

  public void visit(ast.Ast.Exp.Not e);

  public void visit(ast.Ast.Exp.Num e);

  public void visit(ast.Ast.Exp.Sub e);

  public void visit(ast.Ast.Exp.This e);

  public void visit(ast.Ast.Exp.Times e);

  public void visit(ast.Ast.Exp.True e);

  // statements
  public void visit(ast.Ast.Stm.Assign s);

  public void visit(ast.Ast.Stm.AssignArray s);

  public void visit(ast.Ast.Stm.Block s);

  public void visit(ast.Ast.Stm.If s);

  public void visit(ast.Ast.Stm.Print s);

  public void visit(ast.Ast.Stm.While s);

  // type
  public void visit(ast.Ast.Type.Boolean t);

  public void visit(ast.Ast.Type.ClassType t);

  public void visit(ast.Ast.Type.Int t);

  public void visit(ast.Ast.Type.IntArray t);

  // dec
  public void visit(ast.Ast.Dec.DecSingle d);

  // method
  public void visit(ast.Ast.Method.MethodSingle m);

  // class
  public void visit(ast.Ast.Class.ClassSingle c);

  // main class
  public void visit(ast.Ast.MainClass.MainClassSingle c);

  // program
  public void visit(ast.Ast.Program.ProgramSingle p);

}
