package edu.uci.ics.inf225.webcrawler;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;

/**
 * This class is responsible for performing ALL of the operations over the
 * crawled {@link Page}s.
 * 
 * @author mgiorgio
 * 
 */
public class CrawlingListener {

	private static BlockingQueue<Page> pageQueue;

	private static final Logger log = LoggerFactory.getLogger(CrawlingListener.class);

	private CrawlingListener() {
	}

	public static void setPageQueue(BlockingQueue<Page> queue) {
		pageQueue = queue;
	}

	/**
	 * Puts the {@link Page} into the static {@link BlockingQueue}. This is a
	 * <b>blocking</b> operation.
	 * 
	 * @param page
	 *            The page to process.
	 */
	public static void processPage(Page page) {
		try {
			pageQueue.put(page);
		} catch (InterruptedException e) {
			log.info("Crawling Listener has been interrupted: " + e.getMessage());
		}
	}

}
