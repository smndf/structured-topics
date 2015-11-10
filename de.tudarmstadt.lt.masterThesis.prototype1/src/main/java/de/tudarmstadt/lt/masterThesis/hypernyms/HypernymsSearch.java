package de.tudarmstadt.lt.masterThesis.hypernyms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Pointer;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;

public class HypernymsSearch {

	public void wordNetSearch(String inputFile){

		String outputFile = inputFile.substring(1, inputFile.length()-4) + "WithHyp.csv";

		Set<String> cluster = new HashSet<String>();

		InputStream is2 = getClass().getResourceAsStream(inputFile);
		BufferedReader clusters = new BufferedReader(new InputStreamReader(is2));
		String line = null;
		try {
			File fo = new File(outputFile);
			FileWriter fw = new FileWriter (fo);

			int noLine = 0;
			while ( (line = clusters.readLine()) != null){ 
				if (line.split("\t").length>2){
					cluster.clear();
					System.out.println("nouvelle ligne" + noLine++);
					int nbTriangles = Integer.valueOf(line.split("\t")[1]);
					String words =  line.split("\t")[2];
					for (int i=0;i<words.split(",").length;i++){
						cluster.add(words.split(",")[i]);
					}
					if (cluster.size()>2){					
						Set<Hypernym> frequentHypernyms = frequentHypernyms(cluster);
						//System.out.println("Best hypernyms : " + frequentHypernyms.toString());
						writeFile(outputFile,fw,noLine,frequentHypernyms,cluster,cluster.size(),nbTriangles
								);
					} else {
						System.out.println("cluster removed due to low size.");
					}
				}
			}
			fw.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		


	}

	private void writeFile(String outputFile, FileWriter fw , int noLine, Set<Hypernym> frequentHypernyms,
			Set<String> cluster, int clusterSize, int nbTriangles) throws IOException {

		String line = noLine + "\t" + clusterSize + "\t" + nbTriangles +"\t" + toString(frequentHypernyms,cluster) + "\t" + toString(cluster) +"\n";
		fw.write (line);

	}

	private String toString(Set<String> set) {
		StringWriter res = new StringWriter();
		for (String s : set){
			if (s.split("#").length>1) s = s.split("#")[0];
			res.write(s + ", ");
		}
		if (res.toString().length()>1) res.getBuffer().setLength( res.toString().length()-2);
		return res.toString();
	}

	private String toString(Set<Hypernym> frequentHypernyms, Set<String> cluster) {
		StringWriter res = new StringWriter();
		float f;
		for (Hypernym h : frequentHypernyms){
			if (h.word.split("#").length>1) h.word = h.word.split("#")[0];
			res.write(h.word + ":" + h.nb + "/" + cluster.size()  +/* "=" + (f=h.nb/cluster.size()) +*/ ", ");
		}
		if (res.toString().length()>1) res.getBuffer().setLength( res.toString().length()-2);
		return res.toString();
	}

	private Set<Hypernym> frequentHypernyms(Set<String> set) {
		Map<String,Integer> hMap = new HashMap<String,Integer>();
		TreeMap<Integer,Set<String>> hypRankMap = new TreeMap<Integer,Set<String>>();

		String inputIsasFile = "/wikipedia.patterns_lemmatized";

		InputStream is = getClass().getResourceAsStream(inputIsasFile);
		BufferedReader isasText = new BufferedReader(new InputStreamReader(is));
		String line = null;
		try {

			for (String word : set){

				word = word.split("#")[0].toLowerCase();
				if (word.length() > 0){
					// ADD EACH WORD IN THE SET
					//addToMaps(hMap,hypRankMap,word);

					String letter = word.substring(0,1);
					while ( (line = isasText.readLine()) != null){ 
						if (line.toLowerCase().startsWith(letter) && line.split(" ")[0].toLowerCase().equals(word.toLowerCase())){
							addToMaps(hMap,hypRankMap,line.split(" ")[2].split("\t")[0]);
						}
					}
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		


		try {
			try {
				JWNL.initialize(new FileInputStream("src/main/resources/properties.xml"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (JWNLException e) {
				e.printStackTrace();
			}

			Dictionary dictionary = Dictionary.getInstance();
			IndexWord indexWord = null;
			//int i = 0;
			for (String word : set){
				//System.out.println(i++);
				//if (i==18) System.out.println(word);
				//if (i==19) System.out.println(word);
				POS pos = POS.NOUN;
				if (word.split("#").length>1){					
					pos = POS.NOUN;
					switch (word.split("#")[1]) {
					case "NP" : pos = POS.NOUN;
					break;
					case "VP" : pos = POS.VERB;
					break;
					case "JJ" : pos = POS.ADJECTIVE;
					break;
					case "RB" : pos = POS.ADVERB;
					break;
					}

					word = word.split("#")[0].toLowerCase();

					//addToMaps(hMap,hypRankMap,word);
					//System.out.println("word " + pos);
					indexWord = dictionary.lookupIndexWord(pos, word.toLowerCase());
					if (indexWord != null) {
						Synset[] synset = indexWord
								.getSenses();
						if (synset != null) {
							for (Synset s : synset) {
								Pointer[] pointerArr = s.getPointers(PointerType.HYPERNYM);
								if (pointerArr != null){
									for (Pointer x : pointerArr) {
										for (Word hypernym : x.getTargetSynset().getWords()) {
											addToMaps(hMap,hypRankMap,hypernym.getLemma());
											//System.out.println("dfbv");
										}
									}
								}
							}
						}
					}
				}
			}
			dictionary.close();
		} catch (JWNLException e) {
			e.printStackTrace();
		}

		//System.out.println(hypRankMap.size());
		Set<Hypernym> res = findBestHypernyms(hypRankMap,set.size());

		if (res.isEmpty()){
			res.add(new Hypernym("NO HYPERNYMS",0));
		}
		
		return res;
	}

	private Set<Hypernym> findBestHypernyms(TreeMap<Integer, Set<String>> m, int clusterSize) {

		Set<Hypernym> set = new HashSet<Hypernym>();
		while (set.size()<3 && !m.isEmpty()){
			for (String s : m.get(m.lastKey())){
				set.add(new Hypernym(s,m.lastKey()));
			}
			m.remove(m.lastKey());
		}
		// keep only 3 hypernyms and remove the longest ones first
		int length = 15;
		Set<Hypernym> setcp = new HashSet<Hypernym>();
		setcp.addAll(set);
		while (set.size()>3){			
			for (Hypernym h : setcp){
				if (set.size()>3 && h.word.length() > length){
					set.remove(h);
				}
			}
			length--;
		}
		return set;
	}

	private void addToMaps(Map<String, Integer> hMap,
			Map<Integer, Set<String>> hypRankMap, String word) {

		if (hMap.containsKey(word)){
			hypRankMap.get(hMap.get(word)).remove(word);
			if (!hypRankMap.containsKey(hMap.get(word)+1)){
				hypRankMap.put(hMap.get(word)+1,new HashSet<String>());
			}
			hypRankMap.get(hMap.get(word)+1).add(word);						
			hMap.put(word,hMap.get(word)+1);
		} else {
			hMap.put(word,1);
			if (!hypRankMap.containsKey(1)){
				hypRankMap.put(1,new HashSet<String>());
			}
			hypRankMap.get(1).add(word);
		}

	}
	
	private class Hypernym{
		
		private String word;
		private int nb;

		public Hypernym(String word, int nb) {
			super();
			this.word = word;
			this.nb = nb;
		}
		
		public int getNb() {
			return nb;
		}
		public void setNb(int nb) {
			this.nb = nb;
		}
		public String getWord() {
			return word;
		}
		public void setWord(String word) {
			this.word = word;
		}
	}
}
