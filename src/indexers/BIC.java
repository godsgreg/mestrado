package indexers;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import utilidades.NameFinder;
import net.semanticmetadata.lire.utils.FileUtils;

public class BIC {
	/**
	 * Implementação do descritor BIC
	 */
	
	private static final int BIN = 4;
	private static int BINS = BIN * BIN * BIN;

	private static int bin(int v) {
		int i = 0, b;
		int[] bin = { 0, 64, 128, 192, 255 };
		for (b = 0; b < BIN; b++)
			if (v >= bin[b] && v <= bin[b + 1]) {
				i = b;
				break;
			}

		return i;
	}

	private static int bin_rgp(Color c) {
		int r_bin, g_bin, b_bin;

		r_bin = bin(c.getRed());
		g_bin = bin(c.getGreen());
		b_bin = bin(c.getBlue());

		return r_bin + g_bin + b_bin;
	}

	public static double[] getVector(BufferedImage image){
		double[] d = new double[BINS*2+1];
		
		int w = image.getWidth();
	    int h = image.getHeight();

	    int[] dataBuffInt = image.getRGB(0, 0, w, h, null, 0, w); 
	    
		int b, i, j;
		int rw = w;// 3*w; /* R G B */

		int[] hist_low = new int[BINS];
		int[] hist_high = new int[BINS];

		for (b=0; b < BINS; b++)
		{
			hist_low[b]  = 0;
			hist_high[b] = 0;
		}

		for (i=0; i < h; i++)
			for (j=0; j < rw; j+=3)
			{
				Color c = new Color(dataBuffInt[i*rw+j]);
				int v = bin_rgp(c);
				//v = c.getRed() + c.getGreen() + c.getBlue();
				//int v = bin_rgb( &image[i*rw+j] );

				if (i == 0 || i == h-1 || j == 0 || j == rw-3) /* border */
				{
					hist_low[v]++;
				}
				else
				{
					if (v == bin_rgp( new Color(dataBuffInt[(i-1)*rw+ j])) &&
							v == bin_rgp( new Color(dataBuffInt[(i+1)*rw+ j])) &&
							v == bin_rgp( new Color(dataBuffInt[i*rw+(j-3)])) &&
							v == bin_rgp( new Color(dataBuffInt[i*rw+(j+3)])))
					{
						hist_low[v]++;
					}
					else
					{
						hist_high[v]++;
					}
				}
			}

		for (b=0; b < BINS; b++)
		{
			d[b]      = (hist_low[b]  * 255.0) / (w*h);
			d[b+BINS] = (hist_high[b] * 255.0) / (w*h);
		}
		return d;
	}
	
	public static void index(String databasePath, String indexPath) throws Exception{
		ArrayList<String> images = FileUtils.getAllImages(new File(databasePath), true);

		if(!(new File(indexPath)).mkdirs()){
			//throw new Exception();
		}
		
		File arq = new File(indexPath + "/indexBic.txt");
    	FileWriter fw = new FileWriter(arq);
   
    	System.out.println("Starting indexing process...");
    	
    	if (images.size()>0) {
    		for (Iterator<String> it = images.iterator(); it.hasNext(); ) {
                String imageFilePath = it.next();
                
                System.out.println("Indexing " + imageFilePath);
                
	            double[] d = getVector(ImageIO.read(new FileInputStream(imageFilePath)));
	            String feature = "";
	            for(int i=0;i<d.length;i++){
	            	feature = feature + String.valueOf(d[i]) + " ";
	            }
	            fw.append(NameFinder.findName(imageFilePath) + " " + feature + "\n");
    		}
    	}
    	
    	fw.close();
    	System.out.println("Finished indexing...");
	}
	
}
