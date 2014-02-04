package edu.uci.ics.inf225.webcrawler.tokenizing;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.inf225.webcrawler.stats.StatsCalculator;

public class PageTokenizer {

	private BlockingQueue<Page> pageQueue;

	private static final Logger pageLogger = LoggerFactory.getLogger("pagelogger");
	private static final Logger log = LoggerFactory.getLogger(PageTokenizer.class);

	private static final String CONTENT_FIELD = "content";

	private ExecutorService threadExecutor;

	private StatsCalculator calculator;

	public PageTokenizer(BlockingQueue<Page> pageQueue, StatsCalculator calculator) {
		this.pageQueue = pageQueue;
		this.calculator = calculator;
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

		calculator.newPage(page);
		try {
			List<String> tokens = getTerms(page);
			passTermsToCalculator(tokens, 1);

			List<String> _2grams = get2Grams(page);
			passTermsToCalculator(_2grams, 2);

		} catch (IOException e) {
			log.error("Page {} could not be processed.", page.getWebURL());
		} finally {
			calculator.closePage(page);
		}
	}

	private void passTermsToCalculator(List<String> tokens, int gramity) {
		for (String token : tokens) {
			calculator.newToken(token, gramity);
		}
	}

	private Analyzer create2GramsAnalyzer() {
		return new Analyzer() {

			@Override
			protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
				/*
				 * Splits input by white space.
				 */
				WhitespaceTokenizer source = new WhitespaceTokenizer(Version.LUCENE_46, reader);

				TokenStream result = null;

				/*
				 * Removes possessive 's from words. Also, splits words by
				 * non-alphanumeric characters such as slash, hyphens, etc.
				 */
				int configurationFlags = WordDelimiterFilter.STEM_ENGLISH_POSSESSIVE | WordDelimiterFilter.GENERATE_WORD_PARTS | WordDelimiterFilter.GENERATE_NUMBER_PARTS;
				result = new WordDelimiterFilter(source, configurationFlags, null);

				/*
				 * Normalize tokens to lower-case.
				 */
				result = new LowerCaseFilter(Version.LUCENE_46, result);

				// result = new NGramTokenFilter(Version.LUCENE_46, result, 2,
				// 2);
				ShingleFilter shingleFilter = new ShingleFilter(result, 2, 2);
				shingleFilter.setOutputUnigrams(false);
				result = shingleFilter;

				TokenStreamComponents components = new TokenStreamComponents(source, result);

				return components;
			}

		};
	}

	private List<String> getTerms(Page page) throws IOException {
		return getTokens(page, createLuceneAnalyzer());
	}

	private List<String> get2Grams(Page page) throws IOException {
		return getTokens(page, create2GramsAnalyzer());
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
			return htmlParseData.getHtml();
		} else {
			return page.getParseData().toString();
		}
	}

	private Analyzer createLuceneAnalyzer() {
		return new SimpleAnalyzer();
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
