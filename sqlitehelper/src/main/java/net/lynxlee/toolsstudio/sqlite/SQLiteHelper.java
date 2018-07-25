package net.lynxlee.toolsstudio.sqlite;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnDef;
import org.tmatesoft.sqljet.core.schema.ISqlJetTableDef;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.ISqlJetTransaction;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import net.lynxlee.toolsstudio.sqlite.dbobj.Field;
import net.lynxlee.toolsstudio.sqlite.dbobj.Row;

/**
 * sqllite数据库集合句柄类
 * 
 * @author air
 * 
 */
public class SQLiteHelper {

	private static Logger logger = LoggerFactory.getLogger(SQLiteHelper.class);

	/**
	 * sqllite数据库集合
	 */
	private static ConcurrentHashMap<String, SqlJetDb> dbs = new ConcurrentHashMap<String, SqlJetDb>();

	/**
	 * 向数据库集合添加数据库句柄
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @throws RuntimeException
	 *             打开数据库文件失败
	 */
	public void putDbs(String db_name) throws RuntimeException {
		SqlJetDb db = null;
		if (null != (db = this.openDb(db_name))) {
			this.putDbs(db_name, db);
		} else {
			new RuntimeException("数据库[" + db_name + "]打开失败.");
		}
	}

	/**
	 * 向数据库集合添加数据库句柄
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @param db
	 *            数据库调用句柄
	 */
	public void putDbs(String db_name, SqlJetDb db) {
		dbs.put(db_name, db);
	}

	/**
	 * 获取数据库调用句柄
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @return 数据库调用句柄
	 */
	public SqlJetDb getDb(String db_name) {
		SqlJetDb db = null;
		if (dbs.containsKey(db_name)) {
			if (dbs.get(db_name).isOpen()) {
				return dbs.get(db_name);
			}
		}
		db = this.openDb(db_name);
		dbs.put(db_name, db);
		return db;
	}

	/**
	 * 打开数据库文件并获取操作句柄
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @return 数据库操作句柄
	 */
	public SqlJetDb openDb(String db_name) {
		SqlJetDb db = null;
		try {
			db = SqlJetDb.open(new File(db_name), true);
			// db.getOptions().setAutovacuum(true);
		} catch (SqlJetException e) {
			logger.error("打开[" + db_name + "]失败!", e);
		}
		return db;
	}

	/**
	 * 获取数据库表设计操作句柄
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @param table_name
	 *            表名
	 * @return 数据表设计操作句柄
	 * @throws SqlJetException
	 *             获取句柄异常
	 */
	public ISqlJetTableDef getTableDef(String db_name, String table_name) throws SqlJetException {
		ISqlJetTableDef lsjtd = this.getDb(db_name).getSchema().getTable(table_name);
		if (null != lsjtd) {
			return lsjtd;
		} else {
			return null;
		}
	}

	/**
	 * 获取数据库表操作句柄
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @param table_name
	 *            表名
	 * @return 数据库表操作句柄
	 * @throws SqlJetException
	 *             获取句柄异常
	 */
	public ISqlJetTable getTable(String db_name, String table_name) throws SqlJetException {
		ISqlJetTable isjt = this.getDb(db_name).getTable(table_name);
		if (null != isjt) {
			return isjt;
		} else {
			return null;
		}
	}

	/**
	 * 获取数据库表名集合
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @return 数据库表名集合
	 * @throws SqlJetException
	 *             操作句柄异常
	 */
	public Set<String> getTableNames(String db_name) throws SqlJetException {
		Set<String> set = this.getDb(db_name).getSchema().getTableNames();
		if (null != set) {
			return set;
		} else {
			return null;
		}
	}

	/**
	 * 插入数据表操作
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @param table_name
	 *            表名
	 * @param values
	 *            字段集合
	 * @return 插入行数
	 * @throws SqlJetException
	 *             操作句柄异常
	 */
	public long insert(String db_name, String table_name, Map<String, Object> values) throws SqlJetException {
		this.beginWriteTransaction(db_name);
		try {
			return this.getTable(db_name, table_name).insertByFieldNames(values);
		} catch (SqlJetException e) {
			this.rollbackTransaction(db_name);
			throw new SqlJetException(e);
		} finally {
			this.commitTransaction(db_name);
		}
	}

