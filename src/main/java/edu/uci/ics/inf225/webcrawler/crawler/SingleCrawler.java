package edu.uci.ics.inf225.webcrawler.crawler;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.uci.ics.inf225.webcrawler.CrawlingListener;

public class SingleCrawler extends WebCrawler {

	private static final String EXPECTED_SUBDOMAIN_SUFFIX = ".ics";
	private static final String EXPECTED_DOMAIN = "uci.edu";
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|3gp|ram|m4v|pdf|ppt|xlsx?|docx?|rm|smil|wmv|swf|wma|zip|rar|gz|java|cc|cpp|py))$");

	private Predicate shouldVisitPredicate;

	public SingleCrawler() {
		shouldVisitPredicate = buildShouldVisitPredicate();
	}

	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(WebURL url) {
		return shouldVisitPredicate.evaluate(url);
	}

	/**
	 * Creates a {@link Predicate} to decide whether a {@link WebURL} has to be
	 * visited or not.
	 * 
	 * @return a {@link Predicate} that will return <code>true</code> whether
	 *         the {@link WebURL} must be visited. Otherwise, the
	 *         {@link Predicate} will return <code>false</code>.
	 */
	protected Predicate buildShouldVisitPredicate() {
		List<Predicate> predicates = new LinkedList<>();

		predicates.add(domainPredicate());
		predicates.add(subdomainPredicate());
		predicates.add(extensionsPredicate());
		// Avoid blacklist. (NOT operation).
		predicates.add(PredicateUtils.notPredicate(blackListPrefixesPredicate()));

		return PredicateUtils.allPredicate(predicates);
	}

	/**
	 * Creates a {@link Predicate} to check the page extension.
	 * 
	 * @return A {@link Predicate} that will return <code>true</code> whether
	 *         the {@link WebURL} does NOT matches the
	 *         {@link SingleCrawler#FILTERS} regular expression.
	 */
	private Predicate extensionsPredicate() {
		return new Predicate() {
			// Anonymous class
			@Override
			public boolean evaluate(Object object) {
				String href = ((WebURL) object).getPath().toLowerCase();
				return !FILTERS.matcher(href).matches();
			}
		};
	}

	/**
	 * Checks whether a {@link WebURL}'s prefix is black-listed.
	 * 
	 * @return A {@link Predicate} that will return <code>true</code> if the
	 *         {@link WebURL}'s prefix <b>is</b> present in the black list.
	 */
	private Predicate blackListPrefixesPredicate() {
		final List<String> blackListedPrefixes = new LinkedList<>();

		// Blacklisted URL's prefixes should be added here.
		blackListedPrefixes.add("http://archive.ics.uci.edu/ml/datasets.html?");
		blackListedPrefixes.add("http://calendar.ics.uci.edu/calendar.php?");
		blackListedPrefixes.add("https://duttgroup.ics.uci.edu/doku.php");

		return new Predicate() {

			@Override
			public boolean evaluate(Object object) {
				for (String blackListedPrefix : blackListedPrefixes) {
					if (((WebURL) object).getURL().startsWith(blackListedPrefix)) {
						return true;
					}
				}
				return false;
			}
		};
	}

	/**
	 * Checks whether a {@link WebURL} sub-domain ends with
	 * {@link SingleCrawler#EXPECTED_SUBDOMAIN_SUFFIX}.
	 * 
	 * @return A {@link Predicate} that will return <code>true</code> if the
	 *         {@link WebURL}'s sub-domain ends with
	 *         {@link SingleCrawler#EXPECTED_SUBDOMAIN_SUFFIX}.
	 */
	private Predicate subdomainPredicate() {
		return new Predicate() {

			@Override
			public boolean evaluate(Object object) {
				return ((WebURL) object).getSubDomain().endsWith(EXPECTED_SUBDOMAIN_SUFFIX);
			}
		};
	}

	/**
	 * Checks whether a {@link WebURL} domain is
	 * {@link SingleCrawler#EXPECTED_DOMAIN}.
	 * 
	 * @return A {@link Predicate} that will return <code>true</code> if the
	 *         {@link WebURL}'s domain is {@link SingleCrawler#EXPECTED_DOMAIN}.
	 **/
	private Predicate domainPredicate() {
		return new Predicate() {

			@Override
			public boolean evaluate(Object object) {
				return ((WebURL) object).getDomain().equals(EXPECTED_DOMAIN);
			}
		};
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		// String url = page.getWebURL().getURL();
		// System.out.println("URL: " + url);
		//
		// if (page.getParseData() instanceof HtmlParseData) {
		// HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
		// String text = htmlParseData.getText();
		// String html = htmlParseData.getHtml();
		// List<WebURL> links = htmlParseData.getOutgoingUrls();
		//
		// System.out.println("Text length: " + text.length());
		// System.out.println("Html length: " + html.length());
		// System.out.println("Number of outgoing links: " + links.size());
		// }
		CrawlingListener.processPage(page);
	}
}