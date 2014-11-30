package lexer;

import static control.Control.ConLexer.dump;

import java.io.InputStream;

import lexer.Token.Kind;
import util.Todo;
import util.Todo.Errorkind;

public class Lexer
{
  String fname; // the input file name to be compiled
  InputStream fstream; // input stream for the above file

  public Lexer(String fname, InputStream fstream)
  {
    this.fname = fname;
    this.fstream = fstream;
  }
  String[] word=new String[]{"public","class","extends","void","main",
		  	"static","int","if","else","length","new","out","System",
		  	"return","while","this","true","false","String","println",
		  	"boolean",""};
  Kind[] kind=new Kind[] {Kind.TOKEN_PUBLIC, Kind.TOKEN_CLASS, Kind.TOKEN_EXTENDS,
		  Kind.TOKEN_VOID, Kind.TOKEN_MAIN,Kind.TOKEN_STATIC,Kind.TOKEN_INT, Kind.TOKEN_IF,
		  Kind.TOKEN_ELSE, Kind.TOKEN_LENGTH,Kind.TOKEN_NEW, Kind.TOKEN_OUT, Kind.TOKEN_SYSTEM,
		  Kind.TOKEN_RETURN,  Kind.TOKEN_WHILE,Kind.TOKEN_THIS,Kind.TOKEN_TRUE,Kind.TOKEN_FALSE,
		  Kind.TOKEN_STRING,Kind.TOKEN_PRINTLN,Kind.TOKEN_BOOLEAN,};
	int tokenvalue;//token值
	int lineNum = 1;//行号
	int columnNum = 0;//列号
	String lexbuf = "";
  // When called, return the next token (refer to the code "Token.java")
  // from the input stream.
  // Return TOKEN_EOF when reaching the end of the input stream.
  private Token nextTokenInternal() throws Exception
  {
    int c = this.fstream.read();
    int cur_column;
    columnNum++; 
      // The value for "lineNum" is now "null",
      // you should modify this to an appropriate
      // line number for the "EOF" token.
    // skip all kinds of "blanks"
    while(true) {
    	while (' ' == c || '\t' == c || '\n' == c|| 13 == c) {
    		if(10 == c){//换行 13为回车
    			lineNum++;
    			columnNum = 0;
    		}
    		if('\t' == c){//制表
    			columnNum += 7;
    		}
    		c = this.fstream.read();
    		columnNum++;
    	}
    	if (-1 == c)
    		return new Token(Kind.TOKEN_EOF, lineNum, columnNum);
    	if(isdigit(c)){
    		columnNum++;
			cur_column = columnNum;
			tokenvalue = c - '0';
			this.fstream.mark(1);
			c = this.fstream.read();
			while (isdigit(c)) {//数字
				columnNum++;
				tokenvalue = tokenvalue * 10 + c - '0';
				this.fstream.mark(1);
				c = this.fstream.read();
			}
			if (isalpha(c)) {
				new Todo(Errorkind.columnNum, lineNum, columnNum);
				return null;
			}
			this.fstream.reset();
			columnNum--;
			return new Token(Kind.TOKEN_NUM, lineNum, cur_column,Integer.toString(tokenvalue));
		}
    	if (isalpha(c)) {
			cur_column = columnNum;
			lexbuf = "";
			while (isalpha(c) || isdigit(c)) {
				columnNum++;
				lexbuf += String.valueOf((char) c);
				this.fstream.mark(1);
				c = this.fstream.read();
			}
			this.fstream.reset();
			columnNum--;
			int p = lookup(lexbuf);
			if (-1 != p) {
				return new Token(kind[p], lineNum, cur_column);
			} else {
				return new Token(Kind.TOKEN_ID, lineNum, cur_column);
			}
		}
    	switch (c) {
    	case '+':
    		return new Token(Kind.TOKEN_ADD, lineNum, columnNum);
    	case '&':
			if (this.fstream.read() == '&') {
				return new Token(Kind.TOKEN_AND, lineNum, columnNum);
			}
		case '=':
			return new Token(Kind.TOKEN_ASSIGN, lineNum, columnNum);
		case ',':
			return new Token(Kind.TOKEN_COMMER, lineNum, columnNum);
		case '.':
			return new Token(Kind.TOKEN_DOT, lineNum, columnNum);
		case '{':
			return new Token(Kind.TOKEN_LBRACE, lineNum, columnNum);
		case '[':
			return new Token(Kind.TOKEN_LBRACK, lineNum, columnNum);
		case '(':
			return new Token(Kind.TOKEN_LPAREN, lineNum, columnNum);
		case '<':
			return new Token(Kind.TOKEN_LT, lineNum, columnNum);
		case '!':
			return new Token(Kind.TOKEN_NOT,lineNum, columnNum);
		case '}':
			return new Token(Kind.TOKEN_RBRACE,lineNum, columnNum);
		case ']':
			return new Token(Kind.TOKEN_RBRACK, lineNum, columnNum);
		case ')':
			return new Token(Kind.TOKEN_RPAREN, lineNum, columnNum);
		case ';':
			return new Token(Kind.TOKEN_SEMI,lineNum, columnNum);
		case '-':
			return new Token(Kind.TOKEN_SUB, lineNum, columnNum);
		case '*':
			return new Token(Kind.TOKEN_TIMES, lineNum, columnNum);
		case '/':
			int cc = this.fstream.read();
			if ('/' == cc) {
				cc = this.fstream.read();
				while (cc != 10 && cc != -1) {
					cc = this.fstream.read();
				}
				if (cc == 10) {//换行
					lineNum++;
					columnNum = 0;
				}
				c = this.fstream.read();
				columnNum++;
				continue;
			} else if (cc == '*') {//注释
				int circle = 1;
				cc = this.fstream.read();
				while (0 != circle) {
					while (cc != '*') {
						cc = this.fstream.read();
						columnNum++;
						if (cc == 10) {
							lineNum++;
							columnNum = 0;
						}
						if ('\t' == c) {
							columnNum += 7;
						}
					}
					cc = this.fstream.read();
					columnNum++;
					if (cc == '/') {
						c = this.fstream.read();
						columnNum++;
						circle--;
					}
				}
				continue;
			} else {
				new Todo(Errorkind.columnNum, lineNum, columnNum);
				return null;
			} 
    	default:
      // Lab 1, exercise 2: supply missing code to
      // lex other kinds of tokens.
      // Hint: think carefully about the basic
      // data structure and algorithms. The code
      // is not that much and may be less than 50 lines. If you
      // find you are writing a lot of code, you
      // are on the wrong way.
    		new Todo(Errorkind.error, lineNum, columnNum);
    		return null;
    	}
    }
  }

  private int lookup(String lexbuf2) {
	// TODO Auto-generated method stub
	  for (int i = 0; i < word.length; i++) {
			if (word[i].equals(lexbuf)) {
				return i;
			}
		}
		return -1;
}

private boolean isalpha(int c) {
	// TODO Auto-generated method stub
	return c - 'a' >= 0 && c - 'z' <= 0 || c - 'A' >= 0 && c - 'Z' <= 0
			|| c == '_' ? true : false;
}

private boolean isdigit(int c) {
	// TODO Auto-generated method stub
	return c - '0' >= 0 && c - '9' <= 0 ? true : false;
}

public Token nextToken()
  {
    Token t = null;

    try {
      t = this.nextTokenInternal();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    if (dump)
      System.out.println(t.toString());
    return t;
  }
}
