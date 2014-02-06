package edu.uci.ics.inf225.webcrawler.tokenizing;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.inf225.webcrawler.NewPageListener;
import edu.uci.ics.inf225.webcrawler.stats.StatsCalculator;

public class PageTokenizer implements NewPageListener {

	private static final Logger pageLogger = LoggerFactory.getLogger("pagelogger");
	private static final Logger log = LoggerFactory.getLogger(PageTokenizer.class);

	private static final String CONTENT_FIELD = "content";

	private StatsCalculator calculator;

	public PageTokenizer(StatsCalculator calculator) {
		this.calculator = calculator;
	}

	public void init() {
		// Nothing to do, so far.
	}

	public void start() {
		// Nothing to do, so far.
	}

	public void stop() {
		// Nothing to do, so far.
	}

	public void newPage(Page page) {
		pageLogger.info("{},{},{}", page.getWebURL().getURL(), extractContentLength(page), page.getWebURL().getDepth());

		calculator.newPage(page);
		try {
			List<String> tokens = getTerms(page);
			passTermsToCalculator(tokens);

		} catch (IOException e) {
			log.error("Page {} could not be processed.", page.getWebURL());
		} finally {
			calculator.closePage(page);
		}
	}

	private void passTermsToCalculator(List<String> tokens) {
		for (String token : tokens) {
			calculator.newToken(token);
		}
	}

	private List<String> getTerms(Page page) throws IOException {
		return getTokens(page, createLuceneAnalyzer());
	}

	private List<String> getTokens(Page page, Analyzer analyzer) throws IOException {
		ArrayList<String> listOfWords = new ArrayList<String>();
		TokenStream ts;

		ts = analyzer.tokenStream(CONTENT_FIELD, new StringReader(getContentFromPage(page)));

		CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);

		try {
			ts.reset(); // Resets this stream to the beginning.
			while (ts.incrementToken()) {
				listOfWords.add(termAtt.toString());
			}
			ts.end(); // Perform end-of-stream operations, e.g. set
						// the
						// final
						// offset.
		} finally {
			ts.close(); // Release resources associated with this
						// stream.
		}

		return listOfWords;
	}

	private String getContentFromPage(Page page) {
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			return htmlParseData.getText();
		} else {
			return page.getParseData().toString();
		}
	}

	private Analyzer createLuceneAnalyzer() {
		return new SimpleAnalyzer(loadStopWords());
	}

	private List<String> loadStopWords() {
		try {
			List<String> readLines = FileUtils.readLines(new File("cfg/stop_words.txt"));
			readLines.addAll(Arrays.asList(StringUtils.split("a b c d e f g h i j k l m n o p q r s t u v w x y z", " ")));
			return readLines;
		} catch (IOException e) {
			log.error("Stop Words could not be loaded.");
			return new ArrayList<>();
		}
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
