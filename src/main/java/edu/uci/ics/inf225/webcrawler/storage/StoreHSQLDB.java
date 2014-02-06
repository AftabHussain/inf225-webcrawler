package edu.uci.ics.inf225.webcrawler.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Rezvan
 * 
 */
public class StoreHSQLDB {

	public Connection conn = null;
	private PreparedStatement ps;

	public void connect() throws Exception {
		Class.forName("org.hsqldb.jdbcDriver");
		conn = DriverManager.getConnection("jdbc:hsqldb:file:crawlerdb", "sa",
				"");
	}

	public void shutdown() throws SQLException {
		Statement st = conn.createStatement();
		st.execute("SHUTDOWN");
		conn.close();
	}

	public synchronized void selectquery(String expression) throws SQLException {
		Statement st = null;
		ResultSet rs = null;
		st = conn.createStatement();
		rs = st.executeQuery(expression); // run the select query

		// do something with the result set.
		dump(rs);
		st.close();
	} // void update()

	public synchronized void createtables() {
		Statement st = null;
		try {
			st = conn.createStatement();
			st.execute("CREATE TABLE pages ( page_id INTEGER IDENTITY, url VARCHAR(1000), title VARCHAR(300), content CLOB)");
			// st.execute("CREATE TABLE terms ( term_id INTEGER IDENTITY, term VARCHAR(300), gram INTEGER)");
			// st.execute("CREATE TABLE postings ( term_id INTEGER, page_id INTEGER, frequency INTEGER)");
			st.close();
			ps = createPreparedStatement();
		} catch (SQLException e) {
			// tables are existed
			
			e.printStackTrace();
		}
	}

	// Uses Prepared Statement to insert pages
	public synchronized void insertP(String url, String title, String content)
			throws SQLException {

		ps.setString(1, url);
		ps.setString(2, title);
		ps.setClob(3, new StringReader(content));

		ps.execute();
	}

	private PreparedStatement createPreparedStatement() throws SQLException {
		String str = "insert into pages (url, title, Content) values (?,?,?)";

		PreparedStatement ps = conn.prepareStatement(str);
		return ps;
	}

	public synchronized void update(String expression) throws SQLException {
		Statement st = null;
		st = conn.createStatement(); // statements
		int i = st.executeUpdate(expression); // run the query
		if (i == -1) {
			System.out.println("db error : " + expression);
		}
		st.close();
	} // void update()

	public static void dump(ResultSet rs) throws SQLException {
		// while (rs.next()) {
		// System.out.println(rs.getString("url"));

		ResultSetMetaData meta = rs.getMetaData();
		int colmax = meta.getColumnCount();
		int i;
		Object o = null;

		for (; rs.next();) {
			for (i = 0; i < colmax; ++i) {
				o = rs.getObject(i + 1); // Is SQL the first column is indexed

				if(o instanceof Clob) {
					Reader reader = ((Clob)o).getCharacterStream();
					BufferedReader bufferedReader = new BufferedReader(reader);
					
					try {
						System.out.println(bufferedReader.readLine());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// with 1 not 0
					System.out.print(o.toString() + " ");
				}
				
			}
			System.out.println(" ");
		}
	}

	public void createBlob(String ht) throws SQLException {

		Blob myClob = conn.createBlob();

	}

}