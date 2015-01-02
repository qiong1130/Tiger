package parser;

import ast.Ast.Stm;
import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;

public class Parser {
	Lexer lexer;
	public static Token current;

	public Parser() {
		lexer = null;
		current = new Token(Kind.TOKEN_ADD, 1);
	}

	public Parser(String fname, java.io.InputStream fstream) {
		lexer = new Lexer(fname, fstream);
		current = lexer.nextToken();
	}

	// /////////////////////////////////////////////
	// utility methods to connect the lexer
	// and the parser.

	private void advance() {
		current = lexer.nextToken();
	}

	private String eatToken(Kind kind) {
		String id = null;
		if (kind == current.kind) {
			id = current.lexeme;
			advance();
		} else {
			System.out.print("at line " + current.lineNum + ",column "
					+ current.columnNum + '\t');
			System.out.print("Expects: " + kind.toString());
			System.out.println(",But got: " + current.kind.toString());
			System.exit(1);
		}
		return id;
	}

	private void error(Token current) {
		System.out.println("Syntax error: compilation aborting...\n");
		System.out.println("Error Information:" + current.kind.toString()
				+ "\tat line " + current.lineNum + ",column "
				+ current.columnNum);
		System.exit(1);
		return;
	}

	// ////////////////////////////////////////////////////////////
	// below are method for parsing.

	// A bunch of parsing methods to parse expressions. The messy
	// parts are to deal with precedence and associativity.

	// ExpList -> Exp ExpRest*
	// ->
	// ExpRest -> , Exp
	private java.util.LinkedList<ast.Ast.Exp.T> parseExpList() {
		java.util.LinkedList<ast.Ast.Exp.T> exp_list = new java.util.LinkedList<ast.Ast.Exp.T>();
		if (current.kind == Kind.TOKEN_RPAREN)
			return exp_list;
		ast.Ast.Exp.T exp = parseExp();
		exp_list.add(exp);
		while (current.kind == Kind.TOKEN_COMMER) {
			advance();
			exp = parseExp();
			exp_list.add(exp);
		}
		return exp_list;
	}

	// AtomExp -> (exp)
	// -> INTEGER_LITERAL
	// -> true
	// -> false
	// -> this
	// -> id
	// -> new int [exp]
	// -> new id ()
	private ast.Ast.Exp.T parseAtomExp() {
		String id = null;
		switch (current.kind) {
		case TOKEN_LPAREN: // TOKEN_LPAREN:"("
			advance();
			ast.Ast.Exp.T exps = parseExp();
			eatToken(Kind.TOKEN_RPAREN); // TOKEN_RPAREN:")"
			return exps;
		case TOKEN_NUM:
			String num = current.lexeme;
			advance();
			return new ast.Ast.Exp.Num(10);
		case TOKEN_FALSE:
			advance();
			return new ast.Ast.Exp.False();
		case TOKEN_TRUE:
			advance();
			return new ast.Ast.Exp.True();
		case TOKEN_THIS:
			advance();
			return new ast.Ast.Exp.This();
		case TOKEN_ID:
			id = current.lexeme;
			advance();
			return new ast.Ast.Exp.Id(id);
		case TOKEN_NEW: {
			advance();
			switch (current.kind) {
			case TOKEN_INT:
				advance();
				eatToken(Kind.TOKEN_LBRACK);
				ast.Ast.Exp.T exp = parseExp();
				eatToken(Kind.TOKEN_RBRACK);
				return new ast.Ast.Exp.NewIntArray(exp);
			case TOKEN_ID:
				id = current.lexeme;
				advance();
				eatToken(Kind.TOKEN_LPAREN);
				eatToken(Kind.TOKEN_RPAREN);
				return new ast.Ast.Exp.NewObject(id);
			default:
				error(current);
				return null;
			}
		}
		default:
			error(current);
			return null;
		}
	}

