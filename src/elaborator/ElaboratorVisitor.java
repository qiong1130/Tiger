package elaborator;

<<<<<<< HEAD
import java.util.LinkedList;

import ast.Ast.Class;
import ast.Ast.Class.ClassSingle;
import ast.Ast.Dec;
import ast.Ast.Exp;
import ast.Ast.Exp.Add;
import ast.Ast.Exp.And;
import ast.Ast.Exp.ArraySelect;
import ast.Ast.Exp.Call;
import ast.Ast.Exp.False;
import ast.Ast.Exp.Id;
import ast.Ast.Exp.Length;
import ast.Ast.Exp.Lt;
import ast.Ast.Exp.NewIntArray;
import ast.Ast.Exp.NewObject;
import ast.Ast.Exp.Not;
import ast.Ast.Exp.Num;
import ast.Ast.Exp.Sub;
import ast.Ast.Exp.This;
import ast.Ast.Exp.Times;
import ast.Ast.Exp.True;
import ast.Ast.MainClass;
import ast.Ast.Method;
import ast.Ast.Method.MethodSingle;
import ast.Ast.Program.ProgramSingle;
import ast.Ast.Stm;
import ast.Ast.Stm.Assign;
import ast.Ast.Stm.AssignArray;
import ast.Ast.Stm.Block;
import ast.Ast.Stm.If;
import ast.Ast.Stm.Print;
import ast.Ast.Stm.While;
import ast.Ast.Type;
import ast.Ast.Type.ClassType;
import control.Control.ConAst;

public class ElaboratorVisitor implements ast.Visitor
{
  public ClassTable classTable; // symbol table for class
  public MethodTable methodTable; // symbol table for each method
  public String currentClass; // the class name being elaborated
  public Type.T type; // type of the expression being elaborated

  public ElaboratorVisitor()
  {
    this.classTable = new ClassTable();
    this.methodTable = new MethodTable();
    this.currentClass = null;
    this.type = null;
  }

  private void error()
  {
    System.out.println("type mismatch");
    System.exit(1);
  }

  // /////////////////////////////////////////////////////
  // expressions
  @Override
  public void visit(Add e)
  {
  }

  @Override
  public void visit(And e)
  {
  }

  @Override
  public void visit(ArraySelect e)
  {
  }

  @Override
  public void visit(Call e)
  {
    Type.T leftty;
    Type.ClassType ty = null;

    e.exp.accept(this);
    leftty = this.type;
    if (leftty instanceof ClassType) {
      ty = (ClassType) leftty;
      e.type = ty.id;
    } else
      error();
    MethodType mty = this.classTable.getm(ty.id, e.id);

    java.util.LinkedList<Type.T> declaredArgTypes
    = new java.util.LinkedList<Type.T>();
    for (Dec.T dec: mty.argsType){
      declaredArgTypes.add(((Dec.DecSingle)dec).type);
    }
    java.util.LinkedList<Type.T> argsty = new LinkedList<Type.T>();
    for (Exp.T a : e.args) {
      a.accept(this);
      argsty.addLast(this.type);
    }
    if (declaredArgTypes.size() != argsty.size())
      error();
    // For now, the following code only checks that
    // the types for actual and formal arguments should
    // be the same. However, in MiniJava, the actual type
    // of the parameter can also be a subtype (sub-class) of the 
    // formal type. That is, one can pass an object of type "A"
    // to a method expecting a type "B", whenever type "A" is
    // a sub-class of type "B".
    // Modify the following code accordingly:
    for (int i = 0; i < argsty.size(); i++) {
      Dec.DecSingle dec = (Dec.DecSingle) mty.argsType.get(i);
      if (dec.type.toString().equals(argsty.get(i).toString()))
        ;
      else
        error();
    }
    this.type = mty.retType;
    // the following two types should be the declared types.
    e.at = declaredArgTypes;
    e.rt = this.type;
    return;
  }

  @Override
  public void visit(False e)
  {
  }

  @Override
  public void visit(Id e)
  {
    // first look up the id in method table
    Type.T type = this.methodTable.get(e.id);
    // if search failed, then s.id must be a class field.
    if (type == null) {
      type = this.classTable.get(this.currentClass, e.id);
      // mark this id as a field id, this fact will be
      // useful in later phase.
      e.isField = true;
    }
    if (type == null)
      error();
    this.type = type;
    // record this type on this node for future use.
    e.type = type;
    return;
  }

