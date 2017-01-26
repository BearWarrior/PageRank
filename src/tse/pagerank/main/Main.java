package tse.pagerank.main;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tse.pagerank.gui.SwingGui;
import tse.pagerank.io.InputOutput;
import tse.pagerank.model.Page;
import tse.pagerank.model.Paire;
import tse.pagerank.nayuki.Pagerank;
import tse.pagerank.ordonnanceur.GrandManitou;

public class Main 
{
	final static int ID_CAT_HISTORY = 5794;
	final static int ID_CAT_SWITZERLAND = 7933;
	final static int ID_ART_SWITZERLAND = 10801;
	final static int ID_CAT_SOUTHKOREA = 4;
	final static int ID_ART_SOUTHKOREA = 19491;
	final static int ID_CAT_CITIESSOUTHKOREA = 4622;
	final static int ID_ART_CITIESSOUTHKOREA = 11062;
	
	private static final File PAGERANKS_RAW_FILE = new File("wikipedia-pageranks.raw");  // Output file

	final static double EPSILON = 0.0000000001;
	
	public static void main(String[] args) 
	{
		//DO not remove, it initialize the object
		GrandManitou gm = new GrandManitou();

		//SIMPLE
		File fileCat = new File("simplewiki-20161020-category.sql.gz");
		File fileCatLinks = new File("simplewiki-20161020-categorylinks.sql.gz");
		File filePage = new File("simplewiki-20161020-page.sql.gz");
		File filePageLinks = new File("simplewiki-20161020-pagelinks.sql.gz");

		long tpsDebut = System.currentTimeMillis();

		//_________________________________________FILE PROCESS_____________________________________________
		//Article (Page & categories)
		InputOutput.readCategories(filePage, 11, "page");
		System.out.print("traitement Page : " + (System.currentTimeMillis() - tpsDebut) + "ms 		");
		System.out.println("Nombre de pages: " + GrandManitou.hashPage.size() + "		Nombre de catégorie: " + GrandManitou.hashCat.size());
		tpsDebut = System.currentTimeMillis();
		//Categories - check if categories from previous process are saved and we update their idCategory
		//Remark : sur simple wiki --> toutes les catégories répertiriés dans page.sql sont présentes dans catégories.sql
		InputOutput.readCategories(fileCat, 4, "category");
		System.out.print("traitement Catégorie : " + (System.currentTimeMillis() - tpsDebut) + "ms		");
		System.out.println("Nombre de mise a jour : " +  GrandManitou.hashCat.size()  + "   Nombre de catégories ignorées : " + GrandManitou.cptMismatch_inCategoryNotInPage);
		tpsDebut = System.currentTimeMillis(); 
		//Link between categories
		InputOutput.readCategories(fileCatLinks, 6, "link");
		System.out.print("traitement Catégorie : " + (System.currentTimeMillis() - tpsDebut) + "ms		");
		System.out.println("Nombre de catégories links : " + GrandManitou.cptLink);
		tpsDebut = System.currentTimeMillis();
		//Link between pages
		InputOutput.readCategories(filePageLinks, 3, "pageLinks");
		System.out.print("traitement pageLinks : " + (System.currentTimeMillis() - tpsDebut) + "ms		");
		System.out.println("Nombre de page links : " + GrandManitou.nbpPageLinks + "  Nombre de nom incohérent dans les page links : " + GrandManitou.cptLinkPageNameError);
		tpsDebut = System.currentTimeMillis();

		System.out.println("______________________________");

		
		System.out.println(GrandManitou.hashNomIdCat.get("'Wars'"));
		
		GrandManitou.displayChildOfCat(6810);
		
		//________________________________PROCESS BEFORE CALLING PAGERANK____________________________________
		//Sort 
		Paire[] intPair = new Paire[GrandManitou.tabDestSrc.size()];
		for (int i = 0; i < intPair.length; i++) 
			intPair[i] = GrandManitou.tabDestSrc.get(i);
		Arrays.sort(intPair);
		//Format data for the pagerank
		int[] links = Paire.fromPaireToPageRankTab(intPair);


		//__________________________________________PAGE RANK_________________________________________________
		// Iteratively compute PageRank
		final double DAMPING = 0.85;  // Between 0.0 and 1.0; standard value is 0.85
		System.out.println("Computing PageRank...");
		Pagerank pr = new Pagerank(links);
		double[] prevPageranks = pr.pageranks.clone();
		double[] changeRatio = new double[2];
		
		int iteration = 0;
		do
		{
			pr.iterateOnce(DAMPING);
			if(iteration%10 == 0)
			{
				long startTime = System.currentTimeMillis();
				System.out.print("Iteration " + iteration);
				System.out.printf(" (%.3f s)%n", (System.currentTimeMillis() - startTime) / 1000.0);
			}

			// Calculate (and print) statistics
			double[] pageranks = pr.pageranks;
			changeRatio = printPagerankChangeRatios(prevPageranks, pageranks);
			prevPageranks = pageranks.clone();
			iteration++;
		}
		while(changeRatio[0] < (1 - EPSILON) && changeRatio[1] > (1 + EPSILON));
		
		//______________________________ASSIGNING PAGE RANK TO OUR PAGES________________________________________
		
		pr.setPageRanksToPages();
		System.out.println("______________________________");
		
		
		SwingGui.launchGUI();
		
		
		//______________________________PAGE RANK UTILISATION________________________________________
		/*Set<Page> pageFromPreviousCatSTR = GrandManitou.getAllPagesFromCat("'War'");
		List<Page> sortedPagesSTR = GrandManitou.sortPages(pageFromPreviousCatSTR);
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("War.txt")));
			for(Page page : sortedPagesSTR)
			{
				System.out.println(page);
				bw.write(page.toString() + "\n");
			}
			bw.flush();
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	} //End of the main
	
	
	
	
	private static double[] printPagerankChangeRatios(double[] prevPr, double[] pr) 
	{
		double[] out = new double[2];
		double min = Double.POSITIVE_INFINITY;
		double max = 0;
		for (int i = 0; i < pr.length; i++) 
		{
			if (pr[i] != 0 && prevPr[i] != 0) 
			{
				double ratio = pr[i] / prevPr[i];
				min = Math.min(ratio, min);
				max = Math.max(ratio, max);
			}
		}
		out[0] = min;
		out[1] = max;
		//System.out.println("Range of ratio of changes: " + min + " to " + max);
		return out;
	}
	
	private static void printTopPages(double[] pageranks, Map<Integer, Page> titleById) 
	{
		final int NUM_PAGES = 30;
		double[] sorted = pageranks.clone();
		Arrays.sort(sorted);
		for (int i = 0; i < NUM_PAGES; i++) 
		{
			for (int j = 0; j < sorted.length; j++) 
			{
				if (pageranks[j] == sorted[sorted.length - 1 - i]) 
				{
					System.out.printf("  %.3f  %s%n", Math.log10(pageranks[j]), titleById.get(j));
					break;
				}
			}
		}
	}
}