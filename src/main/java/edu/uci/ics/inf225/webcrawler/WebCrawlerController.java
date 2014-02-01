package edu.uci.ics.inf225.webcrawler;

import java.util.concurrent.LinkedBlockingQueue;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.inf225.webcrawler.tokenizing.PageTokenizer;

public class WebCrawlerController {

	private static final int PAGE_QUEUE_SIZE = 100;

	public WebCrawlerController() {
	}

	public void initialize() {
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

	private void initializeCrawler() {
		// TODO Initialize Crawler. DO NOT START IT.

	}

	public void start() {
		startCrawler();
	}

	private void startCrawler() {
		// TODO Start Crawler here. BLOCKING OPERATION. It will not continue
		// until it
		// finishes.
	}
}
