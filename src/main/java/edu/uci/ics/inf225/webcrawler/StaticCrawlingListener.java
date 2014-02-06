package edu.uci.ics.inf225.webcrawler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

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
public class StaticCrawlingListener {

	private static BlockingQueue<Page> pageQueue;

	private static final int PAGE_QUEUE_SIZE = 100;

	private static List<NewPageListener> listeners = new LinkedList<>();

	private static final Logger log = LoggerFactory.getLogger(StaticCrawlingListener.class);

	private static ExecutorService threadExecutor;

	static {
		initializePageQueue();
		threadExecutor = Executors.newSingleThreadExecutor();
		threadExecutor.execute(new ProcessPageTask());
	}

	private static void initializePageQueue() {
		pageQueue = new LinkedBlockingQueue<Page>(PAGE_QUEUE_SIZE);
	}

	private StaticCrawlingListener() {
	}

	public static void setPageQueue(BlockingQueue<Page> queue) {
		pageQueue = queue;
	}

	public static void addListener(NewPageListener listener) {
		listeners.add(listener);
	}

	/**
	 * Puts the {@link Page} into the static {@link BlockingQueue}. This is a
	 * <b>blocking</b> operation.
	 * 
	 * @param page
	 *            The page to process.
	 */
	public static void newPage(Page page) {
		try {
			pageQueue.put(page);
		} catch (InterruptedException e) {
			log.info("Crawling Listener has been interrupted: " + e.getMessage());
		}
	}

	public static void stop() {
		threadExecutor.shutdownNow();
	}

	private static class ProcessPageTask implements Runnable {

		private boolean keepWorking;

		public ProcessPageTask() {
		}

		@Override
		public void run() {
			keepWorking = true;
			while (keepWorking) {
				try {
					Page page = pageQueue.take();

					dispatchPage(page);
				} catch (InterruptedException e) {
					keepWorking = false;
				}
			}
		}

		private void dispatchPage(Page page) {
			for (NewPageListener listener : listeners) {
				listener.newPage(page);
			}
		}

	}

}