  @Override
  public void visit(Length e)
  {
  }

  @Override
  public void visit(Lt e)
  {
    e.left.accept(this);
    Type.T ty = this.type;
    e.right.accept(this);
    if (!this.type.toString().equals(ty.toString()))
      error();
    this.type = new Type.Boolean();
    return;
  }

  @Override
  public void visit(NewIntArray e)
  {
  }

  @Override
  public void visit(NewObject e)
  {
    this.type = new Type.ClassType(e.id);
    return;
  }

  @Override
  public void visit(Not e)
  {
  }

  @Override
  public void visit(Num e)
  {
    this.type = new Type.Int();
    return;
  }

  @Override
  public void visit(Sub e)
  {
    e.left.accept(this);
    Type.T leftty = this.type;
    e.right.accept(this);
    if (!this.type.toString().equals(leftty.toString()))
      error();
    this.type = new Type.Int();
    return;
  }

  @Override
  public void visit(This e)
  {
    this.type = new Type.ClassType(this.currentClass);
    return;
  }

  @Override
  public void visit(Times e)
  {
    e.left.accept(this);
    Type.T leftty = this.type;
    e.right.accept(this);
    if (!this.type.toString().equals(leftty.toString()))
      error();
    this.type = new Type.Int();
    return;
  }

  @Override
  public void visit(True e)
  {
  }

  // statements
  @Override
  public void visit(Assign s)
  {
    // first look up the id in method table
    Type.T type = this.methodTable.get(s.id);
    // if search failed, then s.id must
    if (type == null)
      type = this.classTable.get(this.currentClass, s.id);
    if (type == null)
      error();
    s.exp.accept(this);
    s.type = type;
    this.type.toString().equals(type.toString());
    return;
  }

  @Override
  public void visit(AssignArray s)
  {
  }

  @Override
  public void visit(Block s)
  {
  }

  @Override
  public void visit(If s)
  {
    s.condition.accept(this);
    if (!this.type.toString().equals("@boolean"))
      error();
    s.thenn.accept(this);
    s.elsee.accept(this);
    return;
  }

  @Override
  public void visit(Print s)
  {
    s.exp.accept(this);
    if (!this.type.toString().equals("@int"))
      error();
    return;
  }

  @Override
  public void visit(While s)
  {
  }

  // type
  @Override
  public void visit(Type.Boolean t)
  {
  }

  @Override
  public void visit(Type.ClassType t)
  {
  }

  @Override
  public void visit(Type.Int t)
  {
    System.out.println("aaaa");
  }

  @Override
  public void visit(Type.IntArray t)
  {
  }

  // dec
  @Override
  public void visit(Dec.DecSingle d)
  {
  }

  // method
  @Override
  public void visit(Method.MethodSingle m)
  {
    // construct the method table
    this.methodTable.put(m.formals, m.locals);

    if (ConAst.elabMethodTable)
      this.methodTable.dump();

    for (Stm.T s : m.stms)
      s.accept(this);
    m.retExp.accept(this);
    return;
  }

  // class
  @Override
  public void visit(Class.ClassSingle c)
  {
    this.currentClass = c.id;

    for (Method.T m : c.methods) {
      m.accept(this);
    }
    return;
  }

  // main class
  @Override
  public void visit(MainClass.MainClassSingle c)
  {
    this.currentClass = c.id;
    // "main" has an argument "arg" of type "String[]", but
    // one has no chance to use it. So it's safe to skip it...

    c.stm.accept(this);
    return;
  }

  // ////////////////////////////////////////////////////////
  // step 1: build class table
  // class table for Main class
  private void buildMainClass(MainClass.MainClassSingle main)
  {
    this.classTable.put(main.id, new ClassBinding(null));
  }

  // class table for normal classes
  private void buildClass(ClassSingle c)
  {
    this.classTable.put(c.id, new ClassBinding(c.extendss));
    for (Dec.T dec : c.decs) {
      Dec.DecSingle d = (Dec.DecSingle) dec;
      this.classTable.put(c.id, d.id, d.type);
    }
    for (Method.T method : c.methods) {
      MethodSingle m = (MethodSingle) method;
      this.classTable.put(c.id, m.id, new MethodType(m.retType, m.formals));
    }
  }

  // step 1: end
  // ///////////////////////////////////////////////////

