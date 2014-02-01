package edu.uci.ics.inf225.webcrawler.tokenizing;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;

public class PageTokenizer {

	private BlockingQueue<Page> pageQueue;

	private static final Logger pageLogger = LoggerFactory.getLogger("pagelogger");
	private static final Logger log = LoggerFactory.getLogger(PageTokenizer.class);

	public PageTokenizer(BlockingQueue<Page> pageQueue) {
		this.pageQueue = pageQueue;
	}

	public void init() {
		// Nothing to do, so far.
	}

	public void start() {
		Executors.newSingleThreadExecutor().execute(new PageTokenizerTask());
	}

	private class PageTokenizerTask implements Runnable {

		private boolean keepWorking;
		private Page page;

		public PageTokenizerTask() {
		}

		@Override
		public void run() {
			keepWorking = true;
			while (keepWorking) {
				try {
					page = pageQueue.take();

					processPage(page);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void processPage(Page page) {
		// TODO Auto-generated method stub
		pageLogger.info("Page visited: {}", page.getWebURL().getURL());
	}
}
