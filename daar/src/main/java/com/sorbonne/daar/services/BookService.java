package com.sorbonne.daar.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sorbonne.daar.Book;
import com.sorbonne.daar.DaarApplication;
import com.sorbonne.daar.controller.DAARController;
import com.sorbonne.daar.utils.keywords.MotsClesExtractor;
import com.sorbonne.daar.utils.recherche.ProjectEgrep;

@Service
public class BookService {
	
	/**
	 * Get all the books with a title containing a specific keyword
	 */
	public List<Integer> getRelatedBooksByTitle(String keyword){
		List<Integer> ids = new ArrayList<>();
		
		for (String title : DaarApplication.titles.keySet()) {
			if(title.toLowerCase().contains(keyword)) {
				ids.add(DaarApplication.titles.get(title));
			}
		}
		return ids;
	}
	
	/**
	 * Get all the books for a specific author
	 */
	public List<Integer> getRelatedBooksByAuthor(String keyword){
		List<Integer> ids = new ArrayList<>();
		
		for (String author : DaarApplication.authors.keySet()) {
			if(author.toLowerCase().contains(keyword)) {
				ids.addAll(DaarApplication.authors.get(author));
			}
		}
		return ids;
	}
	
	/**
	 * Get all the books containing a related keyword
	 */
	public Set<Integer> getRelatedBooksKeywords(String keyword){
		// We use a hashset to remove duplicates
		Set<Integer> ids = new HashSet<>();
		
		for (String kwFromDB : DaarApplication.keywords.keySet()) {
			if(kwFromDB.contains(keyword.toLowerCase())) {
				ids.addAll(DaarApplication.keywords.get(kwFromDB));
			}
		}
		return ids;
	}
	
	/**
	 * Order the ids of a result Set based on the closeness graph
	 */
	public void orderResults(List<Integer> ids) {
		List<Integer> orderedIndexes = new ArrayList<>(DaarApplication.closeness.keySet());
		Collections.sort(ids, Comparator.comparing(id -> orderedIndexes.indexOf(id)));
	}

	/**
	 * Get all the books using a specific keywords using Aho-Ullman or KMP
	 */
	public List<Integer> advancedresearch(String regex) throws Exception {
		return ProjectEgrep.advancedResearch(regex);
	}
	
	/**
	 * Get the data of the books in the ids list
	 */
	public List<Book> getBookDataFromIdsList(List<Integer> ids) {
		List<Book> books = new ArrayList<>();
		for (Integer i : ids) {
			books.add(DaarApplication.books.get(i));
		}
		return books;
	}
}
