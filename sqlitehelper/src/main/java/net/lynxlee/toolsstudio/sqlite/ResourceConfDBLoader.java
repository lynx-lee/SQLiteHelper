package net.lynxlee.toolsstudio.sqlite;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceConfDBLoader {

	private static SQLiteHelper dbutil = new SQLiteHelper();
	private static Logger logger = LoggerFactory
			.getLogger(ResourceConfDBLoader.class);

	public static void loadConfig() throws IOException {

//		String db_name = Config.getResourceConfBean().getResource(
//				Config.RESOURCE_CONFILE_DB);
//		db_name = Util.getConfigPath() + db_name;
//		if (!dbutil.isDatabaseExists(db_name)) {
//			for (String sql : Config.getInitSQLConfBean().getInitResourceSql()) {
//				dbutil.createTable(db_name, sql);
//			}
//		}
	}
}
