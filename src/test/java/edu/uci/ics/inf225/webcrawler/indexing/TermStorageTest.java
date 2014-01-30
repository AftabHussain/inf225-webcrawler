package edu.uci.ics.inf225.webcrawler.indexing;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class TermStorageTest {

	private static final String _2_GRAMS_FIELD = "2-grams";
	private static final String TERMS_FIELD = "terms";

	@Test
	public void seeHowTermsAreStoredInLucene() throws IOException {
		final String text1 = "It is a truth universally acknowledged, that a single man in possession of a good fortune, ";
		final String text2 = "must be in want of a wife";

		/*
		 * Store the indices in RAM (for testing).
		 */
		RAMDirectory directory = new RAMDirectory();

		/*
		 * Index the two sentences above.
		 */
		indexText(directory, text1, text2);

		/*
		 * Prints the terms and frequencies from those texts.
		 */
		printTermsAndFrequencies(directory);
	}

	/**
	 * Reads the given {@link Directory} and prints the terms and frequencies.
	 * 
	 * @param directory
	 * @throws IOException
	 */
	private void printTermsAndFrequencies(RAMDirectory directory) throws IOException {
		DirectoryReader directoryReader = DirectoryReader.open(directory);

		List<AtomicReaderContext> readers = directoryReader.leaves();

		for (AtomicReaderContext atomicReaderContext : readers) {
			AtomicReader atomicReader = atomicReaderContext.reader();

			printField(directoryReader, atomicReader, TERMS_FIELD);
			printField(directoryReader, atomicReader, _2_GRAMS_FIELD);
		}
	}

	/**
	 * Prints terms and frequencies in the given {@link DirectoryReader} and
	 * {@link AtomicReader} for the field.
	 * 
	 * @param directoryReader
	 * @param atomicReader
	 * @param field
	 * @throws IOException
	 */
	private void printField(DirectoryReader directoryReader, AtomicReader atomicReader, String field) throws IOException {
		System.out.println("=== " + field + ": " + directoryReader.getSumTotalTermFreq(TERMS_FIELD) + " ===");
		TermsEnum termsIterator = MultiFields.getTerms(atomicReader, field).iterator(null);
		BytesRef text;
		while ((text = termsIterator.next()) != null) {
			System.out.println(text.utf8ToString() + ": " + termsIterator.totalTermFreq());
		}
		System.out.println();
	}

	private void indexText(Directory directory, String... texts) throws IOException {
		Map<String, Analyzer> fieldAnalyzers = new HashMap<String, Analyzer>();

		Analyzer termsAnalyzer = createSingleTermsAnalyzer();
		fieldAnalyzers.put(TERMS_FIELD, termsAnalyzer);

		Analyzer _2gramsAnalyzer = create2GramsAnalyzer();
		fieldAnalyzers.put(_2_GRAMS_FIELD, _2gramsAnalyzer);

		PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(Version.LUCENE_46), fieldAnalyzers);

		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		conf.setOpenMode(OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(directory, conf);

		for (int i = 0; i < texts.length; i++) {
			Document doc = new Document();
			doc.add(new TextField(TERMS_FIELD, new StringReader(texts[i])));
			doc.add(new TextField(_2_GRAMS_FIELD, new StringReader(texts[i])));

			writer.addDocument(doc);
		}

		writer.maybeMerge();
		writer.close();
	}

	private Analyzer createSingleTermsAnalyzer() {
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

				TokenStreamComponents components = new TokenStreamComponents(source, result);

				return components;
			}

		};
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

}
