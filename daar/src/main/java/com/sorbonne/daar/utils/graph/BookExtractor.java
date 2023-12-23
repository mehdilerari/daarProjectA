package com.sorbonne.daar.utils.graph;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sorbonne.daar.Book;

public class BookExtractor {

	public static void main(String[] args) throws IOException {
		
		HashMap<Integer, Book> books = new HashMap<>();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		final String gutendexUrl = "https://gutendex.com/";
		RestTemplate rt = new RestTemplate();
		int count=0;
		
		for (int i = 1; i < 1665; i++) {
			
			String title;
			List<String> authors; 
			String content ="";
			String image ="";
			
			try {
			// First we get the json content for the book with the id i
			String b = rt.getForObject(gutendexUrl + "books/" + i, String.class);
			JsonObject jo = gson.fromJson(b, JsonObject.class);
				
			// We get the title of the book with the id i
			title = GsonManager.getBookTitle(jo);
			authors = GsonManager.getBookAuthors(jo);
			content = GsonManager.getBookHtmlContentURL(jo);
			image = GsonManager.getBookImageURL(jo);
			if(image=="NULL") {
				System.out.println("************" + i);
				count++;
			}
			Book book = new Book(i-1, title, authors, content, image);
			books.put(i-1, book);
			
			} catch (HttpClientErrorException e) {
				System.err.println("not found");
			}
			
		}
		
		System.out.println(count);
		
		System.out.println("saving books.ser file");
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("books.ser"));
		oos.writeObject(books);
		oos.flush();
		oos.close();
		
	}
	
}
