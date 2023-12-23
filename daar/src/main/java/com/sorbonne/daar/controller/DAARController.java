package com.sorbonne.daar.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.tomcat.util.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sorbonne.daar.Book;
import com.sorbonne.daar.DaarApplication;
import com.sorbonne.daar.services.BookService;
import com.sorbonne.daar.utils.graph.GsonManager;
import com.sorbonne.daar.utils.graph.Jaccard;
import com.sorbonne.daar.utils.keywords.MotsClesExtractor;

@CrossOrigin
@RestController
public class DAARController {
	
	private final String gutendexUrl = "https://gutendex.com/";
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	private static final Logger LOGGER  = LoggerFactory.getLogger(DAARController.class);
	
	@Autowired
    private BookService bookService = new BookService();
	
	
	/**
	 * Get a book from keyword
	 * @throws IOException 
	 */
	@GetMapping("/basicsearch/{content}")
	@ResponseBody
	public ResponseEntity<List<Book>> basicSearch(@PathVariable String content) throws ParseException, IOException {
		LOGGER.info("Searching for basic : " + content);
		
		String[] keywords = content.split("\\s+");
		Set<Integer> ids = new LinkedHashSet<>();
		Arrays.asList(keywords).forEach(k -> {
			try {
				// We get the stem of the keyword to compare it 
				// with the words of the keyword map
				k = MotsClesExtractor.stem(k);
			} catch (IOException e) {
				LOGGER.error("Error while getting the stem of the keyword");
			}
			ids.addAll(bookService.getRelatedBooksKeywords(k.toLowerCase()));
		});
		List<Integer> idsAsList = new ArrayList<>(ids);
		bookService.orderResults(idsAsList);
		return ResponseEntity.ok(bookService.getBookDataFromIdsList(idsAsList));
	}
	
	/**
	 * Get a book from keyword
	 * @throws IOException 
	 */
	@GetMapping("/advancedsearch/{regex}")
	@ResponseBody
	public ResponseEntity<List<Book>> advancedSearch(@PathVariable String regex) throws ParseException, IOException {
		LOGGER.info("Searching for advanced : " + regex);
		//TODO
		String[] keywords = regex.split("\\s+");
		Set<Integer> ids = new LinkedHashSet<>();
		Arrays.asList(keywords).forEach(k -> {
			try {
				ids.addAll(bookService.advancedresearch(k.toLowerCase()));
			} catch (Exception e) {
				LOGGER.error("Error during advanced research");
			}
		});
		List<Integer> idsAsList = new ArrayList<>(ids);
		bookService.orderResults(idsAsList);
		return ResponseEntity.ok(bookService.getBookDataFromIdsList(idsAsList));
	}
	
	/**
	 * Get books from a title
	 * @throws IOException 
	 */
	@GetMapping("/titlesearch/{title}")
	@ResponseBody
	public ResponseEntity<List<Book>> titleSearch(@PathVariable String title) throws ParseException, IOException {
		LOGGER.info("Searching for title : " + title);
		
		String[] keywords = title.split("\\s+");
		Set<Integer> ids = new HashSet<>();
		Arrays.asList(keywords).forEach(k -> {
			ids.addAll(bookService.getRelatedBooksByTitle(k.toLowerCase()));
		});
		List<Integer> idsAsList = new ArrayList<>(ids);
		bookService.orderResults(idsAsList);
		return ResponseEntity.ok(bookService.getBookDataFromIdsList(idsAsList));
	}
	
	/**
	 * Get books from an author
	 * @throws IOException 
	 */
	@GetMapping("/authorsearch/{author}")
	@ResponseBody
	public ResponseEntity<List<Book>> authorSearch(@PathVariable String author) throws ParseException, IOException {
		LOGGER.info("Searching for author : " + author);
		
		String[] keywords = author.split("\\s+");
		Set<Integer> ids = new HashSet<>();
		Arrays.asList(keywords).forEach(k -> {
			ids.addAll(bookService.getRelatedBooksByAuthor(k.toLowerCase()));
		});
		List<Integer> idsAsList = new ArrayList<>(ids);
		bookService.orderResults(idsAsList);
		return ResponseEntity.ok(bookService.getBookDataFromIdsList(idsAsList));
	}
	
	
	
	
	////////////// TME8 BELOW, NOT USED //////////////////////////////////
	/**
	 * Get all the books
	 */
	@GetMapping("/books")
	@ResponseBody
	public ResponseEntity<String> getAllBooks() throws JsonMappingException, JsonProcessingException, ParseException {
		
		RestTemplate rt = new RestTemplate();
		String res = rt.getForObject(gutendexUrl + "books/", String.class);
		
		JsonObject jo = gson.fromJson(res, JsonObject.class);
		//JsonElement je = jo.get("results");
		
		return ResponseEntity.ok(jo.toString());
		
	}
	
	
	/**
	 * Get a book from its id
	 */
	@GetMapping("/book/{id}")
	@ResponseBody
	public ResponseEntity<String> getBook(@PathVariable Long id) throws JsonMappingException, JsonProcessingException, ParseException {
		
		RestTemplate rt = new RestTemplate();
		String res = rt.getForObject(gutendexUrl + "books/" + id, String.class);
		
		JsonObject jo = gson.fromJson(res, JsonObject.class);
		
		return ResponseEntity.ok(jo.toString());
	}
	
	
	/**
	 * Get all the books written in french
	 */
	@GetMapping("/frenchbooks")
	@ResponseBody
	public ResponseEntity<String> getAllFrenchBooks() throws JsonMappingException, JsonProcessingException, ParseException {
		
		RestTemplate rt = new RestTemplate();
		String res = rt.getForObject(gutendexUrl + "books?languages=fr", String.class);
		
		JsonObject jo = gson.fromJson(res, JsonObject.class);
		//JsonElement je = jo.get("results");
		
		return ResponseEntity.ok(jo.toString());
		
	}
	
	
	/**
	 * Get a book in french from its id
	 */
	@GetMapping("/frenchbook/{id}")
	@ResponseBody
	public ResponseEntity<String> getFrenchBook(@PathVariable Long id) throws JsonMappingException, JsonProcessingException, ParseException {
		
		RestTemplate rt = new RestTemplate();
		String res = rt.getForObject(gutendexUrl + "books?languages=fr&ids=" + id, String.class);
		
		JsonObject jo = gson.fromJson(res, JsonObject.class);
		
		return ResponseEntity.ok(jo.toString());
		
	}
	
	/**
	 * Get all english books
	 */
	@GetMapping("/englishbooks")
	@ResponseBody
	public ResponseEntity<String> getAllEnglishBooks() throws JsonMappingException, JsonProcessingException, ParseException {
		
		RestTemplate rt = new RestTemplate();
		String res = rt.getForObject(gutendexUrl + "books?languages=en", String.class);
		
		JsonObject jo = gson.fromJson(res, JsonObject.class);
		//JsonElement je = jo.get("results");
		
		return ResponseEntity.ok(jo.toString());
		
	}
	
	/**
	 * Get a book in english from its id
	 */
	@GetMapping("/englishbook/{id}")
	@ResponseBody
	public ResponseEntity<String> getEnglishBook(@PathVariable Long id) throws JsonMappingException, JsonProcessingException, ParseException {
		
		RestTemplate rt = new RestTemplate();
		String res = rt.getForObject(gutendexUrl + "books?languages=en&ids=" + id, String.class);
		
		JsonObject jo = gson.fromJson(res, JsonObject.class);
		
		return ResponseEntity.ok(jo.toString());
		
	}
	
}
