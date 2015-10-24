package de.tudarmstadt.lt.masterThesis.classification;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DBPediaCrawler {

	public String getRandomAbstract(){
		String url = "http://en.wikipedia.org/wiki/Special:Random";
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String uri = doc.baseUri();
		//System.out.println(uri);
		String title = uri.split("/")[uri.split("/").length-1];
		System.out.print(title + "\t");
		url = "http://dbpedia.org/resource/" + title;
		//System.out.println(url);
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(doc);
		String selector = "[property=dbo:abstract]";
		String docAbstract = doc.select(selector).text();
		return docAbstract;
	}


	
}
