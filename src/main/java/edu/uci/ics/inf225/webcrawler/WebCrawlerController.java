package edu.uci.ics.inf225.webcrawler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.inf225.webcrawler.crawler.SingleCrawler;
import edu.uci.ics.inf225.webcrawler.stats.StatsCalculator;
import edu.uci.ics.inf225.webcrawler.tokenizing.PageTokenizer;

public class WebCrawlerController {

	private static final int PAGE_QUEUE_SIZE = 100;
	private CrawlController controller;

	private StatsCalculator calculator;

	private static final Logger console = LoggerFactory.getLogger("console");

	public WebCrawlerController() {
	}

	public void initialize() throws Exception {
		LinkedBlockingQueue<Page> pageQueue = this.initializePageQueue();

		this.initializeCrawler();

		this.initializeTokenizer(pageQueue);

		this.initializeStorage();

		this.initializeStatsCalculator();
	}

	private void initializeStatsCalculator() {
		// TODO Auto-generated method stub

	}

	private LinkedBlockingQueue<Page> initializePageQueue() {
		LinkedBlockingQueue<Page> queue = new LinkedBlockingQueue<Page>(PAGE_QUEUE_SIZE);
		CrawlingListener.setPageQueue(queue);
		return queue;
	}

	private void initializeStorage() {
		// TODO Initialize storage.
		// HSQLDB stuff here.

	}

	private void initializeTokenizer(LinkedBlockingQueue<Page> pageQueue) {
		// TODO Initialize tokenizer.
		// Lucene tokenizer here.
		PageTokenizer tokenizer = new PageTokenizer(pageQueue, calculator);
		tokenizer.init();
		tokenizer.start();

	}

	private void initializeCrawler() throws Exception {
		String crawlStorageFolder = "data/crawl/root";

		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);

		config.setPolitenessDelay(300);
		config.setMaxDepthOfCrawling(-1);
		config.setMaxPagesToFetch(-1);
		config.setIncludeHttpsPages(true);
		config.setUserAgentString("UCI IR 33687990-62921655-43242954");

		/*
		 * Instantiate the controller for this crawl.
		 */
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		controller = new CrawlController(config, pageFetcher, robotstxtServer);

		/*
		 * For each crawl, you need to add some seed urls. These are the first
		 * URLs that are fetched and then the crawler starts following links
		 * which are found in these pages
		 */
		controller.addSeed("http://www.ics.uci.edu/");
	}

	public void start() {
		long beforeCrawling = System.nanoTime();
		startCrawler();
		long afterCrawling = System.nanoTime();
		long crawlingTime = TimeUnit.NANOSECONDS.toMillis(afterCrawling - beforeCrawling);

		console.info("Total crawling time: {}", crawlingTime);
	}

	private void startCrawler() {
		int numberOfCrawlers = 20;
		/*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		controller.start(SingleCrawler.class, numberOfCrawlers);
	}

	public void stop() {
		// this.calculator.calculate();
	}
}
