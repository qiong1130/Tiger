package codegen.C;

import codegen.C.Ast.Dec;
import codegen.C.Ast.Exp;
import codegen.C.Ast.MainMethod;
import codegen.C.Ast.Method;
import codegen.C.Ast.Program;
import codegen.C.Ast.Stm;
import codegen.C.Ast.Type;
import codegen.C.Ast.Vtable;
import codegen.C.Ast.Class;


public interface Visitor
{
  // expressions
  public void visit(Exp.Add e);

  public void visit(Exp.And e);

  public void visit(Exp.ArraySelect e);

  public void visit(Exp.Call e);

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

  // statements
  public void visit(Stm.Assign s);

  public void visit(Stm.AssignArray s);

  public void visit(Stm.Block s);

  public void visit(Stm.If s);

  public void visit(Stm.Print s);

  public void visit(Stm.While s);

  // type
  public void visit(Type.Class t);

  public void visit(Type.Int t);

  public void visit(Type.IntArray t);

  // dec
  public void visit(Dec.DecSingle d);

  // method
  public void visit(Method.MethodSingle m);

  // main method
  public void visit(MainMethod.MainMethodSingle m);

  // vtable
  public void visit(Vtable.VtableSingle v);

  // class
  public void visit(Class.ClassSingle c);

  // program
  public void visit(Program.ProgramSingle p);
}
