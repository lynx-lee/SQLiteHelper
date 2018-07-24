package net.lynxlee.toolsstudio.sqllite;

import java.util.concurrent.ConcurrentHashMap;

import net.lynxlee.toolsstudio.sqllite.object.Table;

/**
 * 数据表句柄类
 * @author air
 *
 */
public class TableHelper {

	private static TableHelper th;

	private ConcurrentHashMap<String, Table> tables = new ConcurrentHashMap<String, Table>();

	private TableHelper() {

	}

	public static TableHelper getInstance() {
		if (null == th) {
			th = new TableHelper();
		}
		return th;
	}

	public ConcurrentHashMap<String, Table> getTables() {
		return this.tables;
	}

	public void setTables(ConcurrentHashMap<String, Table> tables) {
		this.tables = tables;
	}

	public Table getTable(String tableName) {
		return this.tables.get(tableName);
	}

	public void setTable(Table table) {
		this.tables.put(table.getTableName(), table);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(null != this.getTables() ? "Tables:" + this.getTables() + " "
				: "");
		return sb.toString();
	}
}