  // program
  @Override
  public void visit(ProgramSingle p)
  {
    // ////////////////////////////////////////////////
    // step 1: build a symbol table for class (the class table)
    // a class table is a mapping from class names to class bindings
    // classTable: className -> ClassBinding{extends, fields, methods}
    buildMainClass((MainClass.MainClassSingle) p.mainClass);
    for (Class.T c : p.classes) {
      buildClass((ClassSingle) c);
    }

    // we can double check that the class table is OK!
    if (control.Control.ConAst.elabClassTable) {
      this.classTable.dump();
    }

    // ////////////////////////////////////////////////
    // step 2: elaborate each class in turn, under the class table
    // built above.
    p.mainClass.accept(this);
    for (Class.T c : p.classes) {
      c.accept(this);
    }

  }
=======
import java.util.Hashtable;

import ast.Ast.Type.ClassType;

public class ElaboratorVisitor implements ast.Visitor {
	public enum ErrorType {
		MISMATCH, UNDECLARED, NOT_INT, NOT_BOOL, NOT_ARRAY, NOT_INDEX, NOT_CLASS, ARG_NUM, BAD_ARG, BAD_CALL
	}

	public ClassTable classTable; // symbol table for class
	public Hashtable<String, MethodTable> methodTable; // symbol table for each
														// method
	public String currentClass; // the class name being elaborated
	public String currentMethod;
	public ast.Ast.Type.T type; // type of the expression being elaborated
	public java.util.Set<String> usedVariable;

	public ElaboratorVisitor() {
		this.classTable = new ClassTable();
		this.methodTable = new Hashtable<String, MethodTable>();
		this.currentClass = null;
		this.currentMethod = null;
		this.usedVariable = null;
		this.type = null;
	}

	// error report
	private void error(ErrorType errorType, int lineNumber, String info) {
		System.out.print("Error: at line " + lineNumber + ", ");
		switch (errorType) {
		case NOT_INT:
			System.out.println(info + "should be type of int.");
			break;
		case NOT_BOOL:
			System.out.println(info + "should be type of boolean.");
			break;
		case NOT_ARRAY:
			System.out.println(info + " should be type of int array.");
			break;
		case NOT_INDEX:
			System.out
					.println("the expression in the [] should be type of int.");
			break;
		case NOT_CLASS:
			System.out.println(info + " should be a object.");
			break;
		case ARG_NUM:
			System.out.println("inconsistent argument number.");
			break;
		case BAD_ARG:
			System.out.println("bad argument,");
			System.out.println("\t" + info);
			break;
		case UNDECLARED:
			System.out.println("object or method " + info + " is undeclared.");
			break;
		case MISMATCH:
			System.out.println("in assign statement,");
			System.out.println("\t" + info);
			break;
		case BAD_CALL:
			System.out.println("int[] array does not has this method:");
			System.out.println("\t" + info);
			break;
		default:
			System.out.println("unknown error.");
		}
		// System.exit(1);
		return;
		// throw new java.lang.Error();
	}

	// not-used warning
	private void warning(String var, java.util.LinkedList<ast.Ast.Dec.T> list) {
		for (ast.Ast.Dec.T d : list) {
			ast.Ast.Dec.DecSingle decc = ((ast.Ast.Dec.DecSingle) d);
			if (decc.id == var) {
				System.out.println("Warning: at line " + decc.lineNumber
						+ ", variable " + decc.id
						+ " is declared but never used.");
			}
		}
	}

