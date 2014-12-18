package control;

<<<<<<< HEAD
public class Control
{
  // the lexer
  public static class ConLexer
  {
    public static boolean test = false;
    public static boolean dump = false;
  }
=======
public class Control
{
  // compiler testing and debugging
  public static boolean testlexer = false;
  public static boolean testFac = false;
>>>>>>> Lab2
  
  // lexer and parser
  public static boolean lex = false;
  
  // ast
  public static boolean dumpAst = false;

<<<<<<< HEAD
    // elaborator
    public static boolean elabClassTable = false;
    public static boolean elabMethodTable = false;
  }
  
  public static class ConCodeGen
  {
    public static String fileName = null;

    public static String outputName = null;

    public static enum Kind_t {
      Bytecode, C, Dalvik, X86
    }

    public static Kind_t codegen = Kind_t.C;
  }
=======
  // elaborator
  public static boolean elabClassTable = false;
  public static boolean elabMethodTable = false;
>>>>>>> Lab2
}
