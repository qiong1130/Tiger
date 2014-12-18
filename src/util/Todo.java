package util;

public class Todo {

	public enum ErrorKind {
		error, // �Ƿ��ַ�
		lineNum, // �к�Ϊ��
		columnNum, // �Ƿ���ʶ��
		ERROR_,
	}

	public ErrorKind error;// kind of error
	public Integer lineNum;// on which line of the source file this token
							// appears
	public Integer columnNum;// on which column of the source file this token
								// appears

	public Todo(ErrorKind error, Integer lineNum, Integer columnNum) {
		this.error = error;
		this.lineNum = lineNum;
		this.columnNum = columnNum;
		System.out.println(this.toString());
		throw new java.lang.Error();
	}

	@Override
	public String toString() {
		String s = "ERROR:" + this.error + "\tat line " + this.lineNum
				+ ",column " + this.columnNum;
		return s;
	}
}
