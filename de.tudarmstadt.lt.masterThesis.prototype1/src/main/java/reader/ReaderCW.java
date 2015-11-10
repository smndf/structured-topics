package reader;

import graph.ArrayBackedGraph;
import graph.Graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import cw.CW;
import util.MapUtil;

public class ReaderCW {

	public void readTextFile(String fileName,Boolean externFile) throws IOException {

		//FileReader fr = new FileReader("testReader.txt");

		/*File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);

		int content;
		while ((content = fis.read()) != -1) {
			// convert to char and display it
			System.out.print((char) content);
		}

		fis.close();
*/
		BufferedReader br;
		if (externFile){			
			br = new BufferedReader(new FileReader(fileName));
		} else {
			InputStream is = getClass().getResourceAsStream(fileName);
			br = new BufferedReader(new InputStreamReader(is));			
		}
			
		String line = null;
		int N = 10000; //TODO dynamic size
		String[] lineSplit = new String[4];
		Map<Integer,String> nodesMapItoS = new HashMap<Integer,String>();
		Map<String,Integer> nodesMapStoI = new HashMap<String,Integer>();
		Graph<Integer, Float> g = new ArrayBackedGraph<Float>(N, 6);
		br.readLine();
		int i = 0;
		int root = 0; //root word of each cluster,line
		int linectr=0,linectr2 = 0;
		while ( (line = br.readLine()) != null){ 
			root = i;
			linectr++;
			if (linectr==10000){
				linectr2 +=linectr;
				System.out.println("line " + linectr2);
				linectr=0;
			}
			lineSplit = line.split("\t");
			//System.out.println(lineSplit[0]);
			String word = lineSplit[0];
			if (nodesMapStoI.containsKey(word)){
				root = nodesMapStoI.get(word);
			}else{
				//if (!freqMap.containsKey(lineSplit[0]) || (freqMap.containsKey(lineSplit[0]) && freqMap.get(lineSplit[0]) > freqThreshold)){						
				g.addNode(i);
				nodesMapItoS.put(i, word);
				nodesMapStoI.put(word, i);
				i++;
				//}else{
				//System.out.println("node " + lineSplit[0]+ "not added due to low freq");					
				//}
			}
			if (lineSplit.length>1){
				//System.out.println(lineSplit[2]);
				ArrayList<String> neighboursSplit = new ArrayList<String>(Arrays.asList(lineSplit[1].split(",")));
				for (String neighbourSp : neighboursSplit){
					//neighbours.add(neighbourSp.split(":")[0]);
					// if node already added
					if (neighbourSp.split(":").length>1){							
						if (nodesMapStoI.containsKey(neighbourSp.split(":")[0])){
							int node = nodesMapStoI.get(neighbourSp.split(":")[0]);
							g.addEdgeUndirected(root, node, Float.valueOf(neighbourSp.split(":")[1]));
						}else{
							g.addNode(i);
							nodesMapItoS.put(i, neighbourSp.split(":")[0]);
							nodesMapStoI.put(neighbourSp.split(":")[0] , i);
							g.addEdgeUndirected(root, i, Float.valueOf(neighbourSp.split(":")[1]));
							i++;
						}
					}
				}
			}
		}
		System.out.println("graph built");
		CW<Integer> cw = new CW<Integer>();
		Map<Integer, Set<Integer>> clusters = cw.findClusters(g);
		System.out.println(clusters.size() + " clusters.");

		//Map<Integer,Integer> nbTriangles = trianglesInClusters(g,clusters,nodesMapItoS);
		//Map<Integer,Integer> nbConnectedTriplets = connectedTripletsInClusters(g,clusters,nodesMapItoS);
		TrianglesAndTriplets trianglesTriplets = trianglesAndTriplets(g,clusters,nodesMapItoS);
		//Map<Integer,Integer> eigenvectorCentralityScores = eigenvectorCentrality(g, clusters);
		String outputFile = "CW" + fileName.split("/")[fileName.split("/").length-1].substring(0, fileName.length());
		File fo = new File(outputFile);
		try{
			FileWriter fw = new FileWriter (fo);
			//fw.write (MapUtil.toString(clusters,trianglesTriplets,nodesMapItoS,eigenvectorCentralityScores,"\t","\n"));
			fw.write (MapUtil.toString(clusters,trianglesTriplets,nodesMapItoS,"\t","\n"));
			fw.close();
			System.out.println("results written in " + outputFile);
		}
		catch (IOException exception)
		{
			System.out.println (exception.getMessage());
		}

		//System.out.println(MapUtil.toString(clusters,nodesMapItoS," : ","\n"));
	}

