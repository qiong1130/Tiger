package elaborator;

import java.util.Iterator;

public class MethodTable {
	private java.util.Hashtable<String, ast.Ast.Type.T> table;

	public MethodTable() {
		this.table = new java.util.Hashtable<String, ast.Ast.Type.T>();
	}

	// Duplication is not allowed
	public void put(java.util.LinkedList<ast.Ast.Dec.T> formals,
			java.util.LinkedList<ast.Ast.Dec.T> locals) {
		for (ast.Ast.Dec.T dec : formals) {
			ast.Ast.Dec.DecSingle decc = (ast.Ast.Dec.DecSingle) dec;
			if (this.table.get(decc.id) != null) {
				System.out.println("duplicated parameter: " + decc.id);
				//System.exit(1);
			}
			this.table.put(decc.id, decc.type);
		}

		for (ast.Ast.Dec.T dec : locals) {
			ast.Ast.Dec.DecSingle decc = (ast.Ast.Dec.DecSingle) dec;
			if (this.table.get(decc.id) != null) {
				System.out.println("duplicated variable: " + decc.id);
				//System.exit(1);
			}
			this.table.put(decc.id, decc.type);
		}

	}
	// return null for non-existing keys
	public ast.Ast.Type.T get(String id) {
		return this.table.get(id);
	}
	public void dump() {

		for (Iterator<String> itr = this.table.keySet().iterator(); itr
				.hasNext();) {
			String key = (String) itr.next();
			ast.Ast.Type.T type = this.table.get(key);
			System.out.println("\t" + type + "  " + key);
		}
		// new Todo();
	}

	
	public java.util.Hashtable<String, ast.Ast.Type.T> getTable() {
		return table;
	}

	@Override
	public String toString() {
		return this.table.toString();
	}
}
