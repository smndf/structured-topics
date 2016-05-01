package classification;

import static org.elasticsearch.node.NodeBuilder.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.mapper.ParseContext.Document;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import static org.elasticsearch.common.xcontent.XContentFactory.*;
import static org.elasticsearch.index.query.QueryBuilders.*;


public class ElasticSearch {

	public Map<String,Set<String>> loadClusters(String inputFile, String indexName){

		// on startup

		Node node = nodeBuilder().node();
		Client client = node.client();

		InputStream is = getClass().getResourceAsStream(inputFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		Map<String,Set<String>> map = new HashMap<String,Set<String>>();
		try{
			int number = 0;

			String line;
			XContentBuilder englishAnalyzer = XContentFactory.jsonBuilder()
					.startObject()
					.startObject("analysis")
					.startObject("filter")
					.startObject("english_stop")
					.field("type", "stop")
					.field("stopwords", "_english_") 
					.endObject()
					/*		.startObject("english_keywords")
									.field("type", "keyword_marker")
									.array("keywords")
								.endObject()
								.startObject("english_stemmer")
									.field("type", "stemmer")
									.field("language", "english")
								.endObject()
								.startObject("english_possessive_stemmer")
									.field("type", "stemmer")
									.field("language", "possessive_english")
								.endObject()
					 */
					.endObject()
					.startObject("analyzer")
					.startObject("customAnalyzer")
					//.field("type","custom")
					.field("tokenizer","standard")
					.array("filter","standard","lowercase","english_stop"/*,"english_keywords","english_stemmer"*/)
					.endObject()
					.endObject()
					.endObject()
					.endObject();

			/*
			XContentBuilder englishAnalyzer = XContentFactory.jsonBuilder()
				.startObject()
				.startObject("analysis")
				.startObject("filter")
				.startObject("english_stop")
				.field("type", "stop")
				.field("stopwords", "_english_") 
				.endObject()
				.startObject("english_stemmer")
				.field("type", "stemmer")
				.field("language", "english")
				.endObject()
				.startObject("english_possessive_stemmer")
				.field("type", "stemmer")
				.field("language", "possessive_english")
				.endObject()
				.endObject()
				.startObject("analyzer")
				.startObject("customAnalyzer")
				.field("tokenizer","standard")
				.array("filter","english_possessive_stemmer","lowercase","english_stemmer")
				.endObject()
				.endObject()
				.endObject()
				.endObject();
			 */

			client.admin().indices().prepareDelete(indexName).get();
			client.admin().indices().prepareCreate(indexName).setSettings(englishAnalyzer).get();

			while((line= br.readLine())!=null){ // word	cid	cluster	isas

				IndexResponse indexResponse = client.prepareIndex(indexName, "topic", Integer.toString(number))
						.setSource(jsonBuilder()
								.startObject()
								.field("content", line.split("\t")[line.split("\t").length-1])
								.endObject()
								)
								.execute()
								.actionGet();
				Set<String> set = new HashSet<String>();
				for (int i = 0; i<line.split("\t")[1].split(", ").length;i++){					
					set.add(line.split("\t")[1].split(", ")[i]);
				}
				map.put(indexResponse.getId(),set);




				/*
			Document doc;
			DeleteResponse deleteResponse = client.prepareDelete("clusters", "string", Integer.toString(number))
			        .execute()
			        .actionGet();
				 */

				indexResponse.toString();
				// Index name
				String _index = indexResponse.getIndex();
				// Type name
				String _type = indexResponse.getType();
				// Document ID (generated or not)
				String _id = indexResponse.getId();
				// Version (if it's the first time you index this document, you will get: 1)
				long _version = indexResponse.getVersion();
				// isCreated() is true if the document is a new one, false if it has been updated
				boolean created = indexResponse.isCreated();

				System.out.println("index " + _index + " type " + _type + " id " + _id + " created.");
				number++;
			}

			GetResponse getResponse = client.prepareGet(indexName, "topic", "10")
					.execute()
					.actionGet();

			System.out.println(getResponse.getSourceAsString());

			// on shutdown
			node.close();
		} catch (IOException exception){
			System.err.println (exception.getMessage());
		}
		return map;

	}

	public SearchHits search(String stringQuery, String indexName, Client client) {

		/*
		QueryBuilder qb = termsQuery("content",    
				stringQuery.split(" "))                 
				.minimumMatch(1); 

		SearchResponse response = client.prepareSearch("clusters")
				.setTypes("topic")
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(qb)             // Query
				.setFrom(0).setSize(7).setExplain(true)
				.execute()
				.actionGet();
		 */


		MatchQueryBuilder matchQuery = matchQuery("content", stringQuery).analyzer("customAnalyzer"); 

		SearchResponse response = client.prepareSearch(indexName)
				.setTypes("topic")
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(matchQuery)             // Query
				.setFrom(0).setSize(7).setExplain(true)
				.addHighlightedField("content",10,10000)
				.execute()
				.actionGet();

		/*System.out.println(response.getHits().getHits().length + " results for \"" + stringQuery + "\"" );
		for (SearchHit hit : response.getHits().getHits()) {
			System.out.println("score:" + hit.getScore() + " id:" + hit.getId());
			//System.out.println(hit.getSourceAsString());
		}*/
		return response.getHits();
	}
}
