package edu.uci.ics.inf225.webcrawler.stats;

import org.junit.Assert;
import org.junit.Test;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

public class StatsCalculatorTest {

	@Test
	public void test() {
		/*
		 * Expected behavior, observed behavior.
		 */
		Page page1 = createPage();
		Page page2 = createPage();

		// This is the code under test.
		StatsCalculator calculator = new StatsCalculator();

		calculator.newPage(page1);
		calculator.newToken("testtoken", 1);
		calculator.closePage(page1);

		calculator.newPage(page2);
		calculator.newToken("testtoken", 1);
		calculator.closePage(page2);

		// At this point, the maps will contain the accumulated values.
		Assert.assertEquals(2, calculator.getNumberOfPagesForSubdomain("testsubdomain"));
	}

	private Page createPage() {
		return new Page(new WebURL());
	}

}
