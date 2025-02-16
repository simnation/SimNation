package org.simnation.zzz_old;

import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

/**
 * Data Access Object serving as bridge to store the simulation scenario in a database. Uses DataNucleus as JDO
 * implementation and is used in the editor (store) and simulator (load) likewise.
 *
 * @author Rene Kuhlemann
 *
 */
public final class DataAccessObjectFactory implements AutoCloseable {

	public enum DB_TYPE {
		SQLITE("SQLite","jdbc:sqlite:%1$s.db","org.sqlite.JDBC"),
		H2("H2","jdbc:h2:file:%1$s","org.h2.Driver"),
		// BERKELEY("Berkeley","jdbc:berkeley:%1$s","org.berkeley.JDBCDriver"),
		// MYSQL("MySQL","jdbc:mysql:%1$s","com.mysql.jdbc.Driver"),
		HSQLDB("HSQLDB","jdbc:hsqldb:file:%1$s","org.hsqldb.jdbc.JDBCDriver"),
		POSTGRESQL("PostGRESQL","jdbc:postgresql:%1$s","org.postgresql.Driver");

		String name,protocol,driver;

		DB_TYPE(String name,String protocol,String driver) {
			this.name=name;
			this.protocol=protocol;
			this.driver=driver;
		}

		public String toString() {
			return name;
		}
	}

	private static final String PERSISTENCE_UNIT_NAME="Database";

	public static final DB_TYPE DEFAULT_TYPE=DB_TYPE.H2;
	public static final String DEFAULT_PATH=System.getProperty("java.io.tmpdir");
	public static final String DEFAULT_NAME="scenario";

	private final PersistenceManagerFactory pmf;
	private final PersistenceManager pm;

	public DataAccessObjectFactory(DB_TYPE db_type,String db_path,String db_name,String usr,String pwd) throws Exception {
		final String url=String.format(db_type.protocol,normalizePath(db_path)+db_name);
		final Map<String,String> options=new HashMap<>();
		options.put("javax.jdo.option.ConnectionDriverName",db_type.driver);
		options.put("javax.jdo.option.ConnectionURL",url);
		options.put("javax.jdo.option.ConnectionUserName",usr);
		options.put("javax.jdo.option.ConnectionPassword",pwd);
		//options.put("datanucleus.generateSchema.database.mode","drop-and-create");
		options.put("datanucleus.autoCreateSchema","true");
		options.put("datanucleus.rdbms.stringDefaultLength","32");
		pmf=JDOHelper.getPersistenceManagerFactory(options,PERSISTENCE_UNIT_NAME);
		pm=pmf.getPersistenceManager();
	}

	/**
	 * Constructor for embedded databases without username and password
	 *
	 * @param db_type
	 * @param db_path
	 * @param db_name
	 * @throws Exception
	 */
	public DataAccessObjectFactory(DB_TYPE db_type,String db_path,String db_name) throws Exception {
		this(db_type,db_path,db_name,"","");
	}

	/**
	 * Constructor with default options
	 *
	 */
	public DataAccessObjectFactory() throws Exception {
		this(DEFAULT_TYPE,DEFAULT_PATH,DEFAULT_NAME);
	}

	public void close() {
		if (pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
		pm.close();
		pmf.close();
	}

	public <T> List<T> load(Class<T> clazz) {
		return query("SELECT FROM "+clazz.getName());
	}
	
	public <T> List<T> load(Class<T> clazz,int region) {
		return query("SELECT FROM "+clazz.getName()+" WHERE region.id=="+region);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> query(String jdoql) {
		final Query query=pm.newQuery(jdoql);
		return (List<T>) query.execute();
	}

	/**
	 * Returns the number of elements of the specific class within a region. 
	 * 
	 * @param clazz - respective class, i.e. name of table
	 * @param region - region, has to be a column in the class's table!
	 * @return number of elements
	 */
	public <T> int count(Class<T> clazz,int region) {
		final Query query=pm.newQuery("SELECT count(region) FROM "+clazz.getName()+" WHERE region.id=="+region);
		final long result=(Long) query.execute();
		return (int) result;
	}

	public void store(List<?> list) throws Exception {
		pm.currentTransaction().begin();
		pm.makePersistentAll(list);
		pm.currentTransaction().commit();
	}

	public static String[] getDatabaseDriverSelection() {
		return new String[] {DB_TYPE.SQLITE.toString(),DB_TYPE.H2.toString(),DB_TYPE.HSQLDB.toString()};
	}

	public static String testConnectionSettings(DB_TYPE db_type,String db_path,String db_name) {
		return testConnectionSettings(db_type,db_path,db_name,null,null);
	}

	public static String testConnectionSettings(DB_TYPE db_type,String db_path,String db_name,String usr,String pwd) {
		try {
			Class.forName(db_type.driver).newInstance();
			final String url=String.format(db_type.protocol,normalizePath(db_path)+db_name);
			DriverManager.getConnection(url,usr,pwd).close();
		}
		catch (final Exception exception) {
			exception.printStackTrace();
			return exception.toString();
		}
		return null;
	}

	private static String normalizePath(String p) {
		final StringBuffer path=new StringBuffer(p);
		if (!p.endsWith("\\")&&!p.endsWith("/")) path.append('/');
		for (int index=0; index<path.length(); index++)
			// if (buf.charAt(index)=='/') buf.setCharAt(index,'\\');
			if (path.charAt(index)=='\\') path.setCharAt(index,'/');
		return path.toString();
	}

}
