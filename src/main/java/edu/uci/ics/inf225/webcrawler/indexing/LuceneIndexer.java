package edu.uci.ics.inf225.webcrawler.indexing;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;

public class LuceneIndexer {

	private BlockingQueue<Page> queue;

	private static final Logger log = LoggerFactory.getLogger(LuceneIndexer.class);

	public LuceneIndexer(BlockingQueue<Page> queue) {
		this.queue = queue;
	}

	public void start() {

	}

	private class IndexingTask implements Runnable {

		private volatile boolean keepWorking;

		public IndexingTask() {
		}

		@Override
		public void run() {
			keepWorking = true;
			while (keepWorking) {
				try {
					Page page = queue.take();

					processPage(page);
				} catch (InterruptedException e) {
					log.info("Stopping Indexing Task.");
				}
			}
		}

		private void processPage(Page page) {
			// TODO Auto-generated method stub

		}
	}

}
