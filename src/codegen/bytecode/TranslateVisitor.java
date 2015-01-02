package codegen.bytecode;

import java.util.Hashtable;
import java.util.LinkedList;

import codegen.bytecode.Ast.Dec;
import codegen.bytecode.Ast.MainClass;
import codegen.bytecode.Ast.Method;
import codegen.bytecode.Ast.Program;
import codegen.bytecode.Ast.Stm;
import codegen.bytecode.Ast.Class;
import codegen.bytecode.Ast.Type;
import codegen.bytecode.Ast.Stm.*;
import util.Label;

import util.Label;

// Given a Java ast, translate it into Java bytecode.

public class TranslateVisitor implements ast.Visitor
{
  private String classId;
  private int index;
  private Hashtable<String, Integer> indexTable;
  private Type.T type; // type after translation
  private Dec.T dec;
  private LinkedList<Stm.T> stms;
  private Method.T method;
  private Class.T classs;
  private MainClass.T mainClass;
  public Program.T program;

  public TranslateVisitor()
  {
    this.classId = null;
    this.indexTable = null;
    this.type = null;
    this.dec = null;
    this.stms = new java.util.LinkedList<Stm.T>();
    this.method = null;
    this.classs = null;
    this.mainClass = null;
    this.program = null;
  }

  private void emit(Stm.T s)
  {
    this.stms.add(s);
  }

  // /////////////////////////////////////////////////////
  // expressions
  @Override
  public void visit(ast.Ast.Exp.Add e)
  {
  }

  @Override
  public void visit(ast.Ast.Exp.And e)
  {
  }

  @Override
  public void visit(ast.Ast.Exp.ArraySelect e)
  {
  }

  @Override
  public void visit(ast.Ast.Exp.Call e)
  {
    e.exp.accept(this);
    for (ast.Ast.Exp.T x : e.args) {
      x.accept(this);
    }
    e.rt.accept(this);
    Type.T rt = this.type;
    java.util.LinkedList<Type.T> at = new java.util.LinkedList<Type.T>();
    for (ast.Ast.Type.T t : e.at) {
      t.accept(this);
      at.add(this.type);
    }
    emit(new Stm.Invokevirtual(e.id, e.type, at, rt));
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.False e)
  {
  }

  @Override
  public void visit(ast.Ast.Exp.Id e)
  {
    int index = this.indexTable.get(e.id);
    ast.Ast.Type.T type = e.type;
    if (type.getNum() > 0)// a reference
      emit(new Stm.Aload(index));
    else
      emit(new Stm.Iload(index));
    // but what about this is a field?
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.Length e)
  {
  }

  @Override
  public void visit(ast.Ast.Exp.Lt e)
  {
    Label tl = new Label(), fl = new Label(), el = new Label();
    e.left.accept(this);
    e.right.accept(this);
    emit(new Stm.Ificmplt(tl));
    emit(new Stm.LabelJ(fl));
    emit(new Stm.Ldc(0));
    emit(new Stm.Goto(el));
    emit(new Stm.LabelJ(tl));
    emit(new Stm.Ldc(1));
    emit(new Stm.Goto(el));
    emit(new Stm.LabelJ(el));
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.NewIntArray e)
  {
  }

  @Override
  public void visit(ast.Ast.Exp.NewObject e)
  {
    emit(new Stm.New(e.id));
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.Not e)
  {
  }

  @Override
  public void visit(ast.Ast.Exp.Num e)
  {
    emit(new Stm.Ldc(e.num));
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.Sub e)
  {
    e.left.accept(this);
    e.right.accept(this);
    emit(new Stm.Isub());
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.This e)
  {
    emit(new Stm.Aload(0));
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.Times e)
  {
    e.left.accept(this);
    e.right.accept(this);
    emit(new Stm.Imul());
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.True e)
  {
  }

  // statements
  @Override
  public void visit(ast.Ast.Stm.Assign s)
  {
    s.exp.accept(this);
    int index = this.indexTable.get(s.id);
    ast.Ast.Type.T type = s.type;
    if (type.getNum() > 0)
      emit(new Stm.Astore(index));
    else
      emit(new Stm.Istore(index));

    return;
  }

  @Override
  public void visit(ast.Ast.Stm.AssignArray s)
  {
  }

