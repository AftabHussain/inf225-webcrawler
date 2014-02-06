package edu.uci.ics.inf225.webcrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {

	private static final Logger console = LoggerFactory.getLogger("console");

	public static void main(String[] args) {
		WebCrawlerController controller = new WebCrawlerController();

		console.info("Initializing Web Crawler...");
		try {
			controller.initialize();

			hookShutdownTasks(controller);

			console.info("Starting Web Crawler...");
			controller.start();
			console.info("Web Crawler finished.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0); // TODO Implement stop method.
	}

	private static void hookShutdownTasks(WebCrawlerController controller) {
		Runtime.getRuntime().addShutdownHook(new Thread(new FinalizationTasks(controller)));
	}

	private static class FinalizationTasks implements Runnable {

		WebCrawlerController controller;

		public FinalizationTasks(WebCrawlerController controller) {
			this.controller = controller;
		}

		@Override
		public void run() {
			System.out.println("Executing Shutdown Hook...");
			controller.stop();
		}
	}
}