	// /////////////////////////////////////////////////////
	// expressions
	@Override
	public void visit(ast.Ast.Exp.Add e) {
		e.left.accept(this);
		if (!(this.type instanceof ast.Ast.Type.Int))
			error(ErrorType.NOT_INT, e.lineNumber,
					"leftside of the plus operation ");
		e.right.accept(this);
		if (!(this.type instanceof ast.Ast.Type.Int))
			error(ErrorType.NOT_INT, e.lineNumber,
					"rightside of the plus operation ");
		this.type = new ast.Ast.Type.Int();
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.And e) {
		e.left.accept(this);
		if (!(this.type instanceof ast.Ast.Type.Boolean))
			error(ErrorType.NOT_BOOL, e.lineNumber,
					"leftside of the and operation ");
		e.right.accept(this);
		if (!(this.type instanceof ast.Ast.Type.Boolean))
			error(ErrorType.NOT_BOOL, e.lineNumber,
					"rightside of the and operation ");
		this.type = new ast.Ast.Type.Boolean();
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.ArraySelect e) {
		e.array.accept(this);
		ast.Ast.Type.T arrayType = this.type;
		if (!(arrayType instanceof ast.Ast.Type.IntArray))
			error(ErrorType.NOT_ARRAY, e.lineNumber, e.array.toString());

		e.index.accept(this);
		ast.Ast.Type.T indexType = this.type;
		if (!(indexType instanceof ast.Ast.Type.Int))
			error(ErrorType.NOT_INDEX, e.lineNumber, "");

		this.type = new ast.Ast.Type.Int();
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Call e) {
		ast.Ast.Type.T leftty;
		ast.Ast.Type.ClassType ty = null;

		e.exp.accept(this);
		leftty = this.type;
		if (leftty instanceof ast.Ast.Type.ClassType) {
			ty = (ast.Ast.Type.ClassType) leftty;
			e.type = ty.id;
		} else {
			error(ErrorType.NOT_CLASS, e.lineNumber, e.exp.toString());
			return;
		}
		MethodType mty = this.classTable.getm(ty.id, e.id);
		if (mty == null) {
			error(ErrorType.UNDECLARED, e.lineNumber, e.id + " of class "
					+ ty.id + " ");
			this.type = new ast.Ast.Type.Int();
			return;
		}
		java.util.LinkedList<ast.Ast.Type.T> argsty = new java.util.LinkedList<ast.Ast.Type.T>();

		for (ast.Ast.Exp.T a : e.args) {
			a.accept(this);
			argsty.addLast(this.type);
		}
		if (mty.argsType.size() != argsty.size()) {
			error(ErrorType.ARG_NUM, e.lineNumber, "");
			this.type = mty.retType;
			return;
		}
		for (int i = 0; i < argsty.size(); i++) {
			ast.Ast.Dec.DecSingle dec = (ast.Ast.Dec.DecSingle) mty.argsType.get(i);
			if (argsty.get(i) == null) {
				error(ErrorType.BAD_ARG, e.lineNumber, "");
				this.type = mty.retType;
				return;
			}
			if (dec.type.toString().equals(argsty.get(i).toString()))
				;
			else if (classTable.isSubClass(argsty.get(i).toString(),
					dec.type.toString()))
				;
			else
				error(ErrorType.BAD_ARG, e.lineNumber, e.args.get(i).toString()
						+ " is not type of " + dec.type.toString()
						+ " or its subclass.");
		}
		this.type = mty.retType;
		e.at = argsty;
		e.rt = this.type;
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.False e) {
		this.type = new ast.Ast.Type.Boolean();
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Id e) {
		// first look up the id in method table
		ast.Ast.Type.T type = this.methodTable.get(this.currentMethod).get(e.id);
		// if search failed, then s.id must be a class field.
		if (type == null) {
			type = this.classTable.get(this.currentClass, e.id);
			// mark this id as a field id, this fact will be
			// useful in later phase.
			e.isField = true;
		}
		if (type == null)
			error(ErrorType.UNDECLARED, e.lineNumber, e.toString());
		this.usedVariable.add(e.id);
		this.type = type;
		// record this type on this node for future use.
		e.type = type;
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Length e) {
		e.array.accept(this);
		ast.Ast.Type.T arrayType = this.type;

		if (!(arrayType instanceof ast.Ast.Type.IntArray))
			error(ErrorType.NOT_ARRAY, e.lineNumber, e.array.toString());
		this.type = new ast.Ast.Type.Int();
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Lt e) {
		e.left.accept(this);
		if (!(this.type instanceof ast.Ast.Type.Int))
			error(ErrorType.NOT_INT, e.lineNumber,
					"leftside of the lessthan operation ");
		e.right.accept(this);
		if (!(this.type instanceof ast.Ast.Type.Int))
			error(ErrorType.NOT_INT, e.lineNumber,
					"rightside of the lessthan operation ");
		this.type = new ast.Ast.Type.Boolean();
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.NewIntArray e) {
		e.exp.accept(this);
		if (!(this.type instanceof ast.Ast.Type.Int))
			error(ErrorType.NOT_INDEX, e.lineNumber, "");
		this.type = new ast.Ast.Type.IntArray();
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.NewObject e) {
		if (this.classTable.get(e.id) == null)
			error(ErrorType.UNDECLARED, e.lineNumber, e.id);
		this.type = new ast.Ast.Type.ClassType(e.id);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Not e) {
		e.exp.accept(this);
		if (!this.type.toString().equals("boolean"))
			error(ErrorType.NOT_BOOL, e.lineNumber,
					"the expression after \'!\' ");
		this.type = new ast.Ast.Type.Boolean();
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Num e) {
		this.type = new ast.Ast.Type.Int();
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Sub e) {
		e.left.accept(this);
		if (!(this.type instanceof ast.Ast.Type.Int))
			error(ErrorType.NOT_INT, e.lineNumber,
					"leftside of the minus operation ");
		e.right.accept(this);
		if (!(this.type instanceof ast.Ast.Type.Int))
			error(ErrorType.NOT_INT, e.lineNumber,
					"rightside of the minus operation ");
		this.type = new ast.Ast.Type.Int();
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.This e) {
		this.type = new ast.Ast.Type.ClassType(this.currentClass);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Times e) {
		e.left.accept(this);
		if (!(this.type instanceof ast.Ast.Type.Int))
			error(ErrorType.NOT_INT, e.lineNumber,
					"leftside of the times operation ");
		e.right.accept(this);
		if (!(this.type instanceof ast.Ast.Type.Int))
			error(ErrorType.NOT_INT, e.lineNumber,
					"rightside of the times operation ");
		this.type = new ast.Ast.Type.Int();
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.True e) {
		this.type = new ast.Ast.Type.Boolean();
		return;
	}

