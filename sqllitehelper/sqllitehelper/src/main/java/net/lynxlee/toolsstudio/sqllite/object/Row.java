package net.lynxlee.toolsstudio.sqllite.object;

import java.util.Vector;

/**
 * sqllite表行对应类
 * @author air
 *
 */
public class Row extends Vector<Field> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 行号
	 */
	private long rowId;

	/**
	 * 行包含字段集合
	 */
	private Vector<Field> Row = new Vector<Field>();

	public long getRowId() {
		return rowId;
	}

	public void setRowId(long rowId) {
		this.rowId = rowId;
	}

	public Vector<Field> getRow() {
		return Row;
	}

	public void setRow(Vector<Field> row) {
		Row = row;
	}

	public void addField(Field field) {
		Row.add(field);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(null != this.getRow() ? "Row:" + this.getRow() + " " : "");
		return sb.toString();
	}
}