	// NotExp -> AtomExp
	// -> AtomExp .id (expList)
	// -> AtomExp [exp]
	// -> AtomExp .length
	private ast.Ast.Exp.T parseNotExp() {
		ast.Ast.Exp.T exp = parseAtomExp();
		while (current.kind == Kind.TOKEN_DOT // TOKEN_DOT:"."
				|| current.kind == Kind.TOKEN_LBRACK) { // TOKEN_LBRACK:"["
			if (current.kind == Kind.TOKEN_DOT) {
				advance();
				if (current.kind == Kind.TOKEN_LENGTH) {
					advance();
					return new ast.Ast.Exp.Length(exp);
				}
				String id = eatToken(Kind.TOKEN_ID);
				eatToken(Kind.TOKEN_LPAREN);
				java.util.LinkedList<ast.Ast.Exp.T> args = parseExpList();
				eatToken(Kind.TOKEN_RPAREN);
				return new ast.Ast.Exp.Call(exp, id, args);
			} else {
				advance();
				ast.Ast.Exp.T index = parseExp();
				eatToken(Kind.TOKEN_RBRACK);
				return new ast.Ast.Exp.ArraySelect(exp, index);
			}
		}
		return exp;
	}

	// TimesExp -> ! TimesExp
	// -> NotExp
	private ast.Ast.Exp.T parseTimesExp() {
		// ast.Ast.Exp.T not_exp = parseNotExp();
		ast.Ast.Exp.T times_exp;
		if (current.kind != Kind.TOKEN_NOT) {
			return parseNotExp();
		} else {
			advance();
			times_exp = new ast.Ast.Exp.Not(parseTimesExp());
		}
		return times_exp;
	}

	// AddSubExp -> TimesExp * TimesExp
	// -> TimesExp
	private ast.Ast.Exp.T parseAddSubExp() {
		ast.Ast.Exp.T times_exp = parseTimesExp();
		while (current.kind == Kind.TOKEN_TIMES) {
			advance();
			times_exp = new ast.Ast.Exp.Times(times_exp, parseTimesExp());
		}
		return times_exp;
	}

	// LtExp -> AddSubExp + AddSubExp
	// -> AddSubExp - AddSubExp
	// -> AddSubExp
	private ast.Ast.Exp.T parseLtExp() {
		ast.Ast.Exp.T addsub_exp = parseAddSubExp();
		while (current.kind == Kind.TOKEN_ADD || current.kind == Kind.TOKEN_SUB) {
			if (current.kind == Kind.TOKEN_ADD) {
				advance();
				addsub_exp = new ast.Ast.Exp.Add(addsub_exp, parseAddSubExp());
			} else if (current.kind == Kind.TOKEN_SUB) {
				advance();
				addsub_exp = new ast.Ast.Exp.Sub(addsub_exp, parseAddSubExp());
			}
		}
		return addsub_exp;
	}

	// AndExp -> LtExp < LtExp
	// -> LtExp
	private ast.Ast.Exp.T parseAndExp() {
		ast.Ast.Exp.T lt_exp = parseLtExp();
		while (current.kind == Kind.TOKEN_LT) {
			advance();
			lt_exp = new ast.Ast.Exp.Lt(lt_exp, parseLtExp());
		}
		return lt_exp;
	}

	// Exp -> AndExp && AndExp
	// -> AndExp
	private ast.Ast.Exp.T parseExp() {
		ast.Ast.Exp.T and_exp = parseAndExp();
		while (current.kind == Kind.TOKEN_AND) {
			advance();
			and_exp = new ast.Ast.Exp.And(and_exp, parseAndExp());
		}
		return and_exp;
	}

	// Statement -> { Statement* }
	// -> if ( Exp ) Statement else Statement
	// -> while ( Exp ) Statement
	// -> System.out.println ( Exp ) ;
	// -> id = Exp ;
	// -> id [ Exp ]= Exp ;
	private ast.Ast.Stm.T parseStatement() {
		switch (current.kind) {
		case TOKEN_LBRACE: {
			java.util.LinkedList<Stm.T> stms = new java.util.LinkedList<Stm.T>();
			advance();
			while (current.kind == Kind.TOKEN_LBRACE
					|| current.kind == Kind.TOKEN_IF
					|| current.kind == Kind.TOKEN_WHILE
					|| current.kind == Kind.TOKEN_SYSTEM
					|| current.kind == Kind.TOKEN_ID) {
				ast.Ast.Stm.T stm = parseStatement();
				stms.add(stm);
			}
			eatToken(Kind.TOKEN_RBRACE);
			return new ast.Ast.Stm.Block(stms);
		}
		case TOKEN_IF: {
			advance();
			eatToken(Kind.TOKEN_LPAREN);
			ast.Ast.Exp.T condition = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			ast.Ast.Stm.T thenn = parseStatement();
			eatToken(Kind.TOKEN_ELSE);
			ast.Ast.Stm.T elsee = parseStatement();
			return new ast.Ast.Stm.If(condition, thenn, elsee);
		}
		case TOKEN_WHILE: {
			advance();
			eatToken(Kind.TOKEN_LPAREN);
			ast.Ast.Exp.T condition = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			ast.Ast.Stm.T body = parseStatement();
			return new ast.Ast.Stm.While(condition, body);
		}
		case TOKEN_SYSTEM: {
			advance();
			eatToken(Kind.TOKEN_DOT);
			eatToken(Kind.TOKEN_OUT);
			eatToken(Kind.TOKEN_DOT);
			eatToken(Kind.TOKEN_PRINTLN);
			eatToken(Kind.TOKEN_LPAREN);
			ast.Ast.Exp.T exp = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			ast.Ast.Stm.Print print = new ast.Ast.Stm.Print(exp);
			eatToken(Kind.TOKEN_SEMI);
			return print;
		}
		case TOKEN_ID: {
			String id = current.lexeme;
			advance();
			switch (current.kind) {
			case TOKEN_ASSIGN: {
				advance();
				ast.Ast.Exp.T exp = parseExp();
				ast.Ast.Stm.Assign assign = new ast.Ast.Stm.Assign(id, exp);
				eatToken(Kind.TOKEN_SEMI);
				return assign;
			}
			case TOKEN_LBRACK: {
				advance();
				ast.Ast.Exp.T index = parseExp();
				eatToken(Kind.TOKEN_RBRACK);
				eatToken(Kind.TOKEN_ASSIGN);
				ast.Ast.Exp.T exp = parseExp();
				ast.Ast.Stm.AssignArray assignarray = new ast.Ast.Stm.AssignArray(id,
						index, exp);
				eatToken(Kind.TOKEN_SEMI);
				return assignarray;
			}
			}
		}
		default:
			error(current);
			return null;
		}
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a statement.

	}

	// Statements -> Statement Statements
	// ->
	private java.util.LinkedList<ast.Ast.Stm.T> parseStatements() {
		java.util.LinkedList<ast.Ast.Stm.T> stms = new java.util.LinkedList<ast.Ast.Stm.T>();
		while (current.kind == Kind.TOKEN_LBRACE
				|| current.kind == Kind.TOKEN_IF
				|| current.kind == Kind.TOKEN_WHILE
				|| current.kind == Kind.TOKEN_SYSTEM
				|| current.kind == Kind.TOKEN_ID) {
			ast.Ast.Stm.T stm = parseStatement();
			stms.add(stm);
		}
		return stms;
	}

	// Type -> int []
	// -> boolean
	// -> int
	// -> id
	private ast.Ast.Type.T parseType() {
		switch (current.kind) {
		case TOKEN_INT: {
			advance();
			if (current.kind == Kind.TOKEN_LBRACK) {
				eatToken(Kind.TOKEN_LBRACK);
				eatToken(Kind.TOKEN_RBRACK);
				return new ast.Ast.Type.IntArray();
			}
			return new ast.Ast.Type.Int();
		}
		case TOKEN_BOOLEAN: {
			advance();
			return new ast.Ast.Type.Boolean();
		}
		case TOKEN_ID: {
			String id = current.lexeme;
			advance();
			return new ast.Ast.Type.ClassType(id);
		}
		default:
			error(current);
			return null;
		}
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a type.

	}

	// VarDecl -> Type id ;
	private ast.Ast.Dec.DecSingle parseVarDecl() {
		// to parse the "Type" nonterminal in this method, instead of writing
		// a fresh one.
		ast.Ast.Type.T type = parseType();
		String id = eatToken(Kind.TOKEN_ID);
		ast.Ast.Dec.DecSingle dec = new ast.Ast.Dec.DecSingle(type, id);
		eatToken(Kind.TOKEN_SEMI);
		return dec;
	}

