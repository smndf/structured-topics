package reader;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import mcl.MarkovClustering;
import mcl.Matrix;
import mcl.Vectors;

	public class ReaderMCL {

	public void readTextFile(String fileName, int nbNodes) throws IOException {

		System.out.print("Reading input file and building matrix...");	
		//FileReader fr = new FileReader("testReader.txt");
		InputStream is = getClass().getResourceAsStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		String line = null;
		
		String[] lineSplit = new String[4];
		Map<Integer,String> nodesMapItoS = new HashMap<Integer,String>();
		Map<String,Integer> nodesMapStoI = new HashMap<String,Integer>();
		//Graph<Integer, Float> g = new ArrayBackedGraph<Float>(N, 6);
		double[][] matrix = new double[nbNodes+1][nbNodes+1];
		br.readLine();
		int i = 0;
		int root = 0; //root word of each cluster,line
		int linectr=0,linectr2 = 0;
		while ( (line = br.readLine()) != null){ 
			
			root = i;
			
			linectr++;
			if (linectr==100){
				linectr2 +=linectr;
				System.out.println("line " + linectr2);
				linectr=0;
			}
			
			lineSplit = line.split("\t");
			//ArrayList neighbours = new ArrayList<String>();
			//System.out.println(lineSplit[0]);
			String word = lineSplit[0] + "#" + lineSplit[1];
			
			if (nodesMapStoI.containsKey(word)){
				root = nodesMapStoI.get(word);
			}else{
					nodesMapItoS.put(i, word);
					nodesMapStoI.put(word, i);
					//matrix = Vectors.increaseSize(matrix, 1, 1);
					i++;
			}
			if (lineSplit.length>2){
				//System.out.println(lineSplit[2]);
				ArrayList<String> neighboursSplit = new ArrayList<String>(Arrays.asList(lineSplit[2].split(",")));
				for (String neighbourSp : neighboursSplit){
					//neighbours.add(neighbourSp.split(":")[0]);
					// if node already added
					if (neighbourSp.split(":").length>1){							
						if (nodesMapStoI.containsKey(neighbourSp.split(":")[0])){
							int node = nodesMapStoI.get(neighbourSp.split(":")[0]);
							matrix[node][root]+=Double.valueOf(neighbourSp.split(":")[1]);
							matrix[root][node]+=Double.valueOf(neighbourSp.split(":")[1]);

						}else{
								nodesMapItoS.put(i, neighbourSp.split(":")[0]);
								nodesMapStoI.put(neighbourSp.split(":")[0] , i);
								//matrix = Vectors.increaseSize(matrix, 1, 1);
								//g.addEdgeUndirected(root, i, Float.valueOf(neighbourSp.split(":")[1]));
								matrix[i][root]+=Double.valueOf(neighbourSp.split(":")[1]);
								matrix[root][i]+=Double.valueOf(neighbourSp.split(":")[1]);
								i++;
						}
					}
				}
			}
		}
		System.out.println("OK");	

		
		
		System.out.print("Converting double into float...");
		float[][] floatMatrix = new float[matrix.length][matrix[0].length];
		for (i=0;i<matrix.length;i++){
			for (int j=0;j<matrix[0].length;j++){
				// TODO make sure double to float is ok
				floatMatrix[i][j] = (float) matrix[i][j]; 
			}
		}	
		System.out.println("OK");

		
		
		System.out.print("Running Markov Clustering algo...");
		Matrix m = new Matrix(floatMatrix);
		MarkovClustering mc = new MarkovClustering();
		Matrix m2 = mc.run(m, 0, 2.0, 1.0, 0.00000001);
		System.out.println("OK");	

		
		
		System.out.println("\nmap words :");
		System.out.println(nodesMapStoI.toString());

		String output = "resultsMCL.txt";
		writeResultsMCL(m2, nodesMapItoS, output);
		System.out.println("Results written in file "+output);
		
	}
	
	public void writeResultsMCL(Matrix m, Map<Integer,String> nodesMap, String fileName){
		File fo = new File(fileName);
		
		try
		{
			FileWriter fw = new FileWriter (fo);
			
			StringWriter writer = new StringWriter();
			StringWriter cluster = new StringWriter();

			int noTopic = 0;
			Boolean emptyColumn = true;
			for (int j = 0; j<m.size-1; j++){
				emptyColumn = true;
				//writer.write(nodesMap.get(j).toString() + " ");
				for (int i = 0; i<m.size-1;i++){
					if (m.data[i][j]!=0){
						emptyColumn = false;
						break;
					}
				}
				if (!emptyColumn){
					writer.write(noTopic + "\t");
					for (int i = 0; i<m.size-1;i++){
						
						if (m.data[i][j]!=0){
							cluster.write(nodesMap.get(i).toString() + ",");
						}
						
						
					}
					// to remove the last , 
					cluster.getBuffer().setLength(cluster.toString().length()-1);
					writer.write(cluster.toString());
					cluster.getBuffer().setLength(0);
					writer.write("\n");	
					noTopic++;
				}
			}
			fw.write (writer.toString());
			
			fw.close();
		}
		catch (IOException exception)
		{
			System.err.println ("Erreur lors de la lecture : " + exception.getMessage());
		}
	}

	public int getNumberNodes(String fileName) throws IOException {
		System.out.print("Reading input file to know number of nodes...");	
		//FileReader fr = new FileReader("testReader.txt");
		InputStream is = getClass().getResourceAsStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		String line = null;
		int numberOfNodes = 0;
		String[] lineSplit = new String[4];
		Map<Integer,String> nodesMapItoS = new HashMap<Integer,String>();
		Map<String,Integer> nodesMapStoI = new HashMap<String,Integer>();
		//Graph<Integer, Float> g = new ArrayBackedGraph<Float>(N, 6);
		//double[][] matrix = new double[1][1];
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
			//ArrayList neighbours = new ArrayList<String>();
			//System.out.println(lineSplit[0]);
			String word = lineSplit[0];
			
			if (nodesMapStoI.containsKey(word)){
				root = nodesMapStoI.get(word);
			}else{
					nodesMapItoS.put(i, word);
					nodesMapStoI.put(word, i);
					//matrix = Vectors.increaseSize(matrix, 1, 1);
					i++;
			}
			if (lineSplit.length>2){
				//System.out.println(lineSplit[2]);
				ArrayList<String> neighboursSplit = new ArrayList<String>(Arrays.asList(lineSplit[1].split(",")));
				for (String neighbourSp : neighboursSplit){
					//neighbours.add(neighbourSp.split(":")[0]);
					// if node already added
					if (neighbourSp.split(":").length>1){							
						if (nodesMapStoI.containsKey(neighbourSp.split(":")[0])){
							
						}else{
								nodesMapItoS.put(i, neighbourSp.split(":")[0]);
								nodesMapStoI.put(neighbourSp.split(":")[0] , i);
								i++;
						}
					}
				}
			}
		}
		System.out.println("OK");	
		return i-1;
	}
}	
