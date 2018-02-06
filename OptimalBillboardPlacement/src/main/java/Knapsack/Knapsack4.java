package Knapsack;

import entity.Billboard;
import partition.PartitionAlgorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Knapsack4 {

	private ArrayList<ArrayList<ClusterBillboard>> matrixCandidate;
	private ArrayList<ArrayList<ClusterBillboard>> matrixResult;
	public static int chargeDensity = 1;

	public Knapsack4(ArrayList<ArrayList<ClusterBillboard>> matrixCandidate,int chargeDensity) {
		this.chargeDensity=chargeDensity;
		this.matrixCandidate = matrixCandidate;
		this.matrixResult = new ArrayList<ArrayList<ClusterBillboard>>(this.matrixCandidate.size());
	}

	public void printFinalResult(ArrayList<ClusterBillboard> resultRow){
		ArrayList<Integer> budgetList = new ArrayList<Integer>();
		budgetList.add(50);
		budgetList.add(100);
		budgetList.add(150);
		budgetList.add(200);
		budgetList.add(250);
		budgetList.add(300);
		budgetList.add(350);
		budgetList.add(400);
		ClusterBillboard finalResult;
		try{
			for(Integer budget:budgetList) {
				int tripNumber=0;
				finalResult = resultRow.get(budget);
				for(Billboard billboard:finalResult.getBillboardList()){
					tripNumber+=billboard.routes.size();
				}
				System.out.println("Budget    " + budget);
				System.out.println("Influence " + finalResult.getInfluence());
				System.out.println("Overlap   " + (tripNumber-finalResult.getInfluence()));
				System.out.println("Charge    " + finalResult.getCharge());
				System.out.print("Billboard ");
				for(Billboard billboard: finalResult.getBillboardList()){
					System.out.print(billboard.panelID+",");
				}
				System.out.println();
			}
		} catch (Exception e){

		}
	}

	public ArrayList<ClusterBillboard> knapsack(double budget) {
		int numberOfClass = matrixCandidate.size();
		ArrayList<ClusterBillboard> candidate;
		ArrayList<ClusterBillboard> newMatrixRow = new ArrayList<ClusterBillboard>((int)budget);//to be generated
		ArrayList<ClusterBillboard> lastMatrixRow ;// already have been generated
		for(int i=0;i<=budget;i++){
			newMatrixRow.add(new ClusterBillboard());
		}
		matrixResult.add(newMatrixRow);

		for (int i = 1; i <= numberOfClass; i++) {
			newMatrixRow = new ArrayList<ClusterBillboard>((int)budget+1);
			lastMatrixRow = matrixResult.get(i - 1);
			candidate=matrixCandidate.get(i-1);
			System.out.print(i + " : ");
			// matrixCand.get(i).get(0).add(new ClusterBillboard());

			for(int m=0; m<=budget;m++){
				newMatrixRow.add(new ClusterBillboard());//initial new Row
			}

			for (int b = chargeDensity; b <= budget; b+=chargeDensity) {// budget
				double maxInfluence = 0;
				double influence = 0;
				int indexQ = -1;
				for(int q=chargeDensity;q<=b;q+=chargeDensity){
					influence = lastMatrixRow.get(b - q).getInfluence() + candidate.get(q).getInfluence();
					if((influence>lastMatrixRow.get(b).getInfluence() && (influence>maxInfluence))){
						maxInfluence=influence;
						indexQ=q;
					}
				}
				if(indexQ>=0){
					newMatrixRow.get(b).add(lastMatrixRow.get((int) (b-indexQ)));
					newMatrixRow.get(b).add(candidate.get(indexQ));
				}
				else {
					newMatrixRow.get(b).add(lastMatrixRow.get(b));
				}
			}
			matrixResult.add(newMatrixRow);
		}

		//return the last column of the last row
		ArrayList<ClusterBillboard> resultRow = matrixResult.get(matrixResult.size()-1);
		System.out.println("Result");
		printCandidate(matrixResult);
		printFinalResult(resultRow);//print result set
		return resultRow;
	}


	private void printCandidate(ArrayList<ArrayList<ClusterBillboard>> candidates){
		Integer[] length = new Integer[candidates.get(0).size()];
		for(int i=0;i<candidates.get(0).size();i++){
			length[i]=0;
		}
		for(int i=0;i<candidates.size();i++){
			ArrayList<ClusterBillboard> candidateRow=candidates.get(i);
			for(int n=chargeDensity;n<candidateRow.size();n+=chargeDensity){
				ClusterBillboard cluster = candidateRow.get(n);
				if(length[n]<cluster.getBillboardList().size()){
					length[n]=cluster.getBillboardList().size();
				}
			}
		}

		for(int i=0;i<candidates.size();i++){
			ArrayList<ClusterBillboard> candidateRow=candidates.get(i);
			System.out.print("ROW "+i +" : ");
			for(int n=chargeDensity;n<candidateRow.size();n+=chargeDensity){
				ClusterBillboard cluster = candidateRow.get(n);
				cluster.updateInfluence();
				System.out.print("B "+n+"{");
				int billboardnumber = cluster.getBillboardList().size();
				for(Billboard billboard:cluster.getBillboardList()){
					String message = billboard.panelID+"|";
					print(message,8);
				}
				for(int m=0;m<(length[n]-billboardnumber);m++){
					print("",8);
				}
				System.out.print("}");
			}
			System.out.println();
		}

		for(int i=0;i<candidates.size();i++){
			ArrayList<ClusterBillboard> candidateRow=candidates.get(i);
			System.out.print("ROW "+i +" : ");
			for(int n=chargeDensity;n<candidateRow.size();n+=chargeDensity){
				ClusterBillboard cluster = candidateRow.get(n);
				cluster.updateInfluence();
				System.out.print("B "+n+"{");
				int billboardNumber = cluster.getBillboardList().size();
				int tripNumber=0;
				//for(Billboard billboard:cluster.getBillboard()){
				//	tripNumber+=billboard.routes.size();
				//}
				tripNumber=cluster.getInfluence();
				print(tripNumber+"}",5);
			}
			System.out.println();
		}
	}

	public static void print(String message, int length)
	{
		message=String.format("%1$-"+length+"s",message);
		System.out.print(message);
	}

	public static void println(String message, int length)
	{
		message=String.format("%1$-"+length+"s",message);
		System.out.println(message);
	}
}
