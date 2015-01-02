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
import util.Temp;


// Given a Java ast, translate it into a C ast and outputs it.

public class TranslateVisitor implements ast.Visitor {
	public ClassTable table;
	private String classId;
	private Type.T type; // type after translation
	private Dec.T dec;
	private Stm.T stm;
	private Exp.T exp;
	private Method.T method;
	private java.util.LinkedList<Dec.T> tmpVars;
	private java.util.LinkedList<Class.T> classes;
	private java.util.LinkedList<Vtable.T> vtables;
	private java.util.LinkedList<Method.T> methods;
	private MainMethod.T mainMethod;
	public Program.T program;

	public TranslateVisitor() {
		this.table = new ClassTable();
		this.type = null;
		this.dec = null;
		this.stm = null;
		this.exp = null;
		this.method = null;
		this.classes = new java.util.LinkedList<Class.T>();
		this.vtables = new java.util.LinkedList<Vtable.T>();
		this.methods = new java.util.LinkedList<Method.T>();
		this.mainMethod = null;
		this.program = null;
	}

	// //////////////////////////////////////////////////////
	//
	public String genId() {

		return new Temp().toString();
	}

	// /////////////////////////////////////////////////////
	// expressions
	@Override
	public void visit(ast.Ast.Exp.Add e) {
		e.left.accept(this);
		Exp.T left = this.exp;
		e.right.accept(this);
		Exp.T right = this.exp;
		this.exp = new Exp.Add(left, right);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.And e) {
		e.left.accept(this);
		Exp.T left = this.exp;
		e.right.accept(this);
		Exp.T right = this.exp;
		this.exp = new Exp.And(left, right);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.ArraySelect e) {
		e.array.accept(this);
		Exp.T array = this.exp;
		e.index.accept(this);
		Exp.T index = this.exp;
		this.exp = new Exp.ArraySelect(array, index);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Call e) {
		e.exp.accept(this);
		Exp.T exp = this.exp;
		String newid;
		if (this.exp instanceof Exp.NewObject
				|| this.exp instanceof Exp.Call) {
			newid = this.genId();
			this.tmpVars.add(new Dec.DecSingle(new Type.Class(
					e.type), newid));
		} else
			newid = null;
		java.util.LinkedList<Exp.T> args = new java.util.LinkedList<Exp.T>();
		for (ast.Ast.Exp.T x : e.args) {
			x.accept(this);
			args.add(this.exp);
		}
		this.exp = new Exp.Call(newid, exp, e.id, args);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.False e) {
		this.exp = new Exp.Num(0);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Id e) {
		this.exp = new Exp.Id(e.id);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Length e) {
		e.array.accept(this);
		Exp.T exp = this.exp;
		this.exp = new Exp.Length(exp);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Lt e) {
		e.left.accept(this);
		Exp.T left = this.exp;
		e.right.accept(this);
		Exp.T right = this.exp;
		this.exp = new Exp.Lt(left, right);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.NewIntArray e) {
		e.exp.accept(this);
		Exp.T exp = this.exp;
		this.exp = new Exp.NewIntArray(exp);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.NewObject e) {
		this.exp = new Exp.NewObject(e.id);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Not e) {
		e.exp.accept(this);
		Exp.T exp = this.exp;
		this.exp = new Exp.Not(exp);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Num e) {
		this.exp = new Exp.Num(e.num);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Sub e) {
		e.left.accept(this);
		Exp.T left = this.exp;
		e.right.accept(this);
		Exp.T right = this.exp;
		this.exp = new Exp.Sub(left, right);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.This e) {
		this.exp = new Exp.This();
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Times e) {
		e.left.accept(this);
		Exp.T left = this.exp;
		e.right.accept(this);
		Exp.T right = this.exp;
		this.exp = new Exp.Times(left, right);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.True e) {
		this.exp = new Exp.Num(1);
		return;
	}

	// statements
	@Override
	public void visit(ast.Ast.Stm.Assign s) {
		s.exp.accept(this);
		this.stm = new Stm.Assign(s.id, this.exp);
		return;
	}

	@Override
	public void visit(ast.Ast.Stm.AssignArray s) {
		s.index.accept(this);
		Exp.T index = this.exp;
		s.exp.accept(this);
		Exp.T exp = this.exp;
		this.stm = new Stm.AssignArray(s.id, index, exp);
		return;
	}

	@Override
	public void visit(ast.Ast.Stm.Block s) {
		java.util.LinkedList<Stm.T> stms = new java.util.LinkedList<Stm.T>();
		for (ast.Ast.Stm.T stm : s.stms) {
			stm.accept(this);
			stms.add(this.stm);
		}
		this.stm = new Stm.Block(stms);
		return;
	}

	@Override
	public void visit(ast.Ast.Stm.If s) {
		s.condition.accept(this);
		Exp.T condition = this.exp;
		s.thenn.accept(this);
		Stm.T thenn = this.stm;
		s.elsee.accept(this);
		Stm.T elsee = this.stm;
		this.stm = new Stm.If(condition, thenn, elsee);
		return;
	}

	@Override
	public void visit(ast.Ast.Stm.Print s) {
		s.exp.accept(this);
		this.stm = new Stm.Print(this.exp);
		return;
	}

	@Override
	public void visit(ast.Ast.Stm.While s) {
		s.condition.accept(this);
		Exp.T condition = this.exp;
		s.body.accept(this);
		Stm.T body = this.stm;
		this.stm = new Stm.While(condition, body);
		return;
	}

	// type
	@Override
	public void visit(ast.Ast.Type.Boolean t) {
		this.type = new Type.Int();
		return;
	}

	@Override
	public void visit(ast.Ast.Type.ClassType t) {
		this.type = new Type.Class(t.id);
		return;
	}

