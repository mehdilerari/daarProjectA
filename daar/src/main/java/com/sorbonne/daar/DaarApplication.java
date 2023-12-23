package com.sorbonne.daar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.sorbonne.daar.controller.DAARController;
import com.sorbonne.daar.utils.graph.JaccardMatrice;
import com.sorbonne.daar.utils.keywords.MotCleMap;

@SpringBootApplication
public class DaarApplication {
	
	public static HashMap<String, List<Integer>> keywords;
	public static HashMap<String, Integer> titles;
	public static HashMap<String, List<Integer>> authors;
	public static HashMap<Integer, Float> closeness;
	public static HashMap<Integer, Book> books;
	
	private static final Logger LOGGER  = LoggerFactory.getLogger(DaarApplication.class);

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("keywords.ser"));
		MotCleMap mcm = (MotCleMap) ois.readObject();
		ois.close();
		keywords = mcm.getMotCleMap();
		LOGGER.info("Keywords loaded");
		
		ois = new ObjectInputStream(new FileInputStream("titles.ser"));
		titles = (HashMap<String, Integer>) ois.readObject();
		ois.close();
		LOGGER.info("Titles loaded");
		
		ois = new ObjectInputStream(new FileInputStream("authors.ser"));
		authors = (HashMap<String, List<Integer>>) ois.readObject();
		ois.close();
		LOGGER.info("Authors loaded");
		
		ois = new ObjectInputStream(new FileInputStream("closeness.ser"));
		closeness = (HashMap<Integer, Float>) ois.readObject();
		ois.close();
		LOGGER.info("Closeness graph loaded");
		
		ois = new ObjectInputStream(new FileInputStream("books.ser"));
		books = (HashMap<Integer, Book>) ois.readObject();
		ois.close();
		LOGGER.info("Books data loaded");
		
		SpringApplication.run(DaarApplication.class, args);
	}

}
