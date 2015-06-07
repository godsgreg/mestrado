package searchers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.semanticmetadata.lire.utils.FileUtils;

public class TextualSearcher {
	static ModeloVetorial MV;
	static String imagesPath = utilidades.ProjectSetup.databasePath;//consultasPath;
	static String pathConsultas = utilidades.ProjectSetup.ranksBasePath;//ranksConsultasPath;
	static String[] descritores = {"ACC","BIC","CEDD","CLD","FCTH","GCH","JCD","PHOG","SIFT"};
	
	
	public static void buscar() throws Exception{
		MV = new ModeloVetorial();
		ArrayList<String> images;

		images = FileUtils.getAllImages(new File(imagesPath), true);

		//para cada descritor visual
		for(int i = 8; i < 9/*descritores.length*/; i++){
			//para cada imagem da consulta
			for(String imagePath: images){
				System.out.println(descritores[i] + " - " + imagePath);
				imagePath = utilidades.NameFinder.findName(imagePath);
				File file = new File(pathConsultas + "/" + descritores[i] + "/" + imagePath.replace(".jpg","") + ".txt");
				//if(!file.exists()){
					Map<String, Float> rankVisual = new HashMap<String, Float>();
					Map<String, Float> rankMultiModal;
					try{
						//escreve um arquivo contendo o rank visual expandido pela busca textual
						BufferedReader br = new BufferedReader(new FileReader(file));
						System.out.println(file.exists());
						String line;
						while ((line = br.readLine()) != null) {
							//para cada linha, le o nome da imagem e valor de similaridade, em seguida adiciona em um mapa
							String[] dados = line.split(" ");
							if(i == 1)
								rankVisual.put(dados[0], Float.parseFloat(dados[1]));
							else
								rankVisual.put(dados[1], Float.parseFloat(dados[0]));
						}
						br.close();
						rankVisual = normalizaRank(rankVisual, true);
						
						System.out.println("Expandindo rank para o arquivo " + imagePath + ".txt para 1 palavra:");
						
						//expansão multi-modal para topo de tamanho 1, 5, 10 e 20
						rankMultiModal = expandeRank(rankVisual, 1);
						if (!(new File(pathConsultas + "/" + descritores[i] + "_text1").mkdirs())) {
							//System.exit(1);
						}
						escreveArquivo(rankMultiModal,pathConsultas + "/" + descritores[i] + "_text1" + "/" + imagePath.replace(".jpg","") + ".txt");
						
						//System.out.println("Expandindo rank para o arquivo " + String.valueOf(j) + ".txt para 5 palavra:");
						rankMultiModal = expandeRank(rankVisual, 5);
						if (!(new File(pathConsultas + "/" + descritores[i] + "_text5").mkdirs())) {
							//System.exit(1);
						}
						escreveArquivo(rankMultiModal,pathConsultas + "/" + descritores[i] + "_text5" + "/" + imagePath.replace(".jpg","") + ".txt");
						rankMultiModal = expandeRank(rankVisual, 10);
						if (!(new File(pathConsultas + "/" + descritores[i] + "_text10").mkdirs())) {
							//System.exit(1);
						}
						escreveArquivo(rankMultiModal,pathConsultas + "/" + descritores[i] + "_text10" + "/" + imagePath.replace(".jpg","") + ".txt");
						rankMultiModal = expandeRank(rankVisual, 20);
						if (!(new File(pathConsultas + "/" + descritores[i] + "_text20").mkdirs())) {
							//System.exit(1);
						}
						escreveArquivo(rankMultiModal,pathConsultas + "/" + descritores[i] + "_text20" + "/" + imagePath.replace(".jpg","") + ".txt");
	
					}catch(Exception ex){
						ex.printStackTrace();
						//System.exit(0);
					}
				//}
			}
		}

	}
	
	static boolean escreveArquivo(Map<String, Float> rank, String path){
		try{
			File file = new File(path);
			FileWriter fw = new FileWriter(file);
			for(String key: rank.keySet()){
				fw.append(String.valueOf(rank.get(key)) + " ");
				fw.append(key + "\n");
			}
			fw.close();
			//file.close();
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	static Map<String, Float> expandeRank(Map<String, Float> rank, int k){
		String[] rankStr = new String[rank.size()];
		float[] rankSim = new float[rank.size()];
		int i = 0;
		
		//gera query com base nas k primeiras imagens do rank e a primeira palavra de suas descrições
		for(String key : rank.keySet()){
		    rankStr[i] = key;
		    rankSim[i] = rank.get(key);
		    i++;
		}
		rankStr = ordena(rankStr, rankSim)[0]; //vetor ordenado
		String query = "";
		//para cada imagem do topo do rank, concatena um termo textual em uma consulta
		for(i = 0; i < k; i++){
			query += MV.descricao.get(rankStr[i]) + " ";
		}
		//System.out.println(query);
		//gera rank textual
		Map<String, Float> rankText = MV.similaridade(query);
		
		//ordena rank textual
		rankStr = new String[rankText.size()];
		rankSim = new float[rankText.size()];
		i = 0;
		for(String key : rankText.keySet()){
		    rankStr[i] = key;
		    rankSim[i] = rankText.get(key);
		    i++;
		}
		
		String[][] result = ordena(rankStr, rankSim);
		Map<String, Float> novoRank = new HashMap<String, Float>();
		//Insere novas imagens no rank, e atualiza as antigas		
		for(i = 0; i < 100; i ++){
			//Float f = rank.get(result[0][i]);
			//if(f == null){
				novoRank.put(result[0][i], Float.parseFloat(result[1][i]));
			//}else{
			//	novoRank.put(result[0][i], (Float.parseFloat(result[1][i]) + f) / 2);
			//}
		}

		return novoRank;
	}
	
	static String[][] ordena(String[] paths, float[] distancias){
		int menor;
		for(int i = 0; i < distancias.length - 1; i++){
			menor = i;
			for(int j = i+1; j < distancias.length; j++){
				if(distancias[j] < distancias[menor]){
					menor = j;
				}
			}
			if(menor != i){
				float temp;
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
	
	static Map<String, Float> normalizaRank(Map<String, Float> rank, boolean distance){
		if(!distance){
			for(String key: rank.keySet()){
				rank.put(key, 1 - rank.get(key));
			}
		}
		
		float menor = 10000.0f, maior = 0.0f;
		for(String key: rank.keySet()){
			if(rank.get(key) > maior){
				maior = rank.get(key);
			}
			if(rank.get(key) < menor){
				menor = rank.get(key);
			}
		}
		//System.out.println("maior: " + maior + " menor: " + menor);
		for(String key: rank.keySet()){
			/*if(!distance)
				System.out.println("Normalizando similaridade:");
			else
				System.out.println("Normalizando distancia:!!!!!!!!!!!!!!!!!!!!1");
			*/
			float newSim;
			//System.out.println("sim antiga: " + rank.get(key));
			newSim = (rank.get(key) - menor) / (maior - menor);
			
			if(newSim == 0.0f){
				newSim = 0.001f;
			}
			rank.put(key, newSim);
			//System.out.println("sim nova: " + rank.get(key) + "\n\n");
			
		}
		return rank;
	}
}