	/**
	 * 全表查询数据表操作
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @param table_name
	 *            表名
	 * @return 查询数据集合
	 * @throws SqlJetException
	 *             操作句柄异常
	 */
	public Vector<Row> select(String db_name, String table_name) throws SqlJetException {
		ISqlJetTableDef lsjtd = null;
		List<ISqlJetColumnDef> list = null;
		ISqlJetColumnDef lsjcd = null;
		ISqlJetCursor cursor = null;
		Vector<String> fieldName = new Vector<String>();
		Vector<Row> rows = new Vector<Row>();
		Row row = null;
		Field field = null;
		this.beginReadTransaction(db_name);
		try {
			lsjtd = this.getTableDef(db_name, table_name);
			list = lsjtd.getColumns();
			for (int i = 0; i < list.size(); i++) {
				lsjcd = list.get(i);
				fieldName.add(lsjcd.getName());
			}

			cursor = this.getTable(db_name, table_name).open();
			if (!cursor.eof()) {
				do {
					row = new Row();
					row.setRowId(cursor.getRowId());
					for (int i = 0; i < cursor.getFieldsCount(); i++) {
						field = new Field(fieldName.get(i), cursor.getValue(i), false);
						row.addField(field);
					}
					rows.add(row);
				} while (cursor.next());
			}
		} catch (SqlJetException e) {
			this.rollbackTransaction(db_name);
			throw new SqlJetException(e);
		} finally {
			cursor.close();
			this.commitTransaction(db_name);
		}
		return rows;
	}

	/**
	 * 依据查询条件查询数据表操作
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @param table_name
	 *            表名
	 * @param field
	 *            条件对象
	 * @return 查询数据集合
	 * @throws SqlJetException
	 *             操作句柄异常
	 */
	public Vector<Row> selectByField(String db_name, String table_name, Field field) throws SqlJetException {
		boolean flag = false;
		Vector<Row> rows = new Vector<Row>();
		// Row row = null;
		Vector<Row> _rows = this.select(db_name, table_name);
		for (Row _row : _rows) {
			for (Field _field : _row.getRow()) {
				if (_field.getFieldName().equals(field.getFieldName())
						&& _field.getFieldValue().toString().equals(field.getFieldValue().toString())) {
					flag = true;
					break;
				}
			}
			if (flag) {
				rows.add(_row);
				flag = false;
				// break;
			}
		}
		return rows;
	}

	/**
	 * 依据条件更新数据表操作
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @param table_name
	 *            表名
	 * @param row
	 *            行对象
	 * @throws SqlJetException
	 *             操作句柄异常
	 */
	public void updateByField(String db_name, String table_name, Row row) throws SqlJetException {
		ISqlJetTableDef lsjtd = null;
		List<ISqlJetColumnDef> list = null;
		ISqlJetColumnDef lsjcd = null;
		Vector<String> fieldName = new Vector<String>();
		ISqlJetCursor cursor = null;
		Map<String, Object> map = null;
		this.beginWriteTransaction(db_name);
		try {

			lsjtd = this.getTableDef(db_name, table_name);
			list = lsjtd.getColumns();
			for (int i = 0; i < list.size(); i++) {
				lsjcd = list.get(i);
				fieldName.add(lsjcd.getName());
			}

			cursor = this.getTable(db_name, table_name).open();
			while (!cursor.eof()) {
				for (int i = 0; i < cursor.getFieldsCount(); i++) {
					for (Field _field : row.getRow()) {
						if (_field.isKey()) {
							if (fieldName.get(i).equals(_field.getFieldName())
									&& cursor.getValue(i).equals(_field.getFieldValue())) {
								map = new HashMap<String, Object>();
								for (Field field : row.getRow()) {
									map.put(field.getFieldName(), field.getFieldValue());
								}
								cursor.updateByFieldNames(map);
								break;
							}
						}
					}
				}
				cursor.next();
			}
		} catch (SqlJetException e) {
			this.rollbackTransaction(db_name);
			throw new SqlJetException(e);
		} finally {
			cursor.close();
			this.commitTransaction(db_name);
		}
	}