	@Override
	public void visit(ast.Ast.Type.Int t) {
		this.type = new Type.Int();
		return;
	}

	@Override
	public void visit(ast.Ast.Type.IntArray t) {
		this.type = new Type.IntArray();
		return;
	}

	// dec
	@Override
	public void visit(ast.Ast.Dec.DecSingle d) {
		d.type.accept(this);
		this.dec = new Dec.DecSingle(this.type, d.id);
		return;
	}

	// method
	@Override
	public void visit(ast.Ast.Method.MethodSingle m) {
		this.tmpVars = new java.util.LinkedList<Dec.T>();
		m.retType.accept(this);
		Type.T newRetType = this.type;
		java.util.LinkedList<Dec.T> newFormals = new java.util.LinkedList<Dec.T>();
		newFormals.add(new Dec.DecSingle(new Type.Class(
				this.classId), "this")); 
		for (ast.Ast.Dec.T d : m.formals) {
			d.accept(this);
			newFormals.add(this.dec);
		}
		java.util.LinkedList<Dec.T> locals = new java.util.LinkedList<Dec.T>();
		for (ast.Ast.Dec.T d : m.locals) {
			d.accept(this);
			locals.add(this.dec);
		}
		java.util.LinkedList<Stm.T> newStm = new java.util.LinkedList<Stm.T>();
		for (ast.Ast.Stm.T s : m.stms) {
			s.accept(this);
			newStm.add(this.stm);
		}
		m.retExp.accept(this);
		Exp.T retExp = this.exp;
		for (Dec.T dec : this.tmpVars) {
			locals.add(dec);
		}
		this.method = new Method.MethodSingle(newRetType, this.classId,
				m.id, newFormals, locals, newStm, retExp);
		return;
	}

	// class
	@Override
	public void visit(ast.Ast.Class.ClassSingle c) {
		ClassBinding cb = this.table.get(c.id);
		this.classes.add(new Class.ClassSingle(c.id, cb.fields));
		this.vtables.add(new Vtable.VtableSingle(c.id, cb.methods));
		this.classId = c.id;
		for (ast.Ast.Method.T m : c.methods) {
			m.accept(this);
			this.methods.add(this.method);
		}
		return;
	}

	// main class
	@Override
	public void visit(ast.Ast.MainClass.MainClassSingle c) {
		ClassBinding cb = this.table.get(c.id);
		Class.T newc = new Class.ClassSingle(c.id, cb.fields);
		this.classes.add(newc);
		this.vtables.add(new Vtable.VtableSingle(c.id, cb.methods));

		this.tmpVars = new java.util.LinkedList<Dec.T>();
		for (ast.Ast.Stm.T m : c.stms) {
			m.accept(this);
		}
		MainMethod.T mthd = new MainMethod.MainMethodSingle(
				this.tmpVars, this.stm);
		this.mainMethod = mthd;
		return;
	}

	// /////////////////////////////////////////////////////
	// the first pass
	public void scanMain(ast.Ast.MainClass.T m) {
		this.table.init(((ast.Ast.MainClass.MainClassSingle) m).id, null);
		// this is a special hacking in that we don't want to
		// enter "main" into the table.
		return;
	}

	public void scanClasses(java.util.LinkedList<ast.Ast.Class.T> cs) {
		// put empty chuncks into the table
		for (ast.Ast.Class.T c : cs) {
			ast.Ast.Class.ClassSingle cc = (ast.Ast.Class.ClassSingle) c;
			this.table.init(cc.id, cc.extendss);
		}

		// put class fields and methods into the table
		for (ast.Ast.Class.T c : cs) {
			ast.Ast.Class.ClassSingle cc = (ast.Ast.Class.ClassSingle) c;
			java.util.LinkedList<Dec.T> newDecs = new java.util.LinkedList<Dec.T>();
			for (ast.Ast.Dec.T dec : cc.decs) {
				dec.accept(this);
				newDecs.add(this.dec);
			}
			this.table.initDecs(cc.id, newDecs);

			// all methods
			java.util.LinkedList<ast.Ast.Method.T> methods = cc.methods;
			for (ast.Ast.Method.T mthd : methods) {
				ast.Ast.Method.MethodSingle m = (ast.Ast.Method.MethodSingle) mthd;
				java.util.LinkedList<Dec.T> newArgs = new java.util.LinkedList<Dec.T>();
				for (ast.Ast.Dec.T arg : m.formals) {
					arg.accept(this);
					newArgs.add(this.dec);
				}
				m.retType.accept(this);
				Type.T newRet = this.type;
				this.table.initMethod(cc.id, newRet, newArgs, m.id);
			}
		}

		// calculate all inheritance information
		for (ast.Ast.Class.T c : cs) {
			ast.Ast.Class.ClassSingle cc = (ast.Ast.Class.ClassSingle) c;
			this.table.inherit(cc.id);
		}
	}

	public void scanProgram(ast.Ast.Program.T p) {
		ast.Ast.Program.ProgramSingle pp = (ast.Ast.Program.ProgramSingle) p;
		scanMain(pp.mainClass);
		scanClasses(pp.classes);
		return;
	}

	// end of the first pass
	// ////////////////////////////////////////////////////

	// program
	@Override
	public void visit(ast.Ast.Program.ProgramSingle p) {
		// The first pass is to scan the whole program "p", and
		// to collect all information of inheritance.
		scanProgram(p);

		// do translations
		p.mainClass.accept(this);
		for (ast.Ast.Class.T classs : p.classes) {
			classs.accept(this);
		}
		this.program = new Program.ProgramSingle(this.classes,
				this.vtables, this.methods, this.mainMethod);
		return;
	}

}
