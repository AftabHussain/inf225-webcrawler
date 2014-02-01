package edu.uci.ics.inf225.webcrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {

	private static final Logger console = LoggerFactory.getLogger("console");

	public static void main(String[] args) {
		WebCrawlerController controller = new WebCrawlerController();

		console.info("Initializing Web Crawler...");
		controller.initialize();
		console.info("Starting Web Crawler...");
		controller.start();
		console.info("Web Crawler finished.");
		System.exit(0); // TODO Implement stop method.
	}
}
