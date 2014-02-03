/**
 * 
 */
package edu.uci.ics.inf225.webcrawler.storage;

import java.io.FileWriter;
import java.io.IOException;

import edu.uci.ics.crawler4j.crawler.Page;

/**
 * @author mgiorgio
 * 
 */
public class PageStore {

	private long pagesPerFileCounter;

	private long totalPagesCounter;

	private long filesCounter;

	private FileWriter currentWriter;

	private static final int PAGES_PER_FILE = 200;

	private static final String STORAGE_DIR = "storage_data";
	private static final String FILES_PREFIX = "crwl_";
	private static final String FILES_SUFFIX = ".dat";

	/**
	 * 
	 */
	public PageStore() {
		pagesPerFileCounter = 0;
		filesCounter = 0;
		totalPagesCounter = 0;
	}

	public void close() throws IOException {
		if (currentWriter != null) {
			currentWriter.close();
		}
	}

	public void storePage(Page page) throws IOException {
		FileWriter writer = getCurrentWriter();
		pagesPerFileCounter++;
		totalPagesCounter++;

		writePage(writer, page);
	}

	private void writePage(FileWriter writer, Page page) throws IOException {
		writer.write(page.getWebURL().getURL());
		writer.write("\n");
		writer.write(page.getParseData().toString());
	}

	private FileWriter getCurrentWriter() throws IOException {
		if (totalPagesCounter == 0 || pagesPerFileCounter == PAGES_PER_FILE) {
			updateCurrentWriter();
		}
		return currentWriter;
	}

	private void updateCurrentWriter() throws IOException {
		pagesPerFileCounter = 0;
		String fileName = new StringBuilder().append(STORAGE_DIR).append("/").append(FILES_PREFIX).append(filesCounter++).append(FILES_SUFFIX).toString();
		if (currentWriter != null) {
			// There's no "previous" writer in the first time.
			currentWriter.close();
		}
		currentWriter = new FileWriter(fileName);
	}
}
