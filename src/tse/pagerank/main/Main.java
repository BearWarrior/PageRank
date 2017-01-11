package tse.pagerank.main;

import java.io.File;

import tse.pagerank.io.InputOutput;
import tse.pagerank.ordonnanceur.GrandManitou;

public class Main 
{
	final static int ID_CAT_SWITZERLAND = 7933;
	final static int ID_ART_SWITZERLAND = 10801;
	final static int ID_CAT_SOUTHKOREA = 4;
	final static int ID_ART_SOUTHKOREA = 19491;
	final static int ID_CAT_CITIESSOUTHKOREA = 4622;
	final static int ID_ART_CITIESSOUTHKOREA = 11062;

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

		long tpsDebut = System.currentTimeMillis();

		//PAGE
		InputOutput.readCategories(filePage, 11, "page");
		System.out.println("	traitement Page : " + (System.currentTimeMillis() - tpsDebut) + "ms");
		tpsDebut = System.currentTimeMillis();
		//CAT
		InputOutput.readCategories(fileCat, 4, "category");
		System.out.println("	traitement Catégorie : " + (System.currentTimeMillis() - tpsDebut) + "ms");
		System.out.println("		nombre de mise a jour : " + GrandManitou.hashCatToArt.size());
		tpsDebut = System.currentTimeMillis(); 
		//LINK
		InputOutput.readCategories(fileCatLinks, 6, "link");
		System.out.println("	traitement Catégorie : " + (System.currentTimeMillis() - tpsDebut) + "ms");
		tpsDebut = System.currentTimeMillis();

		System.out.println("\nNb catégorie : " + GrandManitou.hashCatToArt.size() + "    nbCatégorieLink : " + GrandManitou.cptLink + "    nbPage: " + GrandManitou.hashPage.size());
		System.out.println("______________________________");

		//GrandManitou.displayChildOfCat(GrandManitou.hashCatToArt.get(4622));
		GrandManitou.displayChildOfCat(ID_ART_SOUTHKOREA);

	}
}