	// VarDecls -> VarDecl VarDecls
	// ->
	private java.util.LinkedList<ast.Ast.Dec.T> parseVarDecls() {
		java.util.LinkedList<ast.Ast.Dec.T> decs = new java.util.LinkedList<ast.Ast.Dec.T>();
		while (current.kind == Kind.TOKEN_INT
				|| current.kind == Kind.TOKEN_BOOLEAN
				|| current.kind == Kind.TOKEN_ID) {
			ast.Ast.Dec.DecSingle dec = parseVarDecl();
			decs.add(dec);
		}
		return decs;
	}

	// FormalList -> Type id FormalRest*
	// ->
	// FormalRest -> , Type id
	private java.util.LinkedList<ast.Ast.Dec.T> parseFormalList() {
		java.util.LinkedList<ast.Ast.Dec.T> formals = new java.util.LinkedList<ast.Ast.Dec.T>();
		if (current.kind == Kind.TOKEN_INT
				|| current.kind == Kind.TOKEN_BOOLEAN
				|| current.kind == Kind.TOKEN_ID) {
			ast.Ast.Type.T type = parseType();
			String id = eatToken(Kind.TOKEN_ID);
			ast.Ast.Dec.T dec = new ast.Ast.Dec.DecSingle(type, id);
			formals.add(dec);
			while (current.kind == Kind.TOKEN_COMMER) {
				advance();
				type = parseType();
				id = eatToken(Kind.TOKEN_ID);
				dec = new ast.Ast.Dec.DecSingle(type, id);
				formals.add(dec);
			}
		}
		return formals;
	}

