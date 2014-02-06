package edu.uci.ics.inf225.webcrawler.storage;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.inf225.webcrawler.NewPageListener;

public class PageStorage implements NewPageListener {

	private static final int MAX_TEXT_TITLE_LENGTH = 30;
	private StoreHSQLDB hsqldb;

	private static final Logger log = LoggerFactory.getLogger(PageStorage.class);

	private boolean resume;

	public PageStorage(boolean resume) {
		this.resume = resume;
		hsqldb = new StoreHSQLDB();
	}

	public void init() {
		try {
			try {
				hsqldb.connect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!resume) {
				hsqldb.deleteTable("pages");
			}
			hsqldb.createtables();
		} catch (SQLException e) {
			log.warn("Table 'pages' could not be recreated.");
		}
	}

	public PageStorage() {
		this(false);
	}

	@Override
	public void newPage(Page page) {
		String title;
		String content;
		if (page.getParseData() instanceof HtmlParseData) {
			title = ((HtmlParseData) page.getParseData()).getTitle();
			content = ((HtmlParseData) page.getParseData()).getHtml();
		} else {
			content = page.getParseData().toString();
			title = content.substring(0, Math.min(content.length(), MAX_TEXT_TITLE_LENGTH));
		}
		try {
			hsqldb.insertP(page.getWebURL().getURL(), title, content);
		} catch (SQLException e) {
			log.error("URL {} could not be stored: {}", page.getWebURL().getURL(), e.getMessage());
		}
	}

	public void stop() {
		try {
			hsqldb.shutdown();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
