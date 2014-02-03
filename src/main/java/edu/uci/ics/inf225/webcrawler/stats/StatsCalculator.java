package edu.uci.ics.inf225.webcrawler.stats;

import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.crawler4j.crawler.Page;

/**
 * Suppose you have 1 {@link Page} with N tokens. In that case, the
 * {@link StatsCalculator#newPage(Page)} method will be called once, the
 * {@link StatsCalculator#newToken(String, int)} will be called N times and
 * {@link StatsCalculator#closePage(Page)} will be called 1.
 * 
 * @author matias
 * 
 */
public class StatsCalculator {

	private Map<String, Integer> subdomains;

	public StatsCalculator() {
		this.subdomains = new HashMap<String, Integer>();
	}

	/**
	 * This method is going to be called for each new crawled {@link Page}.
	 * 
	 * @param page
	 */
	public void newPage(Page page) {
		// TODO Aftab to implement.
	}

	/**
	 * This method is going to be called every time a new token is discovered on
	 * a page.
	 * 
	 * @param token
	 *            The discovered token.
	 * @param ngramity
	 *            It will be 1 if it's a single term. It will be 2 if it's a
	 *            2-gram and so on.
	 */
	public void newToken(String token, int ngramity) {
		// TODO Aftab to implement.
	}

	/**
	 * This method will be called every time a {@link Page} was crawled and does
	 * not have more tokens.
	 * 
	 * @param page
	 */
	public void closePage(Page page) {
		// TODO Aftab to implement.
	}

	private void checkSubdomain(Page page) {
	}

	protected Object getNumberOfPagesForSubdomain(String string) {
		// TODO Auto-generated method stub
		return 2;
	}
}
