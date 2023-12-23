package com.sorbonne.daar.utils.keywords;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

import com.sorbonne.daar.utils.graph.Jaccard;
import com.sorbonne.daar.utils.graph.JaccardMatrice;

public class MotsClesExtractor {

	public static String stem(String term) throws IOException {

		TokenStream tokenStream = null;
		try {

			// We tokenize the input
			tokenStream = new ClassicTokenizer(Version.LUCENE_40, new StringReader(term));
			// stem
			tokenStream = new PorterStemFilter(tokenStream);

			// Add each token in a set, so that duplicates are removed
			Set<String> stems = new HashSet<String>();
			CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				stems.add(token.toString());
			}

			// if no stem or 2+ stems have been found, return null
			if (stems.size() != 1) {
				return null;
			}
			String stem = stems.iterator().next();
			// if the stem has non-alphanumerical chars, return null
			if (!stem.matches("[a-zA-Z0-9-]+")) {
				return null;
			}

			return stem;

		} finally {
			if (tokenStream != null) {
				tokenStream.close();
			}
		}
	}

	/**
	 * Buld keyword list using english stop words
	 */
	public static List<MotCle> buildKeyWordsList(String input) throws IOException {

		TokenStream tokenStream = null;
		try {

			// Hack to keep dashed words (e.g. "non-specific" rather than "non" and
			// "specific")
			input = input.replaceAll("-+", "-0");
			
			// Replace any punctuation char but apostrophes and dashes by a space
			input = input.replaceAll("[\\p{Punct}&&[^'-]]+", " ");
			
			// Replace most common english contractions
			input = input.replaceAll("(?:'(?:[tdsm]|[vr]e|ll))+\\b", "");

			// tokenize input
			tokenStream = new ClassicTokenizer(Version.LUCENE_40, new StringReader(input));
			
			// to lowercase
			tokenStream = new LowerCaseFilter(Version.LUCENE_40, tokenStream);
			
			// remove dots from acronyms (and "'s" but already done manually above)
			tokenStream = new ClassicFilter(tokenStream);
			
			// convert any char to ASCII
			tokenStream = new ASCIIFoldingFilter(tokenStream);
			
			// We remove the english stop words (ignored words because they are too common)
			// We build more stop words on top of the default set (like gutenberg)
			Scanner s = new Scanner(new File("stopWords.txt"));
			ArrayList<String> stopWordsList = new ArrayList<>();
			while(s.hasNextLine()) {
				stopWordsList.add(s.nextLine());
			}
			s.close();
			
			final CharArraySet cas = new CharArraySet(Version.LUCENE_40, stopWordsList, true);
			final CharArraySet defaultSet = EnglishAnalyzer.getDefaultStopSet();
			cas.addAll(defaultSet);
			tokenStream = new StopFilter(Version.LUCENE_40, tokenStream, cas);

			List<MotCle> keywords = new LinkedList<MotCle>();
			CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				String term = token.toString();
				// Stem each term
				String stem = stem(term);
				if (stem != null) {
					// Create the keyword or get the existing one if any
					MotCle keyword = find(keywords, new MotCle(stem.replaceAll("-0", "-")));
					// Add its corresponding initial token
					keyword.add(term.replaceAll("-0", "-"));
				}
			}

			// Reverse sort by frequency
			Collections.sort(keywords);
			// We keep the 50 most used keywords per text
			keywords = keywords.stream().limit(50).collect(Collectors.toList());
			return keywords;

		} finally {
			if (tokenStream != null) {
				tokenStream.close();
			}
		}

	}

	/**
	 * Generic algorithm to search through a collection
	 */
	public static <T> T find(Collection<T> collection, T example) {
		for (T element : collection) {
			if (element.equals(example)) {
				return element;
			}
		}
		collection.add(example);
		return example;
	}
	
	
	/**
	 * Build the keywords in a .ser file using apache lucene
	 */
	private static void buildKeywordsFile() throws FileNotFoundException, IOException {
		MotCleMap mcm = new MotCleMap();
		HashMap<String, List<Integer>> mcMap = mcm.getMotCleMap();
		
		for (int i = 0; i < 1664; i++) {
			String text = Jaccard.readFile("data", i, StandardCharsets.US_ASCII);
			List<MotCle> mcList = buildKeyWordsList(text);
			for (MotCle stem : mcList) {
				List<Integer> ids = mcMap.get(stem.getRacine());
				if (ids != null) {
					ids.add(i);
				} else {
					ids = new ArrayList<>();
					ids.add(i);
					mcMap.put(stem.getRacine(), ids);
				}
			}
			if(i % 100 == 0) System.out.println("current book : " + i);
		}
		
		System.out.println("end, saving the file");
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("keywords.ser"));
		oos.writeObject(mcm);
		oos.flush();
		oos.close();
	}
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// buildKeywordsFile();
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("keywords.ser"));
		MotCleMap mcm = (MotCleMap) ois.readObject();
		ois.close();
		
		/* HashMap<String, List<Integer>> map = mcm.getMotCleMap();
		map.entrySet().removeIf(e -> map.get(e.getKey()).size() > 30); */
		
		System.out.println(mcm.toString());
		
	}
}
