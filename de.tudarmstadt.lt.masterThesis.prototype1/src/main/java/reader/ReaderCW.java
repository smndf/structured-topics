package reader;

import graph.ArrayBackedGraph;
import graph.Graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import cw.CW;
import util.MapUtil;

public class ReaderCW {

	public void readTextFile(String fileName) throws IOException {

		//FileReader fr = new FileReader("testReader.txt");
		InputStream is = getClass().getResourceAsStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

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

		Map<Integer,Integer> nbTriangles = trianglesInClusters(g,clusters);


		File fo = new File("ClustersCWFilt.txt");
		try{
			FileWriter fw = new FileWriter (fo);
			fw.write (MapUtil.toString(clusters,nbTriangles,nodesMapItoS,"\t","\n"));
			fw.close();
		}
		catch (IOException exception)
		{
			System.out.println (exception.getMessage());
		}

		//System.out.println(MapUtil.toString(clusters,nodesMapItoS," : ","\n"));
	}

	private Map<Integer, Integer> trianglesInClusters(Graph<Integer, Float> g,
			Map<Integer, Set<Integer>> clusters) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		//for each cluster
		for (Entry<Integer, Set<Integer>> entry : clusters.entrySet()) {
			int nbTriangles = 0;
			//int nbTests = 0;
			//for each node in the cluster
			for (Integer node1 : (Set<Integer>)entry.getValue()){
				Iterator<Integer> it1 = g.getNeighbors(node1);
				while(it1.hasNext()){
					int node2 = it1.next();
					Iterator<Integer> it2 = g.getNeighbors(node2);
					while(it2.hasNext()){
						Iterator<Integer> it1bis = g.getNeighbors(node1);
						int node3 = it2.next();
						if (node3 != node1){							
							while(it1bis.hasNext()){
								//nbTests++;
								if (it1bis.next() == node3){
									//System.out.println("node1="  +node1 +  "  node2="  +node2 + "  node3="  +node3);
									nbTriangles++;
								}
							}
						}
					}
				}
			}
			nbTriangles = nbTriangles/6;
			map.put(entry.getKey(), nbTriangles);
			//if (nbTriangles>0)System.out.println(nbTriangles);
			//System.out.println(entry.getValue().size() +" " + nbTriangles +" " + nbTests);
		}
		return map;
	}
}	

