package Knapsack;

import entity.*;

import java.util.ArrayList;

public class Knapsack {

	private ArrayList<ArrayList<ClusterBillboard>> matrix;
	private ArrayList<ArrayList<ClusterBillboard>> matrixCand;

	public void showData() {
		for (int i = 1; i < matrix.size(); i++) {
			System.out.print(i + " : ");
			for (int n = 1; n < matrix.get(1).size(); n++) {
				matrix.get(i).get(n).printInfl();
			}
			System.out.println();
		}
	}

	public void showReslut() {
		for (int i = 1; i < matrixCand.size(); i++) {
			System.out.print(i + " : ");
			for (int n = 1; n < matrixCand.get(1).size(); n++) {
				System.out.print(matrixCand.get(i).get(n).getInfluence());
			}
			System.out.println();
		}
	}

	public Knapsack(ArrayList<ArrayList<ClusterBillboard>> matrix) {
		this.matrix = matrix;
		this.matrixCand = new ArrayList<ArrayList<ClusterBillboard>>(this.matrix.size()+1);
	}

	public void knapsack(double budget) {
		int numberOfClass = matrix.size();
		ArrayList<ClusterBillboard> newMatrixRow = new ArrayList<ClusterBillboard>();//to be generated
		ArrayList<ClusterBillboard> lastMatrixRow;// already have been generated
		ArrayList<ClusterBillboard> matrixRow;//chosen from here
		matrixCand.add(newMatrixRow);// row 0 is null

		for (int i = 1; i <= numberOfClass; i++) {
			matrixRow=matrix.get(i-1);
			newMatrixRow = new ArrayList<ClusterBillboard>(numberOfClass);
			lastMatrixRow = matrixCand.get(i - 1);
			System.out.print(i + " : ");
			// matrixCand.get(i).get(0).add(new ClusterBillboard());
			newMatrixRow.add(new ClusterBillboard());// column 0 is null


			for (int b = 1; b <= budget; b++) {// budget
				int maxInfulenceIndex=0;
				for(int m=maxInfulenceIndex+1; m<matrixRow.size();m++){
					if(matrixRow.get(m).getCharge()<b)
						maxInfulenceIndex=m;
					else
						break;
				}

				if (maxInfulenceIndex==0) {
					// matrixCand.get(i).get(b).add(matrixCand.get(i).get(b).get());
					if(i==1)
						newMatrixRow.add(new ClusterBillboard());
					else
					newMatrixRow.add(lastMatrixRow.get(b));
				} else {
					int maxInfluence = 0;
					int maxIndex = -1;
					int preInfluence = 0;
					int influence = 0;

					for(int q=0;q<=maxInfulenceIndex;q++){

						preInfluence=lastMatrixRow.get((int)(b-newMatrixRow.get(q).getCharge())).getInfluence();
						influence = matrixRow.get(q).getInfluence();
						if (influence + preInfluence > maxInfluence) {
							maxInfluence = influence + preInfluence;
							maxIndex = q;
						}
					}

					//for (int q = 0; q <= b; q++) {// find max
					//
					//	preInfluence = lastMatrixRow.get(b - 1).getInfluence();
					//	// matrix.get(i).get(b).getInfluence();
					//	influence = matrixRow.get(maxInfulenceIndex).getInfluence();
					//	if (influence + preInfluence > maxInfluence) {
					//		maxInfluence = influence + preInfluence;
					//		maxIndex = q;
					//	}
					//}

					if (maxInfluence > lastMatrixRow.get(b).getInfluence()) {
						newMatrixRow.add(lastMatrixRow.get((int)(b-newMatrixRow.get(maxIndex).getCharge())));
						newMatrixRow.get(b).add(matrix.get(i).get(maxIndex));
					} else
						newMatrixRow.add(lastMatrixRow.get(b));

					//if (maxInfluence > lastMatrixRow.get(b).getInfluence()) {
					//	newMatrixRow.add(lastMatrixRow.get(b - maxIndex));
					//	newMatrixRow.get(b).add(matrix.get(i).get(maxIndex));
					//} else
					//	newMatrixRow.add(lastMatrixRow.get(b));
				}
			}
			matrixCand.add(newMatrixRow);
			System.out.println();
		}
	}

}
