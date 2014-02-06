package edu.uci.ics.inf225.webcrawler.stats;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

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

	public Map<String, Integer> subdomains = new HashMap<String, Integer>();
	public Map<String, Integer> words = new HashMap<String, Integer>();
	public Map<String, Integer> two_grams = new HashMap<String, Integer>();
	public Map<String, Integer> pageWords = new HashMap<String, Integer>();
	public int word_count;
	public boolean process_2G;
	public String twoG = "";

	public StatsCalculator() {

	}

	/**
	 * This method is going to be called for each new crawled {@link Page}.
	 * 
	 * @param page
	 */
	public void newPage(Page page) {
		word_count = 0;
		process_2G = false;

		// TODO Aftab to implement.

		processSubdomain(page);
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
	public void newToken(String token) {
		// TODO Aftab to implement.
		if (words.containsKey(token) == true) {
			words.put(token, words.get(token) + 1);
		} else {
			words.put(token, 1);
		}
		word_count++;

		if (process_2G == false) {
			twoG = token;
			process_2G = true;
		} else {
			twoG = twoG + " " + token;
			if (two_grams.containsKey(twoG) == true) {
				two_grams.put(twoG, two_grams.get(twoG) + 1);
			} else {
				two_grams.put(twoG, 1);
			}
			twoG = token;
		}

	}

	/**
	 * This method will be called every time a {@link Page} was crawled and does
	 * not have more tokens.
	 * 
	 * @param page
	 */
	public void closePage(Page page) {
		// TODO Aftab to implement.

		pageWords.put(page.getWebURL().getURL(), word_count);
		twoG = "";
		process_2G = false;
	}

	/*
	 * Adds unique subdomains to the Map
	 */
	private void processSubdomain(Page page) {
		WebURL url = page.getWebURL();
		String path = url.getPath();
		if (subdomains.containsKey(url.getSubDomain()) == true) {
			subdomains.put(url.getSubDomain(),
					subdomains.get(url.getSubDomain()) + 1);
		} else {
			subdomains.put(url.getSubDomain(), 1);
		}
	}

	/*
	 * Returns the number of unique pages.
	 */
	public long getTotalNumberOfUniquePages() {
		return this.pageWords.size();
	}

	/*
	 * Returns a sorted list of subdomains
	 */
	public List<Entry<String, Integer>> getSortedSubdomains() {
		List<Entry<String, Integer>> list = new ArrayList<>(
				this.subdomains.entrySet());

		Collections.sort(list, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});

		return list;
	}

	/*
	 * Returns the URL of the Longest Page, and its length in terms of number of
	 * words.
	 */
	public String getLongestPage() {
		List<Entry<String, Integer>> list = new ArrayList<>(
				this.pageWords.entrySet());

		Collections.sort(list, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		return list.get(list.size() - 1).getKey() + ": "
				+ list.get(list.size() - 1).getValue();
	}

	/*
	 * Returns a list of the 500 most common words
	 */
	public List<Entry<String, Integer>> mostCommonWords() {
		List<Entry<String, Integer>> list = new ArrayList<>(
				this.words.entrySet());
		List<Entry<String, Integer>> list_500 = new ArrayList<>();

		Collections.sort(list, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});

		for (int i = list.size() - 1; i >= Math.max(list.size() - 500, 0); i--) {
			list_500.add(list.get(i));
		}
		return list_500;
	}

	/*
	 * Returns a list of the 20 most common twoGrams
	 */
	public List<Entry<String, Integer>> twoGrams() {
		List<Entry<String, Integer>> list = new ArrayList<>(
				this.two_grams.entrySet());
		List<Entry<String, Integer>> list_20 = new ArrayList<>();

		Collections.sort(list, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});

		for (int i = list.size() - 1; i >= Math.max(list.size() - 20, 0); i--) {
			list_20.add(list.get(i));
		}
		return list_20;
	}

	protected Object getNumberOfPagesForSubdomain(String string) {
		// TODO Auto-generated method stub
		return 2;
	}

	/*
	 * PrintOutPut
	 */
	public void printStatistics() throws Exception {
		List<Entry<String, Integer>> Words = this.mostCommonWords();
		List<Entry<String, Integer>> TwoGrams = this.twoGrams();
		String longestPage_Info = this.getLongestPage();
		FileWriter writer = new FileWriter("Final_Statistics.txt");

		writer.write("Number of unique pages: ");
		writer.write(String.valueOf(pageWords.size()));
		writer.write("\n");

		// writer.write("Unique Pages");
		// for (Iterator iterator = this.pageWords.entrySet().iterator();
		// iterator.hasNext();) {
		// Entry<String,Integer> object = (Entry<String,Integer>)
		// iterator.next();
		// // object.g
		// }
		// writer.println(pageWords);}

		writer.write("Number of subdomains: ");
		writer.write(String.valueOf(subdomains.size()));
		writer.write("\n");

		writer.write("Names of subdomains and the number of pages in each subdomain\n");
		for (Entry<String, Integer> entry : this.subdomains.entrySet()) {
			writer.write(entry.getKey());
			writer.write(": ");
			writer.write(entry.getValue().toString());
			writer.write("\n");
		}

		writer.write("\n");
		writer.write("Longest Page Information: ");
		writer.write(longestPage_Info);
		writer.write("\n");

		writer.write("500 Most Common Words: \n");
		for (int i = 0; i < Words.size(); i++) {
			writer.write(Words.get(i).getKey());
			writer.write(": ");
			writer.write(Words.get(i).getValue().toString());
			writer.write("\n");
		}

		writer.write("\n");
		writer.write("20 most common 2-grams: \n");
		for (int i = 0; i < TwoGrams.size(); i++) {
			writer.write(TwoGrams.get(i).getKey());
			writer.write(": ");
			writer.write(TwoGrams.get(i).getValue().toString());
			writer.write("\n");
		}

		writer.close();

	}
}