  @Override
  public void visit(ast.Ast.Stm.Block s)
  {
  }

  @Override
  public void visit(ast.Ast.Stm.If s)
  {
    Label tl = new Label(), fl = new Label(), el = new Label();
    s.condition.accept(this);
    emit(new Stm.Ifne(tl));
    emit(new Stm.LabelJ(fl));
    s.elsee.accept(this);
    emit(new Stm.Goto(el));
    emit(new Stm.LabelJ(tl));
    s.thenn.accept(this);
    emit(new Stm.Goto(el));
    emit(new Stm.LabelJ(el));
    return;
  }

  @Override
  public void visit(ast.Ast.Stm.Print s)
  {
    s.exp.accept(this);
    emit(new Stm.Print());
    return;
  }

  @Override
  public void visit(ast.Ast.Stm.While s)
  {
  }

  // type
  @Override
  public void visit(ast.Ast.Type.Boolean t)
  {
  }

  @Override
  public void visit(ast.Ast.Type.ClassType t)
  {
  }

  @Override
  public void visit(ast.Ast.Type.Int t)
  {
    this.type = new Type.Int();
  }

  @Override
  public void visit(ast.Ast.Type.IntArray t)
  {
  }

  // dec
  @Override
  public void visit(ast.Ast.Dec.DecSingle d)
  {
    d.type.accept(this);
    this.dec = new Dec.DecSingle(this.type, d.id);
    this.indexTable.put(d.id, index++);
    return;
  }

  // method
  @Override
  public void visit(ast.Ast.Method.MethodSingle m)
  {
    // record, in a hash table, each var's index
    // this index will be used in the load store operation
    this.index = 1;
    this.indexTable = new java.util.Hashtable<String, Integer>();

    m.retType.accept(this);
    Type.T newRetType = this.type;
    java.util.LinkedList<Dec.T> newFormals = new java.util.LinkedList<Dec.T>();
    for (ast.Ast.Dec.T d : m.formals) {
      d.accept(this);
      newFormals.add(this.dec);
    }
    java.util.LinkedList<Dec.T> locals = new java.util.LinkedList<Dec.T>();
    for (ast.Ast.Dec.T d : m.locals) {
      d.accept(this);
      locals.add(this.dec);
    }
    this.stms = new java.util.LinkedList<Stm.T>();
    for (ast.Ast.Stm.T s : m.stms) {
      s.accept(this);
    }

    // return statement is specially treated
    m.retExp.accept(this);

    if (m.retType.getNum() > 0)
      emit(new Stm.Areturn());
    else
      emit(new Stm.Ireturn());

    this.method = new Method.MethodSingle(newRetType, m.id,
        this.classId, newFormals, locals, this.stms, 0, this.index);

    return;
  }

  // class
  @Override
  public void visit(ast.Ast.Class.ClassSingle c)
  {
    this.classId = c.id;
    java.util.LinkedList<Dec.T> newDecs = new java.util.LinkedList<Dec.T>();
    for (ast.Ast.Dec.T dec : c.decs) {
      dec.accept(this);
      newDecs.add(this.dec);
    }
    java.util.LinkedList<Method.T> newMethods = new java.util.LinkedList<Method.T>();
    for (ast.Ast.Method.T m : c.methods) {
      m.accept(this);
      newMethods.add(this.method);
    }
    this.classs = new Class.ClassSingle(c.id, c.extendss, newDecs,
        newMethods);
    return;
  }

  // main class
  @Override
  public void visit(ast.Ast.MainClass.MainClassSingle c)
  {
    //c.stm.accept(this);
    this.mainClass = new MainClass.MainClassSingle(c.id, c.arg,
        this.stms);
    this.stms = new java.util.LinkedList<Stm.T>();
    return;
  }

  // program
  @Override
  public void visit(ast.Ast.Program.ProgramSingle p)
  {
    // do translations
    p.mainClass.accept(this);

    java.util.LinkedList<Class.T> newClasses = new java.util.LinkedList<Class.T>();
    for (ast.Ast.Class.T classs : p.classes) {
      classs.accept(this);
      newClasses.add(this.classs);
    }
    this.program = new Program.ProgramSingle(this.mainClass,
        newClasses);
    return;
  }
}
