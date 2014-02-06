package edu.uci.ics.inf225.webcrawler.storage;

public class testHSQLDB {

	public static void main(String[] args) {

		StoreHSQLDB db = new StoreHSQLDB();

		try {
			db.connect();
			db.selectquery("select count(*) from pages;");
			db.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("done");

	}

}
