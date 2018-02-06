package Knapsack;

import entity.Billboard;
import fileIO.finalResult.FinalResultReader;
import partition.PartitionAlgorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Knapsack3 {

	private ArrayList<ClusterBillboard> candidate;
	private ArrayList<ArrayList<ClusterBillboard>> matrixResult;

	public void showData() {
		for (int i = 1; i < candidate.size(); i++) {
			System.out.print(i + " : ");
			candidate.get(i).printInfl();
			System.out.println();
		}
	}

	public static void main(String[] args) {
		int maxCluster = 1;
		int clusterNumber = 0;
		PartitionAlgorithm partitionAlg = new PartitionAlgorithm(1,1,1);
		List<List<Billboard>> clusters = partitionAlg.getClusters();
		ArrayList<ClusterBillboard> candidate = new ArrayList<ClusterBillboard>();

		for(List<Billboard> billboards : clusters){
			ClusterBillboard cluster = new ClusterBillboard();
			for(Billboard billboard: billboards){
				cluster.add(billboard);
			}
			candidate.add(cluster);
		}

		Date date1 = new Date();
		Knapsack3 knapsack = new Knapsack3(candidate);
		ClusterBillboard result = knapsack.knapsack(100);
		System.out.print("done");
		Date date2 = new Date();
		long begin = date1.getTime();
		long end = date2.getTime();
		System.out.println("runtime: " + ((end - begin)/1000d));
		System.out.println("runtime: " + ((end - begin)/1000d));

	}

	public void showReslut() {
		for (int i = 1; i < matrixResult.size(); i++) {
			System.out.print(i + " : ");
			for (int n = 1; n < matrixResult.get(1).size(); n++) {
				System.out.print(matrixResult.get(i).get(n).getInfluence());
			}
			System.out.println();
		}
	}

	public Knapsack3(ArrayList<ClusterBillboard> candidate) {
		this.candidate = candidate;
		this.matrixResult = new ArrayList<ArrayList<ClusterBillboard>>(this.candidate.size());
	}

	public ClusterBillboard knapsack(double budget) {
		int numberOfClass = candidate.size() - 1;
		ArrayList<ClusterBillboard> newMatrixRow = new ArrayList<ClusterBillboard>((int)budget);//to be generated
		ArrayList<ClusterBillboard> lastMatrixRow ;// already have been generated
		for(int i=0;i<=budget;i++){
			newMatrixRow.add(new ClusterBillboard());
		}
		matrixResult.add(newMatrixRow);

		for (int i = 1; i <= numberOfClass; i++) {
			newMatrixRow = new ArrayList<ClusterBillboard>((int)budget+1);
			lastMatrixRow = matrixResult.get(i - 1);
			System.out.print(i + " : ");
			// matrixCand.get(i).get(0).add(new ClusterBillboard());
			newMatrixRow.add(new ClusterBillboard());// column 0 is null

			for (int b = 1; b <= budget; b++) {// budget
				if(candidate.get(i).getCharge()>b){
					newMatrixRow.add(lastMatrixRow.get(b));//if over budget, get the data from the same position of the previous row
				}
				else {
					int preInfluence = 0;
					int influence = 0;
					preInfluence = lastMatrixRow.get((int) (b - candidate.get(i).getCharge())).getInfluence();
					influence = candidate.get(i).getInfluence();
					if (preInfluence + influence > lastMatrixRow.get(b).getInfluence()) {
						newMatrixRow.add(new ClusterBillboard());
						newMatrixRow.get(b).add(lastMatrixRow.get((int) (b - candidate.get(i).getCharge())));
						newMatrixRow.get(b).add(candidate.get(i));
					} else {
						newMatrixRow.add(lastMatrixRow.get(b));
					}
				}
			}
			matrixResult.add(newMatrixRow);
		}
		//return the last column of the last row
		ArrayList<ClusterBillboard> resultRow = matrixResult.get(matrixResult.size()-1);
		ClusterBillboard finalResult = resultRow.get(resultRow.size()-1);
		return finalResult;
	}

}
