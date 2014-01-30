package edu.uci.ics.inf225.webcrawler.indexing;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;

public class LuceneIndexerHelper {

	private static final Logger log = LoggerFactory.getLogger(LuceneIndexerHelper.class);

	private LuceneIndexerHelper() {
	}

	private static TokenStream create2GramTokenStream(Reader reader) {
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

		return result;
	}

	public void index(Page page, IndexWriter writer) throws IOException {
		log.debug("Indexing {}...", page.getWebURL().getURL());
		Document doc = new Document();

		// TODO Compress Strings.
		StringReader contentReader = new StringReader(page.getParseData().toString());
		doc.add(new TextField("terms", contentReader));
		doc.add(new StringField("subdomain", page.getWebURL().getSubDomain(), Store.YES));
		doc.add(new TextField("2-grams", create2GramTokenStream(contentReader)));
		doc.add(new StringField("url", page.getWebURL().getURL(), Store.YES));
		doc.add(new StringField("title", extractTitle(page), Store.YES));
		doc.add(new StringField("description", extractDescription(page), Store.YES));

		// TODO Use different analyzers (Wrappers) for 2grams
		writer.addDocument(doc);
	}

	private String extractDescription(Page page) {
		// TODO Auto-generated method stub
		return "";
	}

	private String extractTitle(Page page) {
		if (page.getParseData() instanceof HtmlParseData) {
			return ((HtmlParseData) page.getParseData()).getTitle();
		} else {
			return "";
		}
	}
}
