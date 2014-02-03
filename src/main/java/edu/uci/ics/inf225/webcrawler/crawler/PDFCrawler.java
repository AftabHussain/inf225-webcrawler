package edu.uci.ics.inf225.webcrawler.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

public class PDFCrawler extends WebCrawler {

	public PDFCrawler() {
	}

	@Override
	public boolean shouldVisit(WebURL url) {
		return url.getDomain().endsWith("uci.edu") && url.getPath().toLowerCase().contains(".pdf");
	}

	@Override
	public void visit(Page page) {
		System.out.println(page.getWebURL().getURL());
		String contentData = page.getParseData().toString();
		System.out.println(contentData.substring(0, Math.min(2000, contentData.length())));
	}
}
