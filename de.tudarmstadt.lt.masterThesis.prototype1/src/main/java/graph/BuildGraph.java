package graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BuildGraph {

	public static ResultatBuildGraph buildGraph(String fileName) throws IOException{
		
		BufferedReader br;
		br = new BufferedReader(new FileReader(fileName));
		

		String line = null;
		int N = 100;
		String[] lineSplit = new String[4];
		Map<Integer,String> nodesMapItoS = new HashMap<Integer,String>();
		Map<String,Integer> nodesMapStoI = new HashMap<String,Integer>();
		Graph<Integer, Float> g = new ArrayBackedGraph<Float>(N, 1);
		br.readLine();
		int i = 0;
		int root = 0; //root word of each cluster,line
		int linectr=0,linectr2 = 0;
		while ( (line = br.readLine()) != null){ 
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
				root = i;
				i++;
				g.addNode(root);
				if (nodesMapItoS.containsKey(root)) System.out.println("duplicated node for root = "+root);
				nodesMapItoS.put(Integer.valueOf(root), word);
				nodesMapStoI.put(word, Integer.valueOf(root));
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
							nodesMapItoS.put(Integer.valueOf(i), neighbourSp.split(":")[0]);
							nodesMapStoI.put(neighbourSp.split(":")[0] , Integer.valueOf(i));
							g.addEdgeUndirected(root, i, Float.valueOf(neighbourSp.split(":")[1]));
							i++;
						}
					}
				}
			}
		}
		System.out.println("graph built");
		ResultatBuildGraph res = new ResultatBuildGraph(g,nodesMapItoS,nodesMapStoI);
		return res;
	}

}
