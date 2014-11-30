import static control.Control.ConAst.dumpAst;
import static control.Control.ConAst.testFac;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import ast.Ast.Program;
import lexer.Lexer;
import lexer.Token;
import parser.Parser;
import control.CommandLine;
import control.Control;

public class Tiger
{
  public static void main(String[] args)
  {
    InputStream fstream;
    Parser parser;

    // ///////////////////////////////////////////////////////
    // handle command line arguments
    CommandLine cmd = new CommandLine();
    String fname = cmd.scan(args);
<<<<<<< HEAD

    // /////////////////////////////////////////////////////
    // to test the pretty printer on the "test/Fac.java" program
    if (testFac) {
      System.out.println("Testing the Tiger compiler on Fac.java starting:");
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      ast.Fac.prog.accept(pp);
      System.out.println("Testing the Tiger compiler on Fac.java finished.");
      System.exit(1);
    }

    if (fname == null) {
      cmd.usage();
      return;
    }

=======
 	System.out.println("what is outup?");
    // /////////////////////////////////////////////
    // the straight-line interpreter (and compiler)   
    slp.Main slpmain = new slp.Main();
   switch (Control.ConSlp.action){
    case NONE:
//    	System.out.println("what is outup?");
//      System.exit(0);
      break;
    case ARGS:
    	 slpmain.doit(slp.Samples.prog);
    	 System.exit(0);
//    	break;
   case INTERP:
	   	 slpmain.doit(slp.Samples.prog);
    	 System.exit(0);
//    	break;
    default: 	   	 
//      if (Control.ConSlp.div) {
//        slpmain.doit(slp.Samples.dividebyzero); 
//        System.exit(0);
//      }
//      slpmain.doit(slp.Samples.prog);
//      System.exit(0);
    }  
   if (fname == null) {
       cmd.usage();
       return;
     }
//   	System.out.println("what is outup?");
>>>>>>> Lab1
    // /////////////////////////////////////////////////////
    // it would be helpful to be able to test the lexer
    // independently.
    if (Control.ConLexer.test) {
      System.out.println("Testing the lexer. All tokens:");
      try {
        fstream = new BufferedInputStream(new FileInputStream(fname));
        Lexer lexer = new Lexer(fname, fstream);
        Token token = lexer.nextToken();

        while (token.kind != Token.Kind.TOKEN_EOF) {
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
    Program.T theAst = null;

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
    
    return;
  }
}
