package com.sorbonne.daar;

import java.io.Serializable;
import java.util.List;

public class Book implements Serializable{
	
	int id;
	String title;
	List<String> authors; 
	String content;
	String image;
	
	public Book() {	
	}
	
	public Book(int id, String title, List<String> authors, String content, String image) {
		super();
		this.id = id;
		this.title = title;
		this.authors = authors;
		this.content = content;
		this.image = image;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
}