	private Map<Integer, Integer> eigenvectorCentrality(
			Graph<Integer, Float> g, Map<Integer, Set<Integer>> clusters) {

		Map<Integer,Integer> scores = new HashMap<Integer,Integer>();
		//for each cluster
		for (Entry<Integer, Set<Integer>> entry : clusters.entrySet()) {
			//int[][] m = buildAdjacencyMatrix(g,entry);
			Map<Integer,Integer> map1 = new HashMap<Integer,Integer>();
			Map<Integer,Integer> map2 = new HashMap<Integer,Integer>();
			int clusterSize = entry.getValue().size();
			int [][] m = new int [clusterSize][clusterSize];
			//System.out.println(clusterSize);
			for (int i = 0; i<clusterSize;i++){
				for (int j = 0; j<clusterSize;j++){
					m[i][j] = 0;
				}
			}
			int n = 0;
			for (Integer node1 : (Set<Integer>)entry.getValue()){
				int n1;
				if (map2.containsKey(node1)){
					n1 = map2.get(node1);
				} else{
					//System.out.println("node1 = "+node1);
					n1 = n;
					n++;
					map1.put(n1, node1);
					map2.put(node1, n1);					
				}
				Iterator<Integer> it1 = g.getNeighbors(node1);
				while(it1.hasNext()){
					int node2,n2;
					if (entry.getValue().contains((node2 = it1.next())) && node2 != node1){
						if (map2.containsKey(node2)){
							n2 = map2.get(node2);
						}else{
							//System.out.println("node2 = "+node2);
							n2 = n;
							n++;
							map1.put(n2, node2);
							map2.put(node2, n2);					
						}
						m[n1][n2] = 1;
						m[n2][n1] = 1;
					}
				}
			}
			//System.out.println(n);
			int[] v = new int[m.length];
			for (int i=0; i<m.length;i++){
				for (int j=0; j<m.length;j++){
					v[j] = m[i][j];
					//System.out.print(v[j] + " ");
				}
				v = multiply(v, m);
				v = multiply(v, m);

				int res = 0;
				for (int j=0; j<m.length;j++){
					res += v[j];
					//System.out.println(res);
				}
				scores.put(map1.get(i), res);
				//System.out.println(map1.get(i) + " : " + res);
			}
		}


		return scores;
	}