	// statements
	@Override
	public void visit(ast.Ast.Stm.Assign s) {
		// first look up the id in method table
		ast.Ast.Type.T type = this.methodTable.get(this.currentMethod).get(s.id);
		// if search failed, then s.id must
		if (type == null)
			type = this.classTable.get(this.currentClass, s.id);
		if (type == null)
			error(ErrorType.UNDECLARED, s.exp.lineNumber, s.id);
		s.exp.accept(this);
		if (this.type.toString().equals(type.toString()))
			;
		else if (this.type instanceof ast.Ast.Type.ClassType
				&& type instanceof ast.Ast.Type.ClassType
				&& classTable.isSubClass(this.type.toString(), type.toString()))
			;
		else
			error(ErrorType.MISMATCH, s.exp.lineNumber, s.exp.toString()
					+ " is not type of " + type.toString()
					+ " or its subclass.");
		this.usedVariable.add(s.id);
		s.type = this.type;
		return;
	}

	@Override
	public void visit(ast.Ast.Stm.AssignArray s) {
		ast.Ast.Type.T type = this.methodTable.get(this.currentMethod).get(s.id);
		if (type == null)
			type = this.classTable.get(this.currentClass, s.id);
		if (type == null)
			error(ErrorType.UNDECLARED, s.index.lineNumber, s.id);
		if (!(type instanceof ast.Ast.Type.IntArray))
			error(ErrorType.NOT_ARRAY, s.index.lineNumber, s.id);
		s.index.accept(this);
		if (!(this.type instanceof ast.Ast.Type.Int))
			error(ErrorType.NOT_INDEX, s.index.lineNumber, s.index.toString());
		s.exp.accept(this);
		if (!(this.type instanceof ast.Ast.Type.Int))
			error(ErrorType.NOT_INT, s.exp.lineNumber,
					"the right side of the assign" + " statement ");
		this.usedVariable.add(s.id);
		this.type = new ast.Ast.Type.Int();
		return;
	}

	@Override
	public void visit(ast.Ast.Stm.Block s) {
		for (ast.Ast.Stm.T stm : s.stms)
			stm.accept(this);
		return;
	}

	@Override
	public void visit(ast.Ast.Stm.If s) {
		s.condition.accept(this);
		if (!this.type.toString().equals("boolean"))
			error(ErrorType.NOT_BOOL, s.condition.lineNumber,
					"the expression in if() ");
		s.thenn.accept(this);
		s.elsee.accept(this);
		return;
	}

	@Override
	public void visit(ast.Ast.Stm.Print s) {
		s.exp.accept(this);
		if (!this.type.toString().equals("int"))
			error(ErrorType.NOT_INT, s.exp.lineNumber, "the expression"
					+ " in System.out.println() ");
		return;
	}

