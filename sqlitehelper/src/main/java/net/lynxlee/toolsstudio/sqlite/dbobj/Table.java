package net.lynxlee.toolsstudio.sqlite.dbobj;

//import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.sqljet.core.SqlJetException;

import net.lynxlee.toolsstudio.sqlite.SQLiteHelper;

/**
 * 数据表类
 * 
 * @author air
 * 
 */
public class Table {

	private static Logger logger = LoggerFactory.getLogger(Table.class);

	private String TableName;

	private String db_name = null;

	// private ConcurrentHashMap<String, Field> Fields = new
	// ConcurrentHashMap<String, Field>();

	private Vector<Row> Rows = new Vector<Row>();

	/**
	 * 构造器,默认获得app-conf.xml文件path节点 resource.db文件
	 * 
	 * @throws IOException
	 */
	public Table() throws IOException {
		//db_name = ConfigHelper.getAppConf().getPathMap().get(Config.RESOURCE_DB_KEY).getUrl();
	}

	/**
	 * 构造器
	 * 
	 * @param tableName
	 *            全路径数据库文件名
	 * @return 数据表对象
	 * @throws IOException
	 */
	public static Table getTable(String tableName) throws IOException {
		Table table = new Table();
		table.TableName = tableName;
		return table;
	}

	/**
	 * 获取表名
	 * 
	 * @return 表名
	 */
	public String getTableName() {
		return TableName;
	}

	/**
	 * 设置表名
	 * 
	 * @param tableName
	 *            表名
	 */
	public void setTableName(String tableName) {
		TableName = tableName;
	}

	/**
	 * 获得行数据
	 * 
	 * @return 行数据集合
	 */
	public Vector<Row> getRows() {
		SQLiteHelper dbHelper = new SQLiteHelper();
		Vector<Row> rows = null;
		try {
			rows = dbHelper.select(db_name, this.TableName);
		} catch (SqlJetException e) {
			logger.error("查询数据表:[" + this.TableName + "]失败", e);
		}
		Rows = 0 != rows.size() ? rows : null;
		return Rows;
	}

	/**
	 * 依据条件获得行数据
	 * 
	 * @param field
	 *            条件
	 * @return 行数据集合
	 */
	public Vector<Row> getRow(Field field) {
		SQLiteHelper dbutil = new SQLiteHelper();
		Vector<Row> rows = null;
		try {
			rows = dbutil.selectByField(db_name, this.TableName, field);
		} catch (SqlJetException e) {
			logger.error("查询数据表:[" + this.TableName + "]失败", e);
		}
		Rows = 0 != rows.size() ? rows : null;
		return Rows;
	}

	/**
	 * 数据表添加或更新数据
	 * 
	 * @param rows
	 *            行数据集合
	 * @return 添加行数
	 */
	public long putRows(Vector<Row> rows) {
		SQLiteHelper dbutil = new SQLiteHelper();
		Map<String, Object> map = new HashMap<String, Object>();
		long retu = 0;
		for (Row row : rows) {
			for (Field field : row.getRow()) {
				if (field.isKey()) {
					for (Field _field : row.getRow()) {
						map.put(_field.getFieldName(), _field.getFieldValue());
					}
					if (null != this.getRow(field)) {
						try {
							dbutil.updateByField(this.db_name, this.TableName, row);
							retu++;
						} catch (SqlJetException e) {
							logger.error("更新数据表:[" + TableName + "]数据:[" + row + "]失败", e);
							retu--;
						}

					} else {
						try {
							dbutil.insert(this.db_name, this.TableName, map);
							retu++;
						} catch (SqlJetException e) {
							logger.error("插入数据表:[" + TableName + "]数据:[" + row + "]失败", e);
							retu--;
						}
					}
				} else {
					try {
						dbutil.insert(this.db_name, this.TableName, map);
						retu++;
					} catch (SqlJetException e) {
						logger.error("插入数据表:[" + TableName + "]数据:[" + row + "]失败", e);
						retu--;
					}
				}
			}

		}
		return retu;
	}

	/**
	 * 数据表添加或更新一行数据
	 * 
	 * @param row
	 *            行数据
	 * @return true - 操作成功 false - 操作失败
	 */
	public boolean putRow(Row row) {
		SQLiteHelper dbutil = new SQLiteHelper();
		Map<String, Object> map = new HashMap<String, Object>();
		for (Field field : row.getRow()) {
			map.put(field.getFieldName(), field.getFieldValue());
		}
		for (Field field : row.getRow()) {
			if (field.isKey()) {
				if (null != this.getRow(field)) {
					try {
						dbutil.updateByField(this.db_name, this.TableName, row);
					} catch (SqlJetException e) {
						logger.error("更新数据表:[" + TableName + "]数据:[" + row + "]失败", e);
						return false;
					}
					return true;
				} else {
					try {
						dbutil.insert(this.db_name, this.TableName, map);
					} catch (SqlJetException e) {
						logger.error("插入数据表:[" + TableName + "]数据:[" + row + "]失败", e);
						return false;
					}
					return true;
				}
			} else {
				try {
					dbutil.insert(this.db_name, this.TableName, map);
				} catch (SqlJetException e) {
					logger.error("插入数据表:[" + TableName + "]数据:[" + row + "]失败", e);
					return false;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 依据条件删除表数据
	 * 
	 * @param field
	 *            删除条件
	 * @return true - 操作成功 false - 操作失败
	 */
	public boolean delRowByField(Field field) {
		SQLiteHelper dbutil = new SQLiteHelper();
		try {
			dbutil.deleteByField(this.db_name, this.TableName, field);
			return true;
		} catch (SqlJetException e) {
			logger.error("删除数据表:[" + TableName + "]条件数据的:[" + field + "]失败", e);
			return false;
		}
	}

	// public Field getField(String FieldName) {
	// return this.Fields.get(FieldName);
	// }
	//
	// public void setField(Field field) {
	// this.Fields.put(field.getFieldName(), field);
	// }
	//
	// public ConcurrentHashMap<String, Field> getFields() {
	// return this.Fields;
	// }
	//
	// public void setFields(ConcurrentHashMap<String, Field> fields) {
	// this.Fields = fields;
	// }

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(null != this.getTableName() ? "TableName:" + this.getTableName() + " " : "");
		// sb.append(null != this.getFields() ? "TableFields:" +
		// this.getFields()
		// + " " : "");
		return sb.toString();
	}
}
