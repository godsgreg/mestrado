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
import utilidades.NameFinder;

public class CategoryRanker {
	static ModeloVetorial MV;
	static String imagesPath = utilidades.ProjectSetup.databasePath;
	static String pathConsultas = utilidades.ProjectSetup.ranksBasePath;//ranksConsultasPath;
	static String[] descritores = {"ACC","BIC","CEDD","CLD","FCTH","GCH","JCD","PHOG","SIFT"};
	
	
	public void buscar(){
		//cria as listas invertidas
		MV = new ModeloVetorial();
		ArrayList<String> images;
		try {
			images = FileUtils.getAllImages(new File(imagesPath), true);
		
			for(int i = 8; i < 9/*descritores.length*/; i++){
				for(String image: images){
					System.out.println(descritores[i] + " - " + image);
					image = NameFinder.findName(image);
					File file = new File(pathConsultas + "/" + descritores[i] + "/" + image.replace(".jpg", "") + ".txt");
					//if(!file.exists()){
						Map<String, Float> rankVisual = new HashMap<String, Float>();
						Map<String, Integer> rankMultiModal;
						try{
							BufferedReader br = new BufferedReader(new FileReader(file));
							String line;
							while ((line = br.readLine()) != null) {
								//System.out.println(line);
								String[] dados = line.split(" ");
								if(i == 1)
									rankVisual.put(dados[0], Float.parseFloat(dados[1]));
								else
									rankVisual.put(dados[1], Float.parseFloat(dados[0]));
							}
							br.close();
							rankVisual = normalizaRank(rankVisual, true);
							
							//Gera os pesos das categorias para diferentes tamanhos de K: 1, 5, 10 e 20
							
							rankMultiModal = expandeRank(rankVisual, 1);
							if (!(new File(pathConsultas + "/" + descritores[i] + "_cat1").mkdirs())) {
								//System.exit(1);
							}
							escreveArquivo(rankMultiModal,pathConsultas + "/" + descritores[i] + "_cat1" + "/" + image.replace(".jpg", "")+ ".txt");
							
							//System.out.println("Expandindo rank para o arquivo " + String.valueOf(j) + ".txt para 5 palavra:");
							rankMultiModal = expandeRank(rankVisual, 5);
							if (!(new File(pathConsultas + "/" + descritores[i] + "_cat5").mkdirs())) {
								//System.exit(1);
							}
							escreveArquivo(rankMultiModal,pathConsultas + "/" + descritores[i] + "_cat5" + "/" + image.replace(".jpg", "")+ ".txt");
							rankMultiModal = expandeRank(rankVisual, 10);
							if (!(new File(pathConsultas + "/" + descritores[i] + "_cat10").mkdirs())) {
								//System.exit(1);
							}
							escreveArquivo(rankMultiModal,pathConsultas + "/" + descritores[i] + "_cat10" + "/" + image.replace(".jpg", "")+ ".txt");
							rankMultiModal = expandeRank(rankVisual, 20);
							if (!(new File(pathConsultas + "/" + descritores[i] + "_cat20").mkdirs())) {
								//System.exit(1);
							}
							escreveArquivo(rankMultiModal,pathConsultas + "/" + descritores[i] + "_cat20" + "/" + image.replace(".jpg", "")+ ".txt");
						}catch(Exception ex){
							ex.printStackTrace();
						}
					//}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static boolean escreveArquivo(Map<String, Integer> rankMultiModal, String path){
		try{
			File file = new File(path);
			FileWriter fw = new FileWriter(file);
			for(String key: rankMultiModal.keySet()){
				fw.append(String.valueOf(rankMultiModal.get(key)) + " ");
				fw.append(key + "\n");
			}
			fw.close();
			//file.close();
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	static Map<String, Integer> expandeRank(Map<String, Float> rank, int k){
		String[] rankStr = new String[rank.size()];
		float[] rankSim = new float[rank.size()];
		Map<String, Integer> frequencias = new HashMap<String, Integer>();
		int i = 0;
		
		//gera query com base nas k primeiras imagens do rank e a primeira palavra de suas descrições
		for(String key : rank.keySet()){
		    rankStr[i] = key;
		    rankSim[i] = rank.get(key);
		    i++;
		}
		rankStr = ordena(rankStr, rankSim)[0];
		String query = "";
		for(i = 0; i < k; i++){
			String[] dados = MV.descricao.get(rankStr[i]).split(" ");
			String categoria = dados[dados.length - 1];
			//System.out.println(categoria);
			Integer freq = frequencias.get(categoria);
			if(freq == null){
				frequencias.put(categoria, 1);
			}else{
				frequencias.put(categoria, frequencias.get(categoria) + 1);
			}
			//query += MV.descricao.get(rankStr[i]) + " ";
		}
		//System.out.println(query);
		
		Map<String, Integer> novoRank = new HashMap<String, Integer>();
		//Insere novas imagens no rank, e atualiza as antigas		
		for(String key: rank.keySet()){
			String[] dados = MV.descricao.get(key).split(" ");
			String categoria = dados[dados.length - 1];
			Integer freq = frequencias.get(categoria);
			if(freq == null){
				novoRank.put(key, 0);
			}else{
				novoRank.put(key, freq);
			}

		}

		return novoRank;
		//return rankText;
	}
	
	static String[][] ordena(String[] paths, float[] distancias){
		// crescente
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
			newSim = rank.get(key)/ maior;
			
			if(newSim == 0.0f){
				newSim = 0.001f;
			}
			rank.put(key, newSim);
			//System.out.println("sim nova: " + rank.get(key) + "\n\n");
			
		}
		return rank;
	}
}
