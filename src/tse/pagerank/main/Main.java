package tse.pagerank.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tse.pagerank.io.InputOutput;
import tse.pagerank.model.Page;
import tse.pagerank.model.Paire;
import tse.pagerank.nayuki.Pagerank;
import tse.pagerank.ordonnanceur.GrandManitou;

public class Main 
{
	final static int ID_CAT_SWITZERLAND = 7933;
	final static int ID_ART_SWITZERLAND = 10801;
	final static int ID_CAT_SOUTHKOREA = 4;
	final static int ID_ART_SOUTHKOREA = 19491;
	final static int ID_CAT_CITIESSOUTHKOREA = 4622;
	final static int ID_ART_CITIESSOUTHKOREA = 11062;
	
	private static final File PAGERANKS_RAW_FILE = new File("wikipedia-pageranks.raw");  // Output file

	public static void main(String[] args) 
	{
		GrandManitou gm = new GrandManitou();

		//EN
		//File fileCat = new File("enwiki-20161020-category.sql.gz");
		//File fileCatLinks = new File("enwiki-20161020-categorylinks.sql.gz");

		//SIMPLE
		File fileCat = new File("simplewiki-20161020-category.sql.gz");
		File fileCatLinks = new File("simplewiki-20161020-categorylinks.sql.gz");
		File filePage = new File("simplewiki-20161020-page.sql.gz");
		File filePageLinks = new File("simplewiki-20161020-pagelinks.sql.gz");

		long tpsDebut = System.currentTimeMillis();

		//Article (Page & categories)
		InputOutput.readCategories(filePage, 11, "page");
		System.out.print("traitement Page : " + (System.currentTimeMillis() - tpsDebut) + "ms 		");
		System.out.println("Nombre de pages: " + GrandManitou.hashPage.size() + "		Nombre de catégorie: " + GrandManitou.hashCat.size());
		tpsDebut = System.currentTimeMillis();
		//Categories - check if categories from previous process are saved and we update their idCategory
		//Remmarque : sur simple wiki --> toutes les catégories répertiriés dans page.sql sont présentes dans catégories.sql
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

		//SORT
		Paire[] intPair = new Paire[GrandManitou.tabDestSrc.size()];
		for (int i = 0; i < intPair.length; i++) 
		{
			intPair[i] = GrandManitou.tabDestSrc.get(i);
		}
		Arrays.sort(intPair);
		//Mise en forme pour le page rank
		int[] links = Paire.fromPaireToPageRankTab(intPair);
		/*for (int i = 0; i < 500; i++) 
			System.out.println(links[i]);*/




		// Iteratively compute PageRank
		final double DAMPING = 0.85;  // Between 0.0 and 1.0; standard value is 0.85
		System.out.println("Computing PageRank...");
		Pagerank pr = new Pagerank(links);
		double[] prevPageranks = pr.pageranks.clone();
		for (int i = 0; i < 10; i++)
		{
			// Do iteration
			System.out.print("Iteration " + i);
			long startTime = System.currentTimeMillis();
			pr.iterateOnce(DAMPING);
			System.out.printf(" (%.3f s)%n", (System.currentTimeMillis() - startTime) / 1000.0);

			// Calculate and print statistics
			double[] pageranks = pr.pageranks;
			//printPagerankChangeRatios(prevPageranks, pageranks);
			//printTopPages(pageranks, GrandManitou.hashPage);
			prevPageranks = pageranks.clone();
		}
		
		//assignation des pageranks aux pages :
		pr.setPageRanksToPages();
		
		
		Page p = GrandManitou.getMaxPageRankOfCat(ID_ART_SWITZERLAND);
		System.out.println(p);
		
		System.out.println("________________________________________________");
		
		Set<Page> pageFromPreviousCat = GrandManitou.getAllPagesFromCat(ID_ART_SWITZERLAND);
		List<Page> sortedPages = new ArrayList( pageFromPreviousCat);
		System.out.println("avant : " +sortedPages.size());
		Collections.sort(sortedPages);
		Collections.reverse(sortedPages);
		System.out.println("apres : " + sortedPages.size());
		System.out.println("base : " + pageFromPreviousCat.size());
		
		
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("AllPageFromSwitzrrrrlandddd.txt")));
			for(Page page : sortedPages)
			{
				//System.out.println(page);
				bw.write(page.toString() + "\n");
			}
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		/*

		// Write PageRanks to file
		DataOutputStream out;
		try {
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(PAGERANKS_RAW_FILE)));
			
			try {
				for (double x : pr.pageranks)
					out.writeDouble(x);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//GrandManitou.displayChildOfCat(GrandManitou.hashCatToArt.get(4622));
		//GrandManitou.displayChildOfCat(ID_ART_SOUTHKOREA);

*/
	}
	
	private static void printPagerankChangeRatios(double[] prevPr, double[] pr) {
		double min = Double.POSITIVE_INFINITY;
		double max = 0;
		for (int i = 0; i < pr.length; i++) {
			if (pr[i] != 0 && prevPr[i] != 0) {
				double ratio = pr[i] / prevPr[i];
				min = Math.min(ratio, min);
				max = Math.max(ratio, max);
			}
		}
		System.out.println("Range of ratio of changes: " + min + " to " + max);
	}
	
	
	private static void printTopPages(double[] pageranks, Map<Integer, Page> titleById) {
		final int NUM_PAGES = 30;
		double[] sorted = pageranks.clone();
		Arrays.sort(sorted);
		for (int i = 0; i < NUM_PAGES; i++) {
			for (int j = 0; j < sorted.length; j++) {
				if (pageranks[j] == sorted[sorted.length - 1 - i]) {
					System.out.printf("  %.3f  %s%n", Math.log10(pageranks[j]), titleById.get(j));
					break;
				}
			}
		}
	}
}


/*int idPage = 156033;
System.out.println("nombre de lien depuis la page id " + idPage + ":  " + GrandManitou.hashPage.get(idPage).getListLink().size());
GrandManitou.hashPage.get(idPage).displayLinks();*/
