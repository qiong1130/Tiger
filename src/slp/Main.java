package slp;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import slp.Slp.Exp;
import slp.Slp.Exp.Eseq;
import slp.Slp.Exp.Id;
import slp.Slp.Exp.Num;
import slp.Slp.Exp.OP_T;
import slp.Slp.Exp.Op;
import slp.Slp.Exp.T;
import slp.Slp.ExpList;
import slp.Slp.ExpList.Last;
import slp.Slp.ExpList.Pair;
import slp.Slp.Stm;
import util.Bug;
import util.Todo;
import control.Control;
import table.Table;

public class Main
{
  // ///////////////////////////////////////////
  // maximum number of args
  public static List<Table> table_list = new ArrayList<Table>();
  private int maxArgsExp(Exp.T exp)
  {
    //new Todo();
    return -1;
  }

  private int maxArgsStm(Stm.T stm)//最大参数个数值
  {
	int count=0;
    if (stm instanceof Stm.Compound) {
      Stm.Compound s = (Stm.Compound) stm;
      int n1 = maxArgsStm(s.s1);
      int n2 = maxArgsStm(s.s2);
     return n1 >= n2 ? n1 : n2;
    } else if (stm instanceof Stm.Assign) {
    	String id=((Stm.Assign) stm).id;
    	 Exp.T exp=((Stm.Assign) stm).exp;	
    	 if(exp instanceof Exp.Eseq)
    	 {
    		 Stm.T stmmm=((Exp.Eseq) exp).stm;
    		 count=maxArgsStm(stmmm);
    		 exp=((Exp.Eseq) exp).exp;	
    	 }
    	 return ++count;
    } else if (stm instanceof Stm.Print) {
     // new Todo();
    	ExpList.T explist=((Stm.Print) stm).explist;   
    	if(explist instanceof ExpList.Pair){
    		Exp.T expp=((ExpList.Pair) explist).exp;
    		if(expp instanceof Exp.Id){
    			count++;
    		}else if(expp instanceof Exp.Op){  
    				count++;
    		}
    		return count;
    	}else if(explist instanceof ExpList.Last){
    		Exp.T expp=((ExpList.Last) explist).exp;
    		if(expp instanceof Exp.Op){
    			count++;
    		}else if(expp instanceof Exp.Id){
    			count++;
    		}
    		return count;
    	}
    } else 
      new Bug();
    return 0;
  }

  // ////////////////////////////////////////
  // interpreter
  ///interExp
  private int interpExp(Exp.T exp)
  {
	  int result=0;
	  if(exp instanceof Exp.Id){
		  result=lookup(((Exp.Id) exp).id);
	  }else if(exp instanceof Exp.Num){
		  result=((Exp.Num) exp).num;
	  }else if(exp instanceof Exp.Op){
		  	OP_T op=((Exp.Op) exp).op;
 	 	    int left=interpExp(((Exp.Op) exp).left);
 	 	    int right=interpExp(((Exp.Op) exp).right);	    	
 	 	    if( op == OP_T.ADD) {
 	 	    	result=left + right;
 	 	    }else if( op == OP_T.SUB) {
 	 	    	result=left - right;	
 	 	    }else if( op == OP_T.TIMES){
 	 	    	result=left * right;	
 	 	    }else if( op == OP_T.DIVIDE){	 
 	 	    	result=left / right;	
 	 	    }
	  }else if(exp instanceof Exp.Eseq)
	  {
		  Stm.T stm=((Exp.Eseq) exp).stm;
 	 		T expp=((Exp.Eseq) exp).exp;	
 	 		interpStm(stm);
 	 		result=interpExp(expp);
	  }
	  return result;
  }
  ////////////////////////////////////////
  //interExpList
  private void interpExpList(ExpList.T exp)
  {
	  int print;
	  if(exp instanceof ExpList.Pair){
		Exp.T expp=((ExpList.Pair) exp).exp;
  		ExpList.T listt=((ExpList.Pair) exp).list;
  		print=interpExp(expp);
  		System.out.print("print:" + print);
  		interpExpList(listt);
	  }else if(exp instanceof ExpList.Last){
  		Exp.T expp=((ExpList.Last) exp).exp;
  		print=interpExp(expp);
  		System.out.println(" " + print);
  	}   	
  }
  //interpret Statement
  private void interpStm(Stm.T prog)
  {
	  Table table;
    if (prog instanceof Stm.Compound) {
    	Stm.Compound s = (Stm.Compound) prog;
        interpStm(s.s1);
        interpStm(s.s2);	
    } else if (prog instanceof Stm.Assign) {
    	String id=((Stm.Assign) prog).id;
   	 	Exp.T exp=((Stm.Assign) prog).exp;  
   	 	int result=interpExp(exp);
   	 	table=new Table(id,result);
   	 	table_list.add(table);
    } else if (prog instanceof Stm.Print) {
    	ExpList.T explist=((Stm.Print) prog).explist;   
    	 interpExpList(explist);
     } else
      new Bug();
  }
  private static int lookup(String key) {
		for (int i = 0; i < table_list.size(); i++) {
			if (table_list.get(i).id.equals(key)) {
				return table_list.get(i).value;
			}
		}
		System.out.println("lookup fail!");
		return 0;
	}
  //////////////////////////////////////////
  // compile
  HashSet<String> ids;
  StringBuffer buf;

  private void emit(String s)
  {
    buf.append(s);
  }

