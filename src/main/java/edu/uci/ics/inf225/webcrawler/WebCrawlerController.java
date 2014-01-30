package edu.uci.ics.inf225.webcrawler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.inf225.webcrawler.indexing.LuceneIndexer;

public class WebCrawlerController {

	private static final int QUEUE_SIZE = 50;

	public WebCrawlerController() {
	}

	public void start() {
		BlockingQueue<Page> queue = new LinkedBlockingQueue<>(QUEUE_SIZE);
		this.startIndexer(queue);
		this.startCrawler4j(queue);
	}

	private void startIndexer(BlockingQueue<Page> queue) {
		LuceneIndexer indexer = new LuceneIndexer(queue);
	}

	private void startCrawler4j(BlockingQueue<Page> queue) {
		// TODO Auto-generated method stub

	}
}
