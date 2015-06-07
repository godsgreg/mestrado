package searchers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;
import net.semanticmetadata.lire.AbstractImageSearcher;
import net.semanticmetadata.lire.impl.VisualWordsImageSearcher;
import net.semanticmetadata.lire.utils.FileUtils;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import utilidades.NameFinder;

public class CriadorConsultasBase {
	
	private static int rankSize = 100;
	public static String[] databasePath = {"colecaoDafitiPosthaus"};
//	public static String[] databasePath = {"Consultas_Relevantes_DafitiPosthaus/imgsConsulta/colecaoAvulso_semParticao",
//		"Consultas_Relevantes_DafitiPosthaus/imgsConsulta/colecaoBaseDafiti_semParticao",
//		"Consultas_Relevantes_DafitiPosthaus/imgsConsulta/colecaoHard_semParticao"};
	public static String[] indexPath = {"indexACC","indexCEDD","indexCLD","indexFCTH","indexGCH","indexJCD","indexPHOG"};//,"indexSIFT"};
	
	public static void buscar() throws IOException {
		BufferedImage img = null;
		ImageSearcher[] searchers = {
				ImageSearcherFactory.createAutoColorCorrelogramImageSearcher(rankSize),
				ImageSearcherFactory.createCEDDImageSearcher(rankSize),	
				ImageSearcherFactory.createColorLayoutImageSearcher(rankSize),
				ImageSearcherFactory.createFCTHImageSearcher(rankSize),
				ImageSearcherFactory.createColorHistogramImageSearcher(rankSize),
				ImageSearcherFactory.createJCDImageSearcher(rankSize),
				ImageSearcherFactory.createPHOGImageSearcher(rankSize)//,
				//AbstractImageSearcher.clasVisualWordsImageSearcher(rankSize),
		};
		for(int i = 0; i< 1; i++){
			for(int j = 5; j < indexPath.length; j++){

				IndexReader ir = DirectoryReader.open(FSDirectory.open(new File(indexPath[j])));
				ImageSearcher searcher = searchers[j];//ImageSearcherFactory.createCEDDImageSearcher(rankSize);

				ArrayList<String> images = FileUtils.getAllImages(new File(databasePath[i]), true);

				//String subFolderName = databasePath[i].split("/")[2];
				
				if (!(new File("consultas/" + indexPath[j].substring(5))).mkdirs()) {
					//System.exit(1);
				}
				//File arq = new File("consultas/" + indexPath[j] + "/" + indexPath[j].substring(5));
				//FileWriter fw = new FileWriter(arq);

				System.out.println("Searching from " + indexPath[j] + "...");

				if (images.size()>0) {
					for (Iterator<String> it = images.iterator(); it.hasNext(); ) {

						String imageFilePath = it.next();
						System.out.println(searcher.toString());
						img = ImageIO.read(new FileInputStream(imageFilePath));
						//System.out.println(img + " \n" + ir);
						ImageSearchHits hits = searcher.search(img, ir);
						//System.out.println("TESTE");
						//success = (new File("../potentially/long/pathname/without/all/dirs")).mkdirs();
						

						System.out.println("Results for " + NameFinder.findName(imageFilePath) + ":");
						File arq = new File("consultas/" + indexPath[j].substring(5) + "/" + NameFinder.findName(imageFilePath).replace(".jpg", ".txt"));
						FileWriter fw = new FileWriter(arq);

						for (int k = 0; k < hits.length(); k++) {
							String fileName = hits.doc(k).getValues(net.semanticmetadata.lire.DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
							//System.out.println(hits.score(i) + ": \t" + NameFinder.findName(fileName));
							if(!fileName.equals(imageFilePath))
								fw.append(hits.score(k) + " " + NameFinder.findName(fileName) + "\n");
						}

						fw.close();
					}
				}
			}
		}
	}
}