	@Override
	public void visit(ast.Ast.Stm.While s) {
		s.condition.accept(this);
		if (!this.type.toString().equals("boolean"))
			error(ErrorType.NOT_BOOL, s.condition.lineNumber,
					"the expression in while() ");
		s.body.accept(this);
		return;
	}

	// type
	@Override
	public void visit(ast.Ast.Type.Boolean t) {
		this.type = new ast.Ast.Type.Boolean();
		return;
	}
	
	public void visit(ast.Ast.Type.ClassType t) {
	}

	@Override
	public void visit(ast.Ast.Type.Int t) {
		// System.out.println("aaaa");
		this.type = new ast.Ast.Type.Int();
		return;
	}

	@Override
	public void visit(ast.Ast.Type.IntArray t) {
		this.type = new ast.Ast.Type.IntArray();
		return;
	}

	// dec
	@Override
	public void visit(ast.Ast.Dec.DecSingle d) {
		this.classTable.put(currentClass, d.id, d.type);
		return;
	}

	// method
	@Override
	public void visit(ast.Ast.Method.MethodSingle m) {
		// construct the method table
		MethodTable mt = new MethodTable();
		mt.put(m.formals, m.locals);
		this.methodTable.put(m.id, mt);

		java.util.Set<String> toUseVariable = this.methodTable.get(m.id)
				.getTable().keySet();
		usedVariable = new java.util.LinkedHashSet<String>();
		if (control.Control.elabMethodTable) {
			System.out.println("method " + m.id + "() has these variables:");
			this.methodTable.get(m.id).dump();
		}
		for (ast.Ast.Stm.T s : m.stms) {
			this.currentMethod = m.id;
			s.accept(this);
		}
		m.retExp.accept(this);
		toUseVariable.removeAll(usedVariable);
		int notUsedSize = toUseVariable.size();
		if (notUsedSize > 0) {
			for (String var : toUseVariable) {
				warning(var, m.locals);
				warning(var, m.formals);
			}
		}
		System.out
				.println("------------------------------------------------------------------------\n");
		return;
	}

	// class
	@Override
	public void visit(ast.Ast.Class.ClassSingle c) {
		this.currentClass = c.id;

		for (ast.Ast.Method.T m : c.methods) {
			m.accept(this);
		}
		return;
	}

	// main class
	@Override
	public void visit(ast.Ast.MainClass.MainClassSingle c) {
		this.currentClass = c.id;
		// "main" has an argument "arg" of type "String[]", but
		// one has no chance to use it. So it's safe to skip it...
		//for (ast.Ast.Stm.T stm : c.stms) {
			c.accept(this);
		//}
		return;
	}

	// ////////////////////////////////////////////////////////
	// step 1: build class table
	// class table for Main class
	private void buildMainClass(ast.Ast.MainClass.MainClassSingle main) {
		this.classTable.put(main.id, new ClassBinding(null));
	}

	// class table for normal classes
	private void buildClass(ast.Ast.Class.ClassSingle c) {
		this.classTable.put(c.id, new ClassBinding(c.extendss));
		for (ast.Ast.Dec.T dec : c.decs) {
			ast.Ast.Dec.DecSingle d = (ast.Ast.Dec.DecSingle) dec;
			this.classTable.put(c.id, d.id, d.type);
		}
		for (ast.Ast.Method.T method : c.methods) {
			ast.Ast.Method.MethodSingle m = (ast.Ast.Method.MethodSingle) method;
			this.classTable.put(c.id, m.id,
					new MethodType(m.retType, m.formals));
		}
	}

	// step 1: end
	// ///////////////////////////////////////////////////

	// program
	@Override
	public void visit(ast.Ast.Program.ProgramSingle p) {
		// ////////////////////////////////////////////////
		// step 1: build a symbol table for class (the class table)
		// a class table is a mapping from class names to class bindings
		// classTable: className -> ClassBinding{extends, fields, methods}
		buildMainClass((ast.Ast.MainClass.MainClassSingle) p.mainClass);
		for (ast.Ast.Class.T c : p.classes) {
			buildClass((ast.Ast.Class.ClassSingle) c);
		}

		// we can double check that the class table is OK!
		if (control.Control.elabClassTable) {
			this.classTable.dump();
		}

		// ////////////////////////////////////////////////
		// step 2: elaborate each class in turn, under the class table
		// built above.
		p.mainClass.accept(this);
		for (ast.Ast.Class.T c : p.classes) {
			c.accept(this);
		}

	}

>>>>>>> Lab2
}
