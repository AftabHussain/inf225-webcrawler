package edu.uci.ics.inf225.webcrawler;

import java.util.concurrent.LinkedBlockingQueue;

import crawler.SingleCrawler;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.inf225.webcrawler.tokenizing.PageTokenizer;

public class WebCrawlerController {

	private static final int PAGE_QUEUE_SIZE = 100;
	private CrawlController controller;

	public WebCrawlerController() {
	}

	public void initialize() throws Exception {
		LinkedBlockingQueue<Page> pageQueue = this.initializePageQueue();

		this.initializeCrawler();

		this.initializeTokenizer(pageQueue);

		this.initializeStorage();
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
		PageTokenizer tokenizer = new PageTokenizer(pageQueue);
		tokenizer.init();
		tokenizer.start();

	}

	private void initializeCrawler() throws Exception {
		String crawlStorageFolder = "/data/crawl/root";

		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);

		config.setPolitenessDelay(300);
		config.setMaxDepthOfCrawling(-1);
		config.setMaxPagesToFetch(-1);

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
		controller.addSeed("http://www.ics.uci.edu/~welling/");
		controller.addSeed("http://www.ics.uci.edu/~lopes/");
		controller.addSeed("http://www.ics.uci.edu/");
	}

	public void start() {
		startCrawler();
	}

	private void startCrawler() {
		// TODO Start Crawler here. BLOCKING OPERATION. It will not continue
		// until it
		// finishes.
		int numberOfCrawlers = 7;
		/*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		controller.start(SingleCrawler.class, numberOfCrawlers);
	}
}