  private void compileExp(Exp.T exp)
  {
    if (exp instanceof Id) {
      Exp.Id e = (Exp.Id) exp;
      String id = e.id;

      emit("\tmovl\t" + id + ", %eax\n");
    } else if (exp instanceof Num) {
      Exp.Num e = (Exp.Num) exp;
      int num = e.num;

      emit("\tmovl\t$" + num + ", %eax\n");
    } else if (exp instanceof Op) {
      Exp.Op e = (Exp.Op) exp;
      Exp.T left = e.left;
      Exp.T right = e.right;
      Exp.OP_T op = e.op;

      switch (op) {
      case ADD:
        compileExp(left);
        emit("\tpushl\t%eax\n");
        compileExp(right);
        emit("\tpopl\t%edx\n");
        emit("\taddl\t%edx, %eax\n");
        break;
      case SUB:
        compileExp(left);
        emit("\tpushl\t%eax\n");
        compileExp(right);
        emit("\tpopl\t%edx\n");
        emit("\tsubl\t%eax, %edx\n");
        emit("\tmovl\t%edx, %eax\n");
        break;
      case TIMES:
        compileExp(left);
        emit("\tpushl\t%eax\n");
        compileExp(right);
        emit("\tpopl\t%edx\n");
        emit("\timul\t%edx\n");
        break;
      case DIVIDE:
        compileExp(left);
        emit("\tpushl\t%eax\n");
        compileExp(right);
        emit("\tpopl\t%edx\n");
        emit("\tmovl\t%eax, %ecx\n");
        emit("\tmovl\t%edx, %eax\n");
        emit("\tcltd\n");
        emit("\tdiv\t%ecx\n");
        break;
      default:
        new Bug();
      }
    } else if (exp instanceof Eseq) {
      Eseq e = (Eseq) exp;
      Stm.T stm = e.stm;
      Exp.T ee = e.exp;

      compileStm(stm);
      compileExp(ee);
    } else
      new Bug();
  }

  private void compileExpList(ExpList.T explist)
  {
    if (explist instanceof ExpList.Pair) {
      ExpList.Pair pair = (ExpList.Pair) explist;
      Exp.T exp = pair.exp;
      ExpList.T list = pair.list;

      compileExp(exp);
      emit("\tpushl\t%eax\n");
      emit("\tpushl\t$slp_format\n");
      emit("\tcall\tprintf\n");
      emit("\taddl\t$4, %esp\n");
      compileExpList(list);
    } else if (explist instanceof ExpList.Last) {
      ExpList.Last last = (ExpList.Last) explist;
      Exp.T exp = last.exp;

      compileExp(exp);
      emit("\tpushl\t%eax\n");
      emit("\tpushl\t$slp_format\n");
      emit("\tcall\tprintf\n");
      emit("\taddl\t$4, %esp\n");
    } else
      new Bug();
  }

  private void compileStm(Stm.T prog)
  {
    if (prog instanceof Stm.Compound) {
      Stm.Compound s = (Stm.Compound) prog;
      Stm.T s1 = s.s1;
      Stm.T s2 = s.s2;

      compileStm(s1);
      compileStm(s2);
    } else if (prog instanceof Stm.Assign) {
      Stm.Assign s = (Stm.Assign) prog;
      String id = s.id;
      Exp.T exp = s.exp;

      ids.add(id);
      compileExp(exp);
      emit("\tmovl\t%eax, " + id + "\n");
    } else if (prog instanceof Stm.Print) {
      Stm.Print s = (Stm.Print) prog;
      ExpList.T explist = s.explist;

      compileExpList(explist);
      emit("\tpushl\t$newline\n");
      emit("\tcall\tprintf\n");
      emit("\taddl\t$4, %esp\n");
    } else
      new Bug();
  }

  // ////////////////////////////////////////
  public void doit(Stm.T prog)
  {
    // return the maximum number of arguments
    if (Control.ConSlp.action == Control.ConSlp.T.ARGS) {
      int numArgs = maxArgsStm(prog);
      System.out.println(numArgs);
    }

    // interpret a given program
    if (Control.ConSlp.action == Control.ConSlp.T.INTERP) {
      interpStm(prog);
    }

    // compile a given SLP program to x86
    if (Control.ConSlp.action == Control.ConSlp.T.COMPILE) {
      ids = new HashSet<String>();
      buf = new StringBuffer();

      compileStm(prog);
      try {
        // FileOutputStream out = new FileOutputStream();
        FileWriter writer = new FileWriter("slp_gen.s");
        writer
            .write("// Automatically generated by the Tiger compiler, do NOT edit.\n\n");
        writer.write("\t.data\n");
        writer.write("slp_format:\n");
        writer.write("\t.string \"%d \"\n");
        writer.write("newline:\n");
        writer.write("\t.string \"\\n\"\n");
        for (String s : this.ids) {
          writer.write(s + ":\n");
          writer.write("\t.int 0\n");
        }
        writer.write("\n\n\t.text\n");
        writer.write("\t.globl main\n");
        writer.write("main:\n");
        writer.write("\tpushl\t%ebp\n");
        writer.write("\tmovl\t%esp, %ebp\n");
        writer.write(buf.toString());
        writer.write("\tleave\n\tret\n\n");
        writer.close();
        Process child = Runtime.getRuntime().exec("gcc slp_gen.s");
        child.waitFor();
        if (!Control.ConSlp.keepasm)
          Runtime.getRuntime().exec("rm -rf slp_gen.s");
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(0);
      }
      // System.out.println(buf.toString());
    }
  }
}
