package edu.uci.ics.inf225.webcrawler.stats;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
		Page page1 = createPage("ics", "uci.edu", "index.html");
		Page page2 = createPage("ics", "uci.edu", "index2.html");
		Page page3 = createPage("www.ics", "uci.edu", "index3.html");

		// This is the code under test.
		StatsCalculator calculator = new StatsCalculator();

		calculator.newPage(page1);
		calculator.newToken("token1");
		calculator.newToken("token2");
		calculator.closePage(page1);

		calculator.newPage(page2);
		calculator.newToken("token1");
		calculator.newToken("token2");
		calculator.closePage(page2);

		calculator.newPage(page3);
		calculator.newToken("token3");
		calculator.newToken("token4");
		calculator.closePage(page3);

		/*
		 * Subdomains: ics, www.ics (2) tokens: token1, token2, token3, token4
		 * (4) 2 grams = "token1 token2" (2) "token 3 token4" (1) [2 different
		 * 2-grams] Number of tokens per page: p1 = 2. p2 = 2; p3=2. Nr of pages
		 * per subdomain: s1 = 2. s2 = 1.
		 */

		// At this point, the maps will contain the accumulated values.
		Assert.assertEquals("The total number of unique pages is wrong.", 3,
				calculator.getTotalNumberOfUniquePages());
		Assert.assertEquals(
				"The number of subdomains is not the expected one.", 2,
				calculator.subdomains.size());
		Set<String> wordSet = new HashSet<>(Arrays.asList("token1", "token2",
				"token3", "token4"));
		Assert.assertEquals(
				"The stored tokens are different from the expected ones..",
				wordSet, calculator.words.keySet());
		Set<String> _2gramSet = new HashSet<>(Arrays.asList("token1 token2",
				"token3 token4"));
		Assert.assertEquals("The 2-grams are not the expected ones.",
				_2gramSet, calculator.two_grams.keySet());
		Assert.assertEquals(
				"Page 1 does not contain the right number of terms", 2,
				calculator.pageWords.get(page1.getWebURL().getURL()).intValue());
		Assert.assertEquals(
				"Page 2 does not contain the right number of terms", 2,
				calculator.pageWords.get(page2.getWebURL().getURL()).intValue());
		Assert.assertEquals(
				"Page 3 does not contain the right number of terms", 2,
				calculator.pageWords.get(page3.getWebURL().getURL()).intValue());
		Assert.assertEquals("Token 1 count is wrong ", 2,
				calculator.words.get("token1").intValue());
		Assert.assertEquals("Token 2 count is wrong ", 2,
				calculator.words.get("token2").intValue());
		Assert.assertEquals("Token 3 count is wrong ", 1,
				calculator.words.get("token3").intValue());
		Assert.assertEquals("Token 4 count is wrong ", 1,
				calculator.words.get("token4").intValue());
		Assert.assertEquals("2 Gram count of /token1 token2/ is wrong ", 2,
				calculator.two_grams.get("token1 token2").intValue());

		try {
			calculator.printStatistics();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Page createPage(String subdomain, String domain, String page) {
		String fullURL = "http://" + subdomain + "." + domain + "/" + page;
		WebURL url = new WebURL();
		url.setURL(fullURL);
		return new Page(url);
	}
}
