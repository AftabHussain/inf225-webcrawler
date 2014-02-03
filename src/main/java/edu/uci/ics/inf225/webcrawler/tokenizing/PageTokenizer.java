package edu.uci.ics.inf225.webcrawler.tokenizing;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;

public class PageTokenizer {

	private BlockingQueue<Page> pageQueue;

	private static final Logger pageLogger = LoggerFactory.getLogger("pagelogger");
	private static final Logger log = LoggerFactory.getLogger(PageTokenizer.class);

	private ExecutorService threadExecutor;

	public PageTokenizer(BlockingQueue<Page> pageQueue) {
		this.pageQueue = pageQueue;
	}

	public void init() {
		// Nothing to do, so far.
	}

	public void start() {
		threadExecutor = Executors.newSingleThreadExecutor();
		threadExecutor.execute(new PageTokenizerTask());
	}

	public void stop() {
		threadExecutor.shutdownNow();
	}

	private class PageTokenizerTask implements Runnable {

		private boolean keepWorking;

		public PageTokenizerTask() {
		}

		@Override
		public void run() {
			keepWorking = true;
			while (keepWorking) {
				try {
					Page page = pageQueue.take();

					processPage(page);
				} catch (InterruptedException e) {
					keepWorking = false;
				}
			}
		}

	}

	public void processPage(Page page) {
		pageLogger.info("{},{}", page.getWebURL().getURL(), extractContentLength(page));
	}

	private Object extractContentLength(Page page) {
		for (int i = 0; i < page.getFetchResponseHeaders().length; i++) {
			if ("Content-Length".equals(page.getFetchResponseHeaders()[i].getName())) {
				return page.getFetchResponseHeaders()[i].getValue();
			}
		}
		return "0";
	}
}
