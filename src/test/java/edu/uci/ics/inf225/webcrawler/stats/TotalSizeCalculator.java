package edu.uci.ics.inf225.webcrawler.stats;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

public class TotalSizeCalculator {

	public TotalSizeCalculator() {
	}

	@Test
	public void calculateSize() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("crawledpages.log"));

		String line;
		long totalSizeBytes = 0L;
		long counter = 0L;
		long zeroCounter = 0L;
		while (true) {
			line = reader.readLine();

			if (line == null) {
				break;
			}

			String numberString = line.substring(line.lastIndexOf(",") + 1, line.length() - 1);
			Long pageContentLength = Long.valueOf(numberString);
			totalSizeBytes += pageContentLength;

			if (pageContentLength == 0) {
				zeroCounter++;
			}
			counter++;
		}
		reader.close();
		long totalSizeMB = totalSizeBytes / 1024 / 1024;
		double percentageOfUnknownPageLengths = (double) zeroCounter / (double) counter;
		System.out.println(totalSizeMB + " (~" + totalSizeMB * (1 + percentageOfUnknownPageLengths) + ") MB ");
		System.out.println(counter + " pages crawled.");
		System.out.println(zeroCounter + " pages with unknown size crawled (" + percentageOfUnknownPageLengths + ").");
	}
}
