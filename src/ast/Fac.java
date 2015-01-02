package ast;


public class Fac {
	// Lab2, exercise 2: read the following code and make
	// sure you understand how the sample program "test/Fac.java" is
	// represented.

	// /////////////////////////////////////////////////////
	// To represent the "Fac.java" program in memory manually
	// this is for demonstration purpose only, and
	// no one would want to do this in reality (boring and error-prone).
	/*
	 * class Factorial { public static void main(String[] a) {
	 * System.out.println(new Fac().ComputeFac(10)); } } class Fac { public int
	 * ComputeFac(int num) { int num_aux; if (num < 1) num_aux = 1; else num_aux
	 * = num * (this.ComputeFac(num-1)); return num_aux; } }
	 */

	// // main class: "Factorial"
	static ast.Ast.MainClass.MainClassSingle factorial = new ast.Ast.MainClass.MainClassSingle(
			"Factorial", "a",
			new util.Flist<ast.Ast.Stm.T>().addAll(new ast.Ast.Stm.Print(
					new ast.Ast.Exp.Call(new ast.Ast.Exp.NewObject("Fac"),
							"ComputeFac", new util.Flist<ast.Ast.Exp.T>()
									.addAll(new ast.Ast.Exp.Num(10))))));

	
	// // class "Fac"
	static ast.Ast.Class.ClassSingle fac = new ast.Ast.Class.ClassSingle(
			"Fac",
			null,
			new util.Flist<ast.Ast.Dec.T>().addAll(),
			new util.Flist<ast.Ast.Method.T>().addAll(new ast.Ast.Method.MethodSingle(
					new ast.Ast.Type.Int(),
					"ComputeFac",
					new util.Flist<ast.Ast.Dec.T>().addAll(new ast.Ast.Dec.DecSingle(
							new ast.Ast.Type.Int(), "num")),
					new util.Flist<ast.Ast.Dec.T>().addAll(new ast.Ast.Dec.DecSingle(
							new ast.Ast.Type.Int(), "num_aux"),new ast.Ast.Dec.DecSingle(new ast.Ast.Type.IntArray(),"sdd")),
					new util.Flist<ast.Ast.Stm.T>()
							.addAll(new ast.Ast.Stm.If(
									new ast.Ast.Exp.Lt(new ast.Ast.Exp.Id("num"),
											new ast.Ast.Exp.Num(1)),
									new ast.Ast.Stm.Assign("num_aux",
											new ast.Ast.Exp.Num(1)),
									new ast.Ast.Stm.Assign(
											"num_aux",
											new ast.Ast.Exp.Times(
													new ast.Ast.Exp.Id("num"),
													new ast.Ast.Exp.Call(
															new ast.Ast.Exp.This(),
															"ComputeFac",
															new util.Flist<ast.Ast.Exp.T>()
																	.addAll(new ast.Ast.Exp.Sub(
																			new ast.Ast.Exp.Id(
																					"num"),
																			new ast.Ast.Exp.Num(
																					1))))))),
									new ast.Ast.Stm.While(new ast.Ast.Exp.Lt(
											new ast.Ast.Exp.Id("num"),
											new ast.Ast.Exp.Num(1)),
											new ast.Ast.Stm.Assign("num_aux",
													new ast.Ast.Exp.Num(2))),new ast.Ast.Stm.Assign("sdd", new ast.Ast.Exp.NewIntArray(new ast.Ast.Exp.Num(4)))),
					new ast.Ast.Exp.Id("num_aux"))));

	// program
	public static ast.Ast.Program.ProgramSingle prog = new ast.Ast.Program.ProgramSingle(factorial,
			new util.Flist<ast.Ast.Class.T>().addAll(fac));
	// Lab2, exercise 2: you should write some code to
	// represent the program "test/Sum.java".
	// Your code here:

	/*
	 * class Sum { public static void main(String[] a) { System.out.println(new
	 * Doit().doit(101)); } }
	 * 
	 * class Doit { public int doit(int n) { int sum; int i; i = 0; sum = 0;
	 * while (i<n) sum = sum + i; return sum; } }
	 */
	static ast.Ast.MainClass.MainClassSingle sum = new ast.Ast.MainClass.MainClassSingle("Sum",
			"a", new util.Flist<ast.Ast.Stm.T>().addAll(new ast.Ast.Stm.Print(
					new ast.Ast.Exp.Call(new ast.Ast.Exp.NewObject("Doit"), "doit",
							new util.Flist<ast.Ast.Exp.T>().addAll(new ast.Ast.Exp.Num(
									101))))));

	static ast.Ast.Class.ClassSingle doit = new ast.Ast.Class.ClassSingle("Doit", null,
			new util.Flist<ast.Ast.Dec.T>().addAll(),
			new util.Flist<ast.Ast.Method.T>().addAll(new ast.Ast.Method.MethodSingle(
					new ast.Ast.Type.Int(), "doit", new util.Flist<ast.Ast.Dec.T>()
							.addAll(new ast.Ast.Dec.DecSingle(new ast.Ast.Type.Int(), "n")),
					new util.Flist<ast.Ast.Dec.T>().addAll(new ast.Ast.Dec.DecSingle(
							new ast.Ast.Type.Int(), "sum"), new ast.Ast.Dec.DecSingle(
							new ast.Ast.Type.Int(), "i")),
					new util.Flist<ast.Ast.Stm.T>().addAll(new ast.Ast.Stm.Assign("i",
							new ast.Ast.Exp.Num(0)), new ast.Ast.Stm.Assign("sum",
							new ast.Ast.Exp.Num(0)), new ast.Ast.Stm.While(
							new ast.Ast.Exp.Lt(new ast.Ast.Exp.Id("i"), new ast.Ast.Exp.Id(
									"n")), new ast.Ast.Stm.Assign("sum",
									new ast.Ast.Exp.Add(new ast.Ast.Exp.Id("sum"),
											new ast.Ast.Exp.Id("i"))))),
					new ast.Ast.Exp.Id("sum"))));

	// program
	public static ast.Ast.Program.ProgramSingle sum_prog = new ast.Ast.Program.ProgramSingle(sum,
			new util.Flist<ast.Ast.Class.T>().addAll(doit));
}
