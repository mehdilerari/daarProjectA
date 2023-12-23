package com.sorbonne.daar.utils.graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GsonManager {

	/** Get the plain text url of jo
	 * @throws IOException */
	public static String getBookContentURL(JsonObject jo) throws IOException {
		JsonElement plainText = new JsonObject();
		JsonElement el = jo.getAsJsonObject("formats").get("text/plain");
		if (el != null) {
			plainText = el;
		} else if ((el = jo.getAsJsonObject("formats").get("text/plain; charset=us-ascii")) != null) {
			plainText = el;
		} else if ((el = jo.getAsJsonObject("formats").get("text/plain; charset=utf-8")) != null) {
			plainText = el;
		}
		
		return plainText.getAsString();
	}
	
	/** Get the html text url of jo
	 * @throws IOException */
	public static String getBookHtmlContentURL(JsonObject jo) throws IOException {
		JsonElement contentURL= new JsonObject();
		JsonElement el = jo.getAsJsonObject("formats").get("text/html");
		if (el != null) {
			contentURL= el;
		} else if ((el = jo.getAsJsonObject("formats").get("text/html; charset=us-ascii")) != null) {
			contentURL= el;
		} else if ((el = jo.getAsJsonObject("formats").get("text/html; charset=utf-8")) != null) {
			contentURL= el;
		}else if ((el = jo.getAsJsonObject("formats").get("text/html; charset=iso-8859-1")) != null) {
			contentURL= el;
		} else if ((el = jo.getAsJsonObject("formats").get("text/plain")) != null) {
			contentURL= el;
		} else if ((el = jo.getAsJsonObject("formats").get("text/plain; charset=us-ascii")) != null) {
			contentURL= el;
		} else if ((el = jo.getAsJsonObject("formats").get("text/plain; charset=utf-8")) != null) {
			contentURL= el;
		}else {	
			return "NULL";
		}
		//book/632 (has nothing ..)
		return contentURL.getAsString();
	}
	
	/** Get the book title
	 * @throws IOException */
	public static String getBookTitle(JsonObject jo) throws IOException {
		JsonElement el = jo.getAsJsonPrimitive("title");
		return el.getAsString();
	}
	
	/** Get the book authors
	 * @throws IOException */
	public static ArrayList<String> getBookAuthors(JsonObject jo) {
		ArrayList<String> bookAuthors = new ArrayList<String>();
		JsonArray ar = jo.getAsJsonArray("authors");
			ar.forEach(a -> {
				bookAuthors.add(a.getAsJsonObject().get("name").getAsString());
			} );
		return bookAuthors;
	}
	
	/** Get the book title
	 * @throws IOException */
	public static String getBookImageURL(JsonObject jo) throws IOException {
		JsonElement imageURL= new JsonObject();
		JsonElement el = jo.getAsJsonObject("formats").get("image/jpeg");
		if (el != null) {
			imageURL= el;
		} else {	
			return "NULL"; // no book cover (18 books)
		}
		//book/632 (has nothing ..)
		return imageURL.getAsString();
	}
	
	/** returns the content of a book based on his url */
	public static String getBookContent(String url) {
		RestTemplate rt = new RestTemplate();
		return rt.getForObject(url, String.class);
	}

	/** Returns a list with all the plain text urls from a list of book */
	public static JsonArray getURLList(JsonObject jo) {
		JsonArray ja = jo.getAsJsonArray("results");
		JsonArray urlList = new JsonArray();
		ja.forEach(a -> {
			JsonElement el = a.getAsJsonObject().getAsJsonObject("formats").get("text/plain");
			if (el != null) {
				urlList.add(el);
			} else if ((el = a.getAsJsonObject().getAsJsonObject("formats").get("text/plain; charset=utf-8")) != null) {
				urlList.add(el);
			} else if ((el = a.getAsJsonObject().getAsJsonObject("formats")
					.get("text/plain; charset=us-ascii")) != null) {
				urlList.add(el);
			}
		});

		return urlList;
	}

	

}
