import static control.Control.ConAst.testFac;
import static control.Control.ConAst.dumpAst;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import codegen.C.Ast.Program;
import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;
import parser.Parser;
import control.CommandLine;
import control.Control;


public class Tiger {
	public static void main(String[] args) throws IOException {
		InputStream fstream;
		Parser parser;

		// ///////////////////////////////////////////////////////
		// handle command line arguments
		CommandLine cmd = new CommandLine();
		String fname = cmd.scan(args);

		// /////////////////////////////////////////////////////
		// to test the pretty printer on the "test/Fac.java" program
		if (testFac) {
			parser = new Parser();
			System.out
					.println("Testing the Tiger compiler on Fac.java starting:");
			ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
			ast.Fac.prog.accept(pp);

			// elaborate the given program, this step is necessary
			// for that it will annotate the AST with some
			// informations used by later phase.
			elaborator.ElaboratorVisitor elab = new elaborator.ElaboratorVisitor();
			ast.Fac.prog.accept(elab);

			// Compile this program to C.
			System.out.println("code generation starting");
			 // code generation
//			codegen.C.TranslateVisitor trans2C = new codegen.C.TranslateVisitor();
//			// pass this visitor to the "Fac.java" program.
//			ast.Fac.prog.accept(trans2C);
//			// this visitor will return an AST for C.
//			Program.T cast = trans2C.program;
//			// output the AST for C.
//			codegen.C.PrettyPrintVisitor ppc = new codegen.C.PrettyPrintVisitor();
//			cast.accept(ppc);

			// ast.Fac.sum_prog.accept(pp);
			// ast.Fac.prog.accept(pp);
			// ast.Fac.sum_prog.accept(pp);
			 switch (control.Control.ConCodeGen.codegen) {
		      case Bytecode:
		        System.out.println("bytecode codegen");
		        codegen.bytecode.TranslateVisitor trans = new codegen.bytecode.TranslateVisitor();
		        ast.Fac.prog.accept(trans);
		        codegen.bytecode.Ast.Program.T bytecodeAst = trans.program;
		        codegen.bytecode.PrettyPrintVisitor ppbc = new codegen.bytecode.PrettyPrintVisitor();
		        bytecodeAst.accept(ppbc);
		        break;
		      case C:
		        System.out.println("C codegen");
		        codegen.C.TranslateVisitor transC = new codegen.C.TranslateVisitor();
		        ast.Fac.prog.accept(transC);
		        codegen.C.Ast.Program.T cAst = transC.program;
		        codegen.C.PrettyPrintVisitor ppc = new codegen.C.PrettyPrintVisitor();
		        cAst.accept(ppc);
		        break;
		      case Dalvik:
		        // similar
		        break;
		      case X86:
		        // similar
		        break;
		      default:
		        break;
		      }
			System.out
					.println("Testing the Tiger compiler on Fac.java finished.");
			System.exit(1);
		}

		if (fname == null) {
			cmd.usage();
			return;
		}
		Control.ConCodeGen.fileName = fname;

		// /////////////////////////////////////////////////////
		// it would be helpful to be able to test the lexer
		// independently.
		if (Control.ConLexer.test) {
			System.out.println("Testing the lexer. All tokens:");
			try {
				fstream = new BufferedInputStream(new FileInputStream(fname));
				Lexer lexer = new Lexer(fname, fstream);
				Token token = lexer.nextToken();
				while (token.kind != Kind.TOKEN_EOF) {
					System.out.println(token.toString());
					token = lexer.nextToken();
				}
				fstream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.exit(1);
		}

		// /////////////////////////////////////////////////////////
		// normal compilation phases.
		ast.Ast.Program.T theAst = null;

		// parsing the file, get an AST.
		try {
			fstream = new BufferedInputStream(new FileInputStream(fname));
			parser = new Parser(fname, fstream);

			theAst = parser.parse();

			fstream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		// pretty printing the AST, if necessary
		if (dumpAst) {
			ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
			theAst.accept(pp);
		}

		// elaborate the AST, report all possible errors.
		elaborator.ElaboratorVisitor elab = new elaborator.ElaboratorVisitor();
		theAst.accept(elab);

		// code generation
		switch (control.Control.ConCodeGen.codegen) {
		case Bytecode:
			codegen.bytecode.TranslateVisitor trans = new codegen.bytecode.TranslateVisitor();
			theAst.accept(trans);
			codegen.bytecode.Ast.Program.T bytecodeAst = trans.program;
			codegen.bytecode.PrettyPrintVisitor ppbc = new codegen.bytecode.PrettyPrintVisitor();
			bytecodeAst.accept(ppbc);
			break;
		case C:
			codegen.C.TranslateVisitor transC = new codegen.C.TranslateVisitor();
			theAst.accept(transC); 
			
			Program.T cAst = transC.program;
			codegen.C.PrettyPrintVisitor ppc = new codegen.C.PrettyPrintVisitor();
			cAst.accept(ppc);
			break;
		case Dalvik:
			// similar
			break;
		case X86:
			// similar
			break;
		default:
			break;
		}

		// Lab3, exercise 6: add some glue code to
		// call gcc to compile the generated C or x86
		// file, or call java to run the bytecode file.
		// Your code:
	    
		/*String[] cmdArray = { "F:/cygwin/Cygwin.bat","gcc",
				 "Factorial.java.c", "runtime/gc.c",
				"runtime/main.c", "runtime lib.c", "-o",
				"Fac.exe" };
		try {
			Runtime.getRuntime().exec(cmdArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
*/
		Process p = null;
	    BufferedReader br;
	    StringBuffer sb;
	    String temp ;
	    System.out.println("Now Compiling C code using GCC...");
		
		String cmdstr = "gcc -o " + Control.ConCodeGen.fileName + ".out " + Control.ConCodeGen.fileName + ".c " + "runtime/runtime.c";
		//System.out.println(cmdstr);
	   // String cmdstr = "gcc -o " + "a.out " + "a.c " + "runtime/runtime.c";
		p = Runtime.getRuntime().exec(cmdstr);
		br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		sb = new StringBuffer();
		while( ((temp = br.readLine()) != null) ) {
			sb.append(temp + "\n");
		}
		System.out.println(sb);

		System.out.println("Compile ended. Output file is \"" + Control.ConCodeGen.fileName + ".out\". You can run it now.");
		return;
	}
}
