package de.tudarmstadt.lt.masterThesis.hypernyms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

public class IsasSearch {

	public void addISAS(String inputFile, String isasFileStem) throws IOException{
		String outputFile = inputFile.substring(0, inputFile.length()-4) + "IsasPatternsim.csv";
		outputFile = outputFile.split("/")[outputFile.split("/").length-1];
		System.out.println("output = "+outputFile);
		BufferedReader clusters = getBufferedReaderForFile(inputFile,true);
		Set<String> cluster = new HashSet<String>();
		String line;
		int clusterNo = 0;
		int i = 0;
		File fo = new File(outputFile);
		FileWriter fw = new FileWriter (fo);
		while ( (line = clusters.readLine()) != null){
			System.out.println("new line " + i++);
			cluster.clear();
			clusterNo = Integer.valueOf(line.split("\t")[0]);
			for (String word : line.split("\t")[line.split("\t").length-1].split(", ")){
				cluster.add(word);
			}
			TwoMaps<String> twoMaps = searchISAS(cluster, isasFileStem);
			fw.write(clusterNo + "\t" + toString(twoMaps.getMap1()) + "\t" + toString(twoMaps.getMap2()) + "\n");
			fw.flush();
		}
		fw.close();
	}

	private String toString(Map<String,Integer> hypernyms) {
		StringWriter sw = new StringWriter();
		for (String s : hypernyms.keySet()){
			sw.append(s+"("+hypernyms.get(s)+")"+", ");
		}
		return sw.toString().substring(0, sw.getBuffer().length()-2);
	}


	public TwoMaps searchISAS(Set<String> cluster, String isasFileStem){

		Map<String,Integer> hMap1 = new HashMap<String,Integer>();
		TreeMap<Integer,Set<String>> hypRankMap1 = new TreeMap<Integer,Set<String>>();
		Map<String,Integer> hMap2 = new HashMap<String,Integer>();
		TreeMap<Integer,Set<String>> hypRankMap2 = new TreeMap<Integer,Set<String>>();

		String line = null;
		Map<String,Integer> map1 = null;
		Map<String,Integer> map2 = null;
		try {

			Set<String> isas = new HashSet<String>();
			String isasFile;
			for (String word : cluster){
				if (word.length()>0){
					isasFile = isasFileStem + "-" + word.toLowerCase().charAt(0) + ".csv";
					try{
						BufferedReader isasText = getBufferedReaderForFile(isasFile,true);
						//word = word.split("#")[0].toLowerCase();
						//System.out.println("\n"+word.toString());

						String letter = word.substring(0, 1);
						while ( (line = isasText.readLine()) != null){ 
							//System.out.println(line.split("\t")[1].toString());
							if (line.split("\t")[0].toLowerCase().equals(word)){
								String hyp = line.split("\t")[1];
								isas.add(hyp);
								addToMaps(hyp, hMap1, hypRankMap1, 1);
								addToMaps(hyp, hMap2, hypRankMap2, Integer.valueOf(line.split("\t")[2]));
								//System.out.println(line.split("\t")[1].toString());
							}
						}
					}catch (FileNotFoundException e) {
						System.out.println("no file "+isasFile);
					}
				}
			}
			map1 = findBestHypernyms(hypRankMap1,isas.size());
			map2 = findBestHypernyms(hypRankMap2,isas.size());			
			//System.out.println(isas.toString());
			if (map1.isEmpty()){
				map1.put("NO HYPERNYMS",0);
			}
			if (map2.isEmpty()){
				map2.put("NO HYPERNYMS",0);
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		

		return new TwoMaps<String>(map1,map2) ;
	}

	public void addToMaps(String hyp, Map<String, Integer> hMap,
			Map<Integer, Set<String>> hypRankMap, Integer weight) {

		int oldWeight = 0,newWeight = 0;
		if (hMap.containsKey(hyp)){
			oldWeight = hMap.get(hyp);
			newWeight = oldWeight + weight;
			//System.out.println("hyp("+hyp.getWord()+",d="+hyp.depth+")");
			//System.out.println("remove "+hyp.getWord()+" from hypRankMap "+hMap.get(hyp));
			hypRankMap.get(oldWeight).remove(hyp);
			if (!hypRankMap.containsKey(newWeight)){
				hypRankMap.put(newWeight,new HashSet<String>());
			}
			//System.out.println("add "+hyp.getWord()+" to hypRankMap "+hMap.get(hyp)+1);
			hypRankMap.get(newWeight).add(hyp);						
			//System.out.println("update hMap for "+hyp.getWord()+" : "+hMap.get(hyp)+" > "+hMap.get(hyp)+1);
			hMap.put(hyp,newWeight);
		} else {
			hMap.put(hyp,weight);
			//System.out.println("add "+hyp.getWord()+",1 to hMap ");
			if (!hypRankMap.containsKey(weight)){
				hypRankMap.put(weight,new HashSet<String>());
			}
			hypRankMap.get(weight).add(hyp);
			//System.out.println("add 1,"+hyp.getWord()+" to hypRankMap ");
		}


	}
	
	private Map<String,Integer> findBestHypernyms(TreeMap<Integer,Set<String>> hypRankMap, int clusterSize) {

		final int SIZE = 10;
		Map<String,Integer> map = new HashMap<String,Integer>();
		int lastIndex = 0;
		while (map.size()<SIZE && !hypRankMap.isEmpty()){
			for (String s : hypRankMap.get(hypRankMap.lastKey())){
				map.put(s,hypRankMap.lastKey());
				//System.out.println("s.getWord() = "+s.getWord());
				//System.out.println("hypRankMap.lastKey() = "+hypRankMap.lastKey());
			}
			lastIndex = hypRankMap.lastKey();
			hypRankMap.remove(hypRankMap.lastKey());
		}
		// keep only 3 hypernyms and remove the longest ones first
		int length = 15;
		Map<String,Integer> mapcp = new HashMap<String,Integer>();

		for (String h : map.keySet()){
			mapcp.put(h, map.get(h));
		}
		Set<String> set = new HashSet<String>();
		while (map.size()>SIZE){			
			for (String h : mapcp.keySet()){
				//System.out.println("h = "+h.getWord());
				if (map.size()>SIZE && map.get(h)==lastIndex && h.length() > length){
					map.remove(h);
					set.add(h);
				}
			}
			length--;
			for(String h : set){
				mapcp.remove(h);
			}
			set.clear();
		}
		return map;
	}

	private BufferedReader getBufferedReaderForFile(String file, Boolean externFiles) throws FileNotFoundException {

		BufferedReader br;
		if (externFiles){
			br= new BufferedReader(new FileReader(file));			
		} else {			
			InputStream is2 = getClass().getResourceAsStream("/"+file);
			br = new BufferedReader(new InputStreamReader(is2));
		}
		return br;
	}

	public class TwoMaps<T>{
		private Map<T,Integer> map1;
		private Map<T,Integer> map2;
		public TwoMaps(Map<T, Integer> map1, Map<T, Integer> map2) {
			super();
			this.map1 = map1;
			this.map2 = map2;
		}
		public Map<T, Integer> getMap1() {
			return map1;
		}
		public void setMap1(Map<T, Integer> map1) {
			this.map1 = map1;
		}
		public Map<T, Integer> getMap2() {
			return map2;
		}
		public void setMap2(Map<T, Integer> map2) {
			this.map2 = map2;
		}
	}

}
