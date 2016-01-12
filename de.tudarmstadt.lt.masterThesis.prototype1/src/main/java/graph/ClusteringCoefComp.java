package graph;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import reader.ReaderCW.TrianglesAndTriplets;

public class ClusteringCoefComp {

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

	public TrianglesAndTriplets trianglesAndTriplets(Graph<Integer, Float> g,
			Map<Integer, Set<Integer>> clusters) {


		TrianglesAndTriplets trianglesTriplets = new TrianglesAndTriplets();
		Boolean b = false;
		if (b){
			for (Entry<Integer, Set<Integer>> entry : clusters.entrySet()) {
				trianglesTriplets.getTriangles().put(entry.getKey(), 0);
				trianglesTriplets.getTriplets().put(entry.getKey(), 0);
			}
		} else {
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

		}
		return trianglesTriplets;
	}


}
