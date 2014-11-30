package util;

public class Todo
{
	public enum Errorkind{
		error,lineNum,columnNum,ERROR_
	}
	public Errorkind error;
	public Integer lineNum;
	public Integer columnNum;
	
	
  public Todo(Errorkind error,Integer lineNum,Integer columnNum)
  {
	  this.columnNum=columnNum;
	  this.error=error;
	  this.lineNum=lineNum;
	  System.out.println(this.toString());
    throw new java.lang.Error ();
  }
  @Override
	public String toString() {
		String s = "ERROR:" + this.error + "\tat line " + this.lineNum
				+ ",column " + this.columnNum;
		return s;
	}
}
