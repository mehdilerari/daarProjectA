package com.sorbonne.daar.utils.graph;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class TitleExtractor {

	public static void main(String[] args) throws IOException {
		
		HashMap<String, Integer> titles = new HashMap<>();
		HashMap<String, ArrayList<Integer>> authors = new HashMap<>();
				
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		final String gutendexUrl = "https://gutendex.com/";
		RestTemplate rt = new RestTemplate();
		
		for (int i = 1; i < 1665; i++) {
			String title = "";
			
			try {
			// First we get the json content for the book with the id i
			String book = rt.getForObject(gutendexUrl + "books/" + i, String.class);
			JsonObject jo = gson.fromJson(book, JsonObject.class);
				
			// We get the title of the book with the id i
			title = GsonManager.getBookTitle(jo);
			titles.put(title, i-1);
			System.out.println("**" + titles.get(title).toString());
			
			ArrayList<String> author = GsonManager.getBookAuthors(jo);
			for(String a : author) {
				ArrayList<Integer> l = authors.get(a);
				if (l == null) {
					ArrayList<Integer> x = new ArrayList<Integer>();
					x.add(i-1);
					authors.put(a, x);
				}else {
					l.add(i-1);
				}
				System.out.println("--" + authors.get(a).toString());
			}
			
			} catch (HttpClientErrorException e) {
				System.err.println("not found");
			}
			
		}
		
		System.out.println("saving titles.ser file");
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("titles.ser"));
		oos.writeObject(titles);
		oos.flush();
		oos.close();
		
		System.out.println("saving authors.ser file");
		oos = new ObjectOutputStream(new FileOutputStream("authors.ser"));
		oos.writeObject(authors);
		oos.flush();
		oos.close();

	}
	
}
