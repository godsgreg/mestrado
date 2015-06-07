package utilidades;

import java.util.Random;

public class FoldGenerator {
	
	/**
	 * fold[numero da fold][validacao ou treino][vetor de numeros]
	 *     
	 */

	static Random r;
	
	public static int[][][] breakSetSelected(){
		
		int [][] ImageSets = {{39,22,11,1,3,34,14,40,49,9,45,2,48,4,18,8,5,7,37,20},
							{17,23,44,29,20,46,43,47,15,26,39,22,11,1,3,34,14,40,49,9},
							{33,50,21,36,10,13,19,12,38,35,17,23,44,29,30,46,43,47,15,26},
							{42,31,16,27,25,32,6,24,41,28,33,50,21,36,10,13,19,12,38,35},
							{45,2,48,4,18,8,5,7,37,20,42,31,16,27,25,32,6,24,41,28}};
		
		int[] images = new int[50];

		int[] validationSet = new int[10];
		int[][][] folds = new int[5][2][];
		for(int i = 0; i < 50; i++){
			images[i] = i+1;
		}
		for(int i = 5; i > 0; i--){
			validationSet = getInverse(ImageSets[i-1], images);
			folds[i-1][0] = new int[validationSet.length];
			for(int j =0; j < validationSet.length; j ++)
				folds[i-1][0][j] = validationSet[j];
			folds[i-1][1] = new int[50 - validationSet.length];
			folds[i-1][1] = ImageSets[i-1];
		}
		return folds;
	}
	
	public static int[][][] breakSet(){
		r = new Random();
		int[] images = new int[50];
		int[] imagesCpy = new int[50];
		int[] trainingSet = new int[40];
		int[] validationSet = new int[10];
		int[][][] folds = new int[5][2][];
		for(int i = 0; i < 50; i++){
			images[i] = i+1;
		}
		imagesCpy = images;
		for(int i = 5; i > 0; i--){
			int k = 0;
			for(int j = i * 10; j > ((i*10) - 10); j --){
				int randomNumber = r.nextInt(j);
				validationSet[k] = images[randomNumber];
				k++;
				images = removePos(images, randomNumber, j);
			}
			folds[i-1][0] = new int[10];
			for(int j =0; j < 10; j ++)
				folds[i-1][0][j] = validationSet[j];
			folds[i-1][1] = new int[40];
			folds[i-1][1] = getInverse(validationSet, imagesCpy);
		}
		return folds;
	}
	
	static int[] removePos(int [] vec, int pos, int tam){
		int[] newVec = new int[tam - 1];
		for(int i = pos; i < tam - 1; i++){
			vec[i] = vec[i+1];
		}
		for(int i = 0; i < tam - 1; i++){
			newVec[i] = vec[i];
		}
		return newVec;
	}
	
	static int [] getInverse(int [] vec, int[] imgs){
		int[] newImgCpy = new int[50];
		for(int i = 0; i < 50; i++){
			newImgCpy[i] = imgs[i]; 
		}
		
		for(int i = 0; i < vec.length; i++){
			for(int j = 0; j < 50 - i; j++){
				if(newImgCpy[j] == vec[i]){
					newImgCpy = removePos(newImgCpy, j, 50 - i );
					//System.out.println(newImgCpy.length);
					break;
				}
			}
		}
		return newImgCpy;
	}
	
	public static void main(String arg[]){
		int[][][] folds;
		folds = breakSet();
		for(int i = 0; i < 5; i++){
			System.out.print("fold " + i + ":[");
			for(int j = 0; j < folds[i][0].length; j++){
				System.out.print(folds[i][0][j] + ",");
				
			}
			System.out.print("][");
			for(int j = 0; j < folds[i][1].length; j++){
				System.out.print(folds[i][1][j] + ",");
				
			}
			System.out.println("]");
		}
	}
}
