package edu.uci.ics.inf225.webcrawler;

import edu.uci.ics.crawler4j.crawler.Page;

public interface NewPageListener {

	/**
	 * Tells this listener that a new {@link Page} is available.
	 * 
	 * @param page
	 *            The new available {@link Page}.
	 */
	public void newPage(Page page);
}
