package table;

public class Table {
	public String id;
	public int value;

	public Table(String id, int value) {
		this.id = id;
		this.value = value;
	}
	
	public void update(String id, int value) {
		this.id = id;
		this.value = value;
	}

}