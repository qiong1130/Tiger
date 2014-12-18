package elaborator;

public class MethodType {
	public ast.Ast.Type.T retType;
	public java.util.LinkedList<ast.Ast.Dec.T> argsType;

	public MethodType(ast.Ast.Type.T retType, java.util.LinkedList<ast.Ast.Dec.T> decs) {
		this.retType = retType;
		this.argsType = decs;
	}

	@Override
	public String toString() {
		String s = "";
		for (ast.Ast.Dec.T dec : this.argsType) {
			ast.Ast.Dec.DecSingle decc = (ast.Ast.Dec.DecSingle) dec;
			s = decc.type.toString() + "* " + s;
		}
		s = "����:" + s + ", return:" + this.retType.toString();
		return s;
	}
}
