package searchers;

import indexers.BIC;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

public class BicSearcher {
	static Map<String, double[]> indice = new HashMap<String, double[]>();
	static String consultaPath = utilidades.ProjectSetup.ranksConsultasPath;// "colecaoAvulso_semParticao";
	
	public static void buscadorBic(){	
		//Carrega indice na memoria
		try{
			BufferedReader br = new BufferedReader(new FileReader("indexBIC/indexBic.txt"));
			String line;
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
			   String[] dados = line.split(" ");
			   int arraylength = dados.length;
			   double[] vetor = new double[arraylength - 1];
			   for(int i = 1; i < arraylength; i++){
				   vetor[i-1] = Double.parseDouble(dados[i]);
			   }
			   indice.put(dados[0], vetor);
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			if (!(new File(consultaPath+"/BIC")).mkdirs()) {
			    //System.exit(1);
			}

			//Iterator it = indice.entrySet().iterator();

		   // while (it.hasNext()) {
			for (int i = 1; i <= 50; i++) {
				System.out.println("Searching file " + i +".jpg");
				String fileName = consultaPath+"/BIC/" + String.valueOf(i) + ".txt";

		        File arq = new File(fileName);
				FileWriter fw = new FileWriter(arq);
				
				fw.append(geraRank(utilidades.ProjectSetup.consultasPath+"/"+String.valueOf(i) + ".jpg"));
				fw.close();
		    }
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	static String geraRank(String path) throws FileNotFoundException, IOException{
		String[] imagePaths = new String[indice.size()];
		double[] imageDistances = new double[indice.size()];
		int i = 0;
		BufferedImage img = ImageIO.read(new FileInputStream(path));
		double[] vec = BIC.getVector(img);
		//System.out.println(indice.size());
		//Iterator<Entry<String, double[]>> it = indice.entrySet().iterator();
	    //while (it.hasNext()) {
		System.out.println("Camparing with index");
		for (String key : indice.keySet()) {
			
	      //  Map.Entry pairs = (Map.Entry)it.next();
	        //System.out.println(key);
	       // it.remove(); 
	        imagePaths[i] = key;
	        //double[] vec = getDistance(BIC.getVector(img),indice.get(key);
	        //System.out.println(path +" : "+indice.get(path));
	        imageDistances[i] = getDistance(vec,indice.get(key));
	        //System.out.println(i);
	        i++;
	        
	    }
		System.out.println("Sorting");
	    String[][] rank = ordena(imagePaths,imageDistances);
	    String result = "";
	    for(i =0; i< 101; i++){
	    	if(!rank[1][i].equals("0.0"))
	    		result = result + rank[1][i] + " " + rank[0][i] + "\n";
	    }
		return result;
	}
	
	static double getDistance(double[] d1, double[] d2){
		double somatorio = 0;
		//System.out.println("teste");
		for(int i = 0; i < d1.length; i++){
			//System.out.println(d2[i]);
			somatorio += Math.abs(d1[i] - d2[i]);
		}
		return somatorio;
	}
	
	static String[][] ordena(String[] paths, double[] distancias){
		int menor;
		for(int i = 0; i < distancias.length - 1; i++){
			menor = i;
			for(int j = i+1; j < distancias.length; j++){
				if(distancias[j] < distancias[menor]){
					menor = j;
				}
			}
			if(menor != i){
				double temp;
				String tempS;
				
				temp = distancias[i];
				distancias[i] = distancias[menor];
				distancias[menor] = temp;
				
				tempS = paths[i];
				paths[i] = paths[menor];
				paths[menor] = tempS;
			}
		}
		String[][] results = new String[2][distancias.length];
		results[0] = paths;
		for(int i = 0; i < distancias.length - 1; i++){
			results[1][i] = String.valueOf(distancias[i]);
			
		}
		return results;
	}
}
