package de.tudarmstadt.lt.masterThesis.classification;

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
import org.elasticsearch.index.mapper.ParseContext.Document;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import static org.elasticsearch.common.xcontent.XContentFactory.*;
import static org.elasticsearch.index.query.QueryBuilders.*;


public class ElasticSearch {

	public Map<String,Set<String>> loadClusters(String inputFile){

		// on startup

		Node node = nodeBuilder().node();
		Client client = node.client();



		InputStream is = getClass().getResourceAsStream(inputFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		Map<String,Set<String>> map = new HashMap<String,Set<String>>();
		try{
			int number = 0;

			String line;
			while((line= br.readLine())!=null){ // word	cid	cluster	isas

				IndexResponse indexResponse = client.prepareIndex("clusters", "string", Integer.toString(number))
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

			GetResponse getResponse = client.prepareGet("clusters", "string", "10")
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

	public SearchHits search(String stringQuery) {
		Node node = nodeBuilder().node();
		Client client = node.client();

		MoreLikeThisQueryBuilder qb = moreLikeThisQuery("content")  
				.likeText(stringQuery)                             
				.minTermFreq(1)                                             
				.maxQueryTerms(12);
		QueryBuilder qb2 = termsQuery("content",    
				stringQuery.split(" "))                 
				.minimumMatch(1); 

		SearchResponse response = client.prepareSearch("clusters")
				.setTypes("string")
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(qb2)             // Query
				.setFrom(0).setSize(3).setExplain(true)
				.execute()
				.actionGet();
		/*System.out.println(response.getHits().getHits().length + " results for \"" + stringQuery + "\"" );
		for (SearchHit hit : response.getHits().getHits()) {
			System.out.println("score:" + hit.getScore() + " id:" + hit.getId());
			//System.out.println(hit.getSourceAsString());
		}*/
		node.close();
		return response.getHits();
	}
}