	public static int[] multiply(int[] v, int[][] m) {
		int size;
		if ((size=v.length)!=m.length) {
			System.out.println("Not compatible sizes");
			return null;
		}
		int[] res = new int[size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < m[0].length; j++) {
				res[i] += v[i] * m[i][j];
			}
		}
		return res;
	}

	private TrianglesAndTriplets trianglesAndTriplets(Graph<Integer, Float> g,
			Map<Integer, Set<Integer>> clusters,
			Map<Integer, String> nodesMapItoS) {

		TrianglesAndTriplets trianglesTriplets = new TrianglesAndTriplets();
		//for each cluster
		int nbClusters = 0,lastNbClusters = 0;
		for (Entry<Integer, Set<Integer>> entry : clusters.entrySet()) {
			//Set<Set<Integer>> triangles = new HashSet<Set<Integer>>();
			//Set<Set<Integer>> connectedTriplets = new HashSet<Set<Integer>>(); 

			int nbTriangles = 0,nbTriplets=0;
			//int nbTests = 0;
			//for each node in the cluster
			for (Integer node1 : (Set<Integer>)entry.getValue()){
				Iterator<Integer> it1 = g.getNeighbors(node1);
				while(it1.hasNext()){
					int node2;
					if (entry.getValue().contains((node2 = it1.next())) && node2 != node1){
						Iterator<Integer> it2 = g.getNeighbors(node2);
						while(it2.hasNext()){
							Iterator<Integer> it1bis = g.getNeighbors(node1);
							int node3;
							if (entry.getValue().contains((node3 = it2.next())) && node3 != node1 && node3 != node2){							
								//Set<Integer> set = new HashSet<Integer>();
								//set.add(node1);
								//set.add(node2);
								//set.add(node3);
								//connectedTriplets.add(set);
								nbTriplets++;
								//System.out.println("triplet : " + nodesMapItoS.get(node1) + " " + nodesMapItoS.get(node2) + " " + nodesMapItoS.get(node3));
								while(it1bis.hasNext()){
									//nbTests++;
									if (it1bis.next().intValue() == node3){
										//System.out.println("node1="  +node1 +  "  node2="  +node2 + "  node3="  +node3);
										//triangles.add(set);
										nbTriangles++;
										break;
									}
								}
							}
						}
					}
				}
			}
			
			nbTriangles /= 6;
			nbTriplets = ((nbTriplets - (nbTriangles*6))/2)+nbTriangles;
			//System.out.println("triangles : " + triangles.size() + " "+nbTriangles);
			//System.out.println("triplets : " + connectedTriplets.size() + " " + nbTriplets + "\n");

			trianglesTriplets.getTriangles().put(entry.getKey(), nbTriangles);
			trianglesTriplets.getTriplets().put(entry.getKey(), nbTriplets);

			nbClusters++;
			if (nbClusters>lastNbClusters+100){
				System.out.println("cluster " + nbClusters);
				lastNbClusters=nbClusters;
			}

		}
		return trianglesTriplets;
	}


	private Map<Integer, Integer> connectedTripletsInClusters(
			Graph<Integer, Float> g, Map<Integer, Set<Integer>> clusters,
			Map<Integer, String> nodesMapItoS) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int nbClusters = 0,lastNbClusters = 0;
		for (Entry<Integer, Set<Integer>> entry : clusters.entrySet()) {
			Set<Set<Integer>> connectedTriplets = new HashSet<Set<Integer>>(); 
			//int nbTriangles = 0;
			//int nbTests = 0;
			//for each node in the cluster
			for (Integer node1 : (Set<Integer>)entry.getValue()){
				Iterator<Integer> it1 = g.getNeighbors(node1);
				while(it1.hasNext()){
					int node2;
					if (entry.getValue().contains((node2 = it1.next())) && node2 != node1){
						Iterator<Integer> it2 = g.getNeighbors(node2);
						while(it2.hasNext()){
							int node3;
							if (entry.getValue().contains((node3 = it2.next())) && node3 != node1 && node3 != node2){							
								//nbTests++;
								//System.out.println("node1="  +node1 +  "  node2="  +node2 + "  node3="  +node3);
								//nbTriangles++;
								Set<Integer> set = new HashSet<Integer>();
								set.add(node1);
								set.add(node2);
								set.add(node3);
								connectedTriplets.add(set);
							}
						}
					}
				}
			}
			map.put(entry.getKey(), connectedTriplets.size());

			nbClusters++;
			if (nbClusters>lastNbClusters+100){
				System.out.println("cluster " + nbClusters);
				lastNbClusters=nbClusters;
			}

		}
		return map;
	}

	private Map<Integer, Integer> trianglesInClusters(Graph<Integer, Float> g,
			Map<Integer, Set<Integer>> clusters, Map<Integer, String> nodesMapItoS) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		//for each cluster
		int nbClusters = 0,lastNbClusters = 0;
		for (Entry<Integer, Set<Integer>> entry : clusters.entrySet()) {
			Set<Set<Integer>> triangles = new HashSet<Set<Integer>>(); 
			//int nbTriangles = 0;
			//int nbTests = 0;
			//for each node in the cluster
			for (Integer node1 : (Set<Integer>)entry.getValue()){
				Iterator<Integer> it1 = g.getNeighbors(node1);
				while(it1.hasNext()){
					int node2;
					if (entry.getValue().contains((node2 = it1.next())) && node2 != node1){
						Iterator<Integer> it2 = g.getNeighbors(node2);
						while(it2.hasNext()){
							Iterator<Integer> it1bis = g.getNeighbors(node1);
							int node3;
							if (entry.getValue().contains((node3 = it2.next())) && node3 != node1 && node3 != node2){							
								while(it1bis.hasNext()){
									//nbTests++;
									if (it1bis.next().intValue() == node3){
										//System.out.println("node1="  +node1 +  "  node2="  +node2 + "  node3="  +node3);
										//nbTriangles++;
										Set<Integer> set = new HashSet<Integer>();
										set.add(node1);
										set.add(node2);
										set.add(node3);
										if (!triangles.contains(set)){
											triangles.add(set);
										}
									}
								}
							}
						}
					}
				}
			}
			//nbTriangles = nbTriangles/6;
			map.put(entry.getKey(), triangles.size());
			//if (nbTriangles>0)System.out.println(nbTriangles);
			/*System.out.println("cluster " + entry.getKey() +" ("+entry.getValue().size() +" éléments) : " + triangles.size()+" triangles");
			for (int node : entry.getValue()){
				System.out.print(nodesMapItoS.get(node).toString() + " ");				
			}
			System.out.println("\nTriangles:");
			for (Set<Integer> set : triangles){
				System.out.print(set.size() + " ");
				for (int node : set){
					System.out.print(nodesMapItoS.get(node).toString() + " ");				
				}
				System.out.println();
			}*/
			nbClusters++;
			if (nbClusters>lastNbClusters+100){
				System.out.println("cluster " + nbClusters);
				lastNbClusters=nbClusters;
			}

		}
		return map;
	}

	public class TrianglesAndTriplets{

		private Map<Integer, Integer> triangles = new HashMap<Integer, Integer>();
		private Map<Integer, Integer> triplets = new HashMap<Integer, Integer>();

		public TrianglesAndTriplets() {
			super();
			this.triangles = new HashMap<Integer, Integer>();
			this.setTriplets(new HashMap<Integer, Integer>());
			// TODO Auto-generated constructor stub
		}

		public TrianglesAndTriplets(Map<Integer, Integer> triangles,
				Map<Integer, Integer> triplets) {
			super();
			this.setTriangles(triangles);
			this.setTriplets(triplets);
		}

		public String clusteringCoefficient(int clusterIndex){
			if (this.getTriplets().get(clusterIndex)==0) return "0.0";
			else {

				Double res = (this.getTriangles().get(clusterIndex).floatValue()/this.getTriplets().get(clusterIndex).doubleValue());
				DecimalFormat df = new DecimalFormat("0.00");
				return (df.format(res));
			}
		}

		public Map<Integer, Integer> getTriangles() {
			return triangles;
		}

		public void setTriangles(Map<Integer, Integer> triangles) {
			this.triangles = triangles;
		}

		public Map<Integer, Integer> getTriplets() {
			return triplets;
		}

		public void setTriplets(Map<Integer, Integer> triplets) {
			this.triplets = triplets;
		}
	}

}	

