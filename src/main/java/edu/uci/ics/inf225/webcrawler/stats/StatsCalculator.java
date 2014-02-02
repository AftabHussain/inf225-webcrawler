package edu.uci.ics.inf225.webcrawler.stats;

import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.crawler4j.crawler.Page;

public class StatsCalculator {

	private Set<String> subdomains;

	public StatsCalculator() {
		this.subdomains = new HashSet<String>();
	}

	public void processPage(Page page) {
		checkSubdomain(page);
	}

	private void checkSubdomain(Page page) {
		if (!this.subdomains.contains(page.getWebURL().getSubDomain())) {
			this.subdomains.add(page.getWebURL().getSubDomain());
		}
	}
}
