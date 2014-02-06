package edu.uci.ics.inf225.webcrawler.storage;

public class testHSQLDB {

	public static void main(String[] args) {
		
		storeHSQLDB db=  new storeHSQLDB();
		
		try {
		db.connect();
		db.selectquery("drop table pages;");
        db.createtables();
        db.update("insert into pages (url, title, Content) values ('www.ics.uci.edu','ICS Title','weqweqwe');");
        db.update("insert into pages (url, title, Content) values ('www.uci.edu','UCI title','weqweqwe');");
        db.insertP("www.google.com", "Title", "Rezvan");
	    db.selectquery("select * from pages;");
	    db.shutdown();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println("done");
		
	}
	
}
