package ast;

public class PrettyPrintVisitor implements Visitor {
	private int indentLevel;

	public PrettyPrintVisitor() {
		this.indentLevel = 4;
	}

	private void indent() {
		this.indentLevel += 2;
	}

	private void unIndent() {
		this.indentLevel -= 2;
	}

	private void printSpaces() {
		int i = this.indentLevel;
		while (i-- != 0)
			this.say(" ");
	}

	private void sayln(String s) {
		System.out.println(s);
	}

	private void say(String s) {
		System.out.print(s);
	}

	// /////////////////////////////////////////////////////
	// expressions
	@Override
	public void visit(ast.Ast.Exp.Add e) {
		// Lab2, exercise4: filling in missing code.
		// Similar for other methods with empty bodies.
		// Your code here:
		e.left.accept(this);
		this.say(" + ");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.And e) {
		e.left.accept(this);
		this.say(" && ");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.ArraySelect e) {
		e.array.accept(this);
		this.say("[ ");
		e.index.accept(this);
		this.say(" ]");
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Call e) {
		e.exp.accept(this);
		this.say("." + e.id + "(");
		int size = e.args.size();
		for (ast.Ast.Exp.T x : e.args) {
			x.accept(this);
			size--;
			if (0 < size) {
				this.say(", ");
			}
		}
		this.say(")");
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.False e) {
		this.say("false");
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Id e) {
		this.say(e.id);
	}

	@Override
	public void visit(ast.Ast.Exp.Length e) {
		e.array.accept(this);
		this.say(".length");
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Lt e) {
		e.left.accept(this);
		this.say(" < ");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.NewIntArray e) {
		this.say("new int[ ");
		e.exp.accept(this);
		this.say(" ]");
		return;
		// int[] ii = new int[3];
	}

	@Override
	public void visit(ast.Ast.Exp.NewObject e) {
		this.say("new " + e.id + "()");
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Not e) {
		this.say("!(");
		e.exp.accept(this);
		this.say(")");
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Num e) {
		System.out.print(e.num);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.Sub e) {
		e.left.accept(this);
		this.say(" - ");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.This e) {
		this.say("this");
	}

	@Override
	public void visit(ast.Ast.Exp.Times e) {
		e.left.accept(this);
		this.say(" * ");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(ast.Ast.Exp.True e) {
		this.say("true ");
		return;
	}

	// statements
	@Override
	public void visit(ast.Ast.Stm.Assign s) {
		this.printSpaces();
		this.say(s.id + " = ");
		s.exp.accept(this);
		this.sayln(";");
		return;
	}

	@Override
	public void visit(ast.Ast.Stm.AssignArray s) {
		this.printSpaces();
		this.say(s.id + "[");
		s.index.accept(this);
		this.say("] = ");
		s.exp.accept(this);
		this.sayln(";");
		return;
	}

	@Override
	public void visit(ast.Ast.Stm.Block s) {
		this.printSpaces();
		this.sayln("{");
		this.indent();
		for (ast.Ast.Stm.T stm : s.stms)
			stm.accept(this);
		this.unIndent();
		this.printSpaces();
		this.sayln("}");
	}

	@Override
	public void visit(ast.Ast.Stm.If s) {
		this.printSpaces();
		this.say("if (");
		s.condition.accept(this);
		this.sayln(")");
		this.indent();
		s.thenn.accept(this);
		this.unIndent();
		this.printSpaces();
		this.sayln("else");
		this.indent();
		s.elsee.accept(this);
		this.sayln("");
		this.unIndent();
		return;
	}

	@Override
	public void visit(ast.Ast.Stm.Print s) {
		this.printSpaces();
		this.say("System.out.println (");
		s.exp.accept(this);
		this.sayln(");");
		return;
	}

	@Override
	public void visit(ast.Ast.Stm.While s) {
		this.printSpaces();
		this.say("while (");
		s.condition.accept(this);
		this.sayln(")");
		s.body.accept(this);
		return;
	}

	// type
	@Override
	public void visit(ast.Ast.Type.Boolean t) {
		this.say("Boolean");
		return;
	}

	@Override
	public void visit(ast.Ast.Type.ClassType t) {
		this.say(t.id);
		return;
	}

	@Override
	public void visit(ast.Ast.Type.Int t) {
		this.say("int");
		return;
	}

	@Override
	public void visit(ast.Ast.Type.IntArray t) {
		this.say("int[]");
		return;
	}

	// dec
	@Override
	public void visit(ast.Ast.Dec.DecSingle d) {
		d.type.accept(this);
		this.say(" " + d.id);
		return;
	}

	// method
	@Override
	public void visit(ast.Ast.Method.MethodSingle m) {
		this.say("  public ");
		m.retType.accept(this);
		this.say(" " + m.id + "(");
		int size = m.formals.size();
		for (ast.Ast.Dec.T d : m.formals) {
			ast.Ast.Dec.DecSingle dec = (ast.Ast.Dec.DecSingle) d;
			dec.accept(this);
			// dec.type.accept(this);
			// this.say(" " + dec.id);
			size--;
			if (0 < size) {
				this.say(", ");
			}
		}
		this.sayln(")");
		this.sayln("  {");

		for (ast.Ast.Dec.T d : m.locals) {
			ast.Ast.Dec.DecSingle dec = (ast.Ast.Dec.DecSingle) d;
			this.say("    ");
			dec.accept(this);
			// dec.type.accept(this);
			// this.say(" " + dec.id + ";\n");
			this.say(";\n");
		}
		this.sayln("");
		for (ast.Ast.Stm.T s : m.stms)
			s.accept(this);
		this.say("    return ");
		m.retExp.accept(this);
		this.sayln(";");
		this.sayln("  }");
		return;
	}

	// class
	@Override
	public void visit(ast.Ast.Class.ClassSingle c) {
		this.say("class " + c.id);
		if (c.extendss != null)
			this.sayln(" extends " + c.extendss);
		else
			this.sayln("");

		this.sayln("{");

		for (ast.Ast.Dec.T d : c.decs) {
			ast.Ast.Dec.DecSingle dec = (ast.Ast.Dec.DecSingle) d;
			this.say("  ");
			dec.accept(this);
			// dec.type.accept(this);
			// this.say(" ");
			this.sayln(";");
		}
		for (ast.Ast.Method.T mthd : c.methods)
			mthd.accept(this);
		this.sayln("}");
		return;
	}

	// main class
	@Override
	public void visit(ast.Ast.MainClass.MainClassSingle c) {
		this.sayln("class " + c.id);
		this.sayln("{");
		this.sayln("  public static void main (String [] " + c.arg + ")");
		this.sayln("  {");
		c.stms.accept(this);
		this.sayln("  }");
		this.sayln("}");
		return;
	}

	// program
	@Override
	public void visit(ast.Ast.Program.ProgramSingle p) {
		p.mainClass.accept(this);
		this.sayln("");
		for (ast.Ast.Class.T classs : p.classes) {
			classs.accept(this);
		}
		System.out.println("\n");
	}
}