	/**
	 * 依据条件删除表数据操作
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @param table_name
	 *            表名
	 * @param field
	 *            字段对象
	 * @throws SqlJetException
	 *             操作句柄异常
	 */
	public void deleteByField(String db_name, String table_name, Field field) throws SqlJetException {
		ISqlJetTableDef lsjtd = null;
		List<ISqlJetColumnDef> list = null;
		ISqlJetColumnDef lsjcd = null;
		Vector<String> fieldName = new Vector<String>();
		ISqlJetCursor cursor = null;
		this.beginWriteTransaction(db_name);
		try {
			lsjtd = this.getTableDef(db_name, table_name);
			list = lsjtd.getColumns();
			for (int i = 0; i < list.size(); i++) {
				lsjcd = list.get(i);
				fieldName.add(lsjcd.getName());
			}

			cursor = this.getTable(db_name, table_name).open();
			while (!cursor.eof()) {
				for (int i = 0; i < cursor.getFieldsCount(); i++) {
					if (fieldName.get(i).equals(field.getFieldName())
							&& cursor.getValue(i).equals(field.getFieldValue())) {
						cursor.delete();
					}
				}
				cursor.next();
			}
		} catch (SqlJetException e) {
			this.rollbackTransaction(db_name);
			throw new SqlJetException(e);
		} finally {
			cursor.close();
			this.commitTransaction(db_name);
		}
	}

	/**
	 * 关闭数据表句柄引用
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @throws SqlJetException
	 *             操作句柄异常
	 */
	public void closeDb(String db_name) throws SqlJetException {
		this.getDb(db_name).close();
		dbs.remove(db_name);
	}

	/**
	 * 开启读取事务
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @throws SqlJetException
	 *             操作句柄异常
	 */
	public void beginReadTransaction(String db_name) throws SqlJetException {
		this.getDb(db_name).beginTransaction(SqlJetTransactionMode.READ_ONLY);
	}

	/**
	 * 开启写入事务
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @throws SqlJetException
	 *             操作句柄异常
	 */
	public void beginWriteTransaction(String db_name) throws SqlJetException {
		this.getDb(db_name).runTransaction(new ISqlJetTransaction() {
			public Object run(SqlJetDb db) throws SqlJetException {
				db.getOptions().setUserVersion(1);
				return true;
			}
		}, SqlJetTransactionMode.WRITE);
		this.getDb(db_name).beginTransaction(SqlJetTransactionMode.WRITE);
	}

	/**
	 * 开启独占事务
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @throws SqlJetException
	 *             操作句柄异常
	 */
	public void beginExclusiveTransaction(String db_name) throws SqlJetException {
		this.getDb(db_name).beginTransaction(SqlJetTransactionMode.EXCLUSIVE);
	}

	/**
	 * 提交事务
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @throws SqlJetException
	 *             操作句柄异常
	 */
	public void commitTransaction(String db_name) throws SqlJetException {
		this.getDb(db_name).commit();

	}

	/**
	 * 回滚事务
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @throws SqlJetException
	 *             操作句柄异常
	 */
	public void rollbackTransaction(String db_name) throws SqlJetException {
		this.getDb(db_name).rollback();
	}

	/**
	 * 创建数据表
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @param sql
	 *            建表sql
	 * @return true - 成功 false - 失败
	 */
	public boolean createTable(String db_name, String sql) {
		try {
			this.beginWriteTransaction(db_name);
			this.getDb(db_name).createTable(sql);
			this.commitTransaction(db_name);
			return true;
		} catch (SqlJetException e) {
			logger.error("创建[" + db_name + "]失败!", e);
			return false;
		}

	}

	/**
	 * 判断表是否存在
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @param table_name
	 *            表名
	 * @return true - 存在 false - 不存在
	 * @throws SqlJetException
	 *             操作句柄异常
	 */
	public boolean isTableExists(String db_name, String table_name) throws SqlJetException {
		if (null != this.getDb(db_name).getSchema().getTable(table_name)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断数据库文件是否存在
	 * 
	 * @param db_name
	 *            全路径数据库文件名
	 * @return true - 存在 false - 不存在
	 */
	public boolean isDatabaseExists(String db_name) {
		File file = new File(db_name);
		return file.exists();
	}

	/**
	 * @param args
	 */
	// public static void main(String[] args) {
	// ConfigLoader.getIntence().loadConfig();
	// SQLiteHelper dbutil = new SQLiteHelper();
	// String db_name = Config.getResourceConfBean().getResource(
	// Config.RESOURCE_CONFILE_DB);
	//
	// try {
	// ISqlJetTableDef lsjtd = dbutil.getTableDef(db_name, "TBL_SERVERS");
	//
	// ISqlJetTable lsjt = dbutil.getTable(db_name, "TBL_SERVERS");
	//
	// lsjt.insertByFieldNames(null);
	// // lsjt.
	//
	// // dbutil.getDb(db_name);
	// //
	// // List list = lsjtd.getColumns();
	// // List _list = lsjtd.getConstraints();
	//
	// // log.info(list);
	// // log.info(_list);
	// } catch (SqlJetException e) {
	// e.printStackTrace();
	// }
	// }
}