	// Method -> public Type id ( FormalList )
	// { VarDecl* Statement* return Exp ;}
	private ast.Ast.Method.MethodSingle parseMethod() {
		java.util.LinkedList<ast.Ast.Dec.T> locals = new java.util.LinkedList<ast.Ast.Dec.T>();
		java.util.LinkedList<ast.Ast.Stm.T> stms = new java.util.LinkedList<ast.Ast.Stm.T>();
		ast.Ast.Dec.T dec = null;
		eatToken(Kind.TOKEN_PUBLIC);
		ast.Ast.Type.T retType = parseType();
		String id = eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_LPAREN);
		java.util.LinkedList<ast.Ast.Dec.T> formals = parseFormalList();
		eatToken(Kind.TOKEN_RPAREN);
		eatToken(Kind.TOKEN_LBRACE);
		while (current.kind == Kind.TOKEN_INT
				|| current.kind == Kind.TOKEN_BOOLEAN
				|| current.kind == Kind.TOKEN_ID) {
			if (current.kind != Kind.TOKEN_ID) {
				dec = parseVarDecl();
				locals.add(dec);
			} else {
				String id_1 = current.lexeme;
				advance();
				if (current.kind == Kind.TOKEN_ID) {
					String t_id = eatToken(Kind.TOKEN_ID);
					ast.Ast.Type.T type = new ast.Ast.Type.ClassType(id_1);
					dec = new ast.Ast.Dec.DecSingle((ast.Ast.Type.T) type, t_id);
					locals.add(dec);
					eatToken(Kind.TOKEN_SEMI);
				} else {
					if (current.kind == Kind.TOKEN_ASSIGN) {
						advance();
						ast.Ast.Exp.T exp = parseExp();
						eatToken(Kind.TOKEN_SEMI);
						stms.add(new ast.Ast.Stm.Assign(id_1, exp));
						break;
					} else if (current.kind == Kind.TOKEN_LBRACK) {
						advance();
						ast.Ast.Exp.T index = parseExp();
						eatToken(Kind.TOKEN_RBRACK);
						eatToken(Kind.TOKEN_ASSIGN);
						ast.Ast.Exp.T exp = parseExp();
						eatToken(Kind.TOKEN_SEMI);
						stms.add(new ast.Ast.Stm.AssignArray(id_1, index, exp));
						break;
					} else {
						error(current);
						return null;
					}
				}

			}
		}
		java.util.LinkedList<ast.Ast.Stm.T> stms_1 = parseStatements();
		stms.addAll(stms_1);
		eatToken(Kind.TOKEN_RETURN);
		ast.Ast.Exp.T retExp = parseExp();
		eatToken(Kind.TOKEN_SEMI);
		eatToken(Kind.TOKEN_RBRACE);
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a method.
		// new util.Todo();
		return new ast.Ast.Method.MethodSingle(retType, id, formals, locals, stms, retExp);
	}

	// MethodDecls -> MethodDecl MethodDecls
	// ->
	private java.util.LinkedList<ast.Ast.Method.T> parseMethodDecls() {
		java.util.LinkedList<ast.Ast.Method.T> methods = new java.util.LinkedList<ast.Ast.Method.T>();
		while (current.kind == Kind.TOKEN_PUBLIC) {
			ast.Ast.Method.MethodSingle method = parseMethod();
			methods.add(method);
		}
		return methods;
	}

	// ClassDecl -> class id { VarDecl* MethodDecl* }
	// -> class id extends id { VarDecl* MethodDecl* }
	private ast.Ast.Class.T parseClassDecl() {
		eatToken(Kind.TOKEN_CLASS);
		String class_name = eatToken(Kind.TOKEN_ID);
		String extends_name = null;
		if (current.kind == Kind.TOKEN_EXTENDS) {
			eatToken(Kind.TOKEN_EXTENDS);
			extends_name = eatToken(Kind.TOKEN_ID);
		}
		eatToken(Kind.TOKEN_LBRACE);
		java.util.LinkedList<ast.Ast.Dec.T> decs = parseVarDecls();
		java.util.LinkedList<ast.Ast.Method.T> methods = parseMethodDecls();
		eatToken(Kind.TOKEN_RBRACE);
		return new ast.Ast.Class.ClassSingle(class_name, extends_name, decs, methods);
	}

	// ClassDecls -> ClassDecl ClassDecls
	// ->
	private java.util.LinkedList<ast.Ast.Class.T> parseClassDecls() {
		java.util.LinkedList<ast.Ast.Class.T> classes = new java.util.LinkedList<ast.Ast.Class.T>();
		while (current.kind == Kind.TOKEN_CLASS) {
			ast.Ast.Class.T cls = parseClassDecl();
			classes.add(cls);
		}
		return classes;
	}

	public ast.Ast.Program.T parse() {
		return parseProgram();
	}

	// MainClass -> class id
	// {
	// public static void main ( String [] id )
	// {
	// Statement
	// }
	// }
	private ast.Ast.MainClass.MainClassSingle parseMainClass() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a main class as described by the
		// grammar above.
		eatToken(Kind.TOKEN_CLASS);
		String mainclass_id = eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_LBRACE);
		eatToken(Kind.TOKEN_PUBLIC);
		eatToken(Kind.TOKEN_STATIC);
		eatToken(Kind.TOKEN_VOID);
		eatToken(Kind.TOKEN_MAIN);
		eatToken(Kind.TOKEN_LPAREN);
		eatToken(Kind.TOKEN_STRING);
		eatToken(Kind.TOKEN_LBRACK);
		eatToken(Kind.TOKEN_RBRACK);
		String a = eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_RPAREN);
		eatToken(Kind.TOKEN_LBRACE);
		java.util.LinkedList<ast.Ast.Stm.T> stms = parseStatements();
		eatToken(Kind.TOKEN_RBRACE);
		eatToken(Kind.TOKEN_RBRACE);
		ast.Ast.MainClass.MainClassSingle mainclass = new ast.Ast.MainClass.MainClassSingle(
				mainclass_id, a, stms);
		return mainclass;
		// new util.Todo();
	}

	// Program -> MainClass ClassDecl*
	private ast.Ast.Program.ProgramSingle parseProgram() {
		ast.Ast.MainClass.MainClassSingle mainclass = parseMainClass();
		java.util.LinkedList<ast.Ast.Class.T> classes = parseClassDecls();
		ast.Ast.Program.ProgramSingle prog = new ast.Ast.Program.ProgramSingle(mainclass, classes);
		eatToken(Kind.TOKEN_EOF);
		return prog;
	}

}
