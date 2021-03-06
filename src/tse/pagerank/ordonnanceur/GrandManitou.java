package tse.pagerank.ordonnanceur;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tse.pagerank.model.Category;
import tse.pagerank.model.Page;
import tse.pagerank.model.Paire;
import tse.pagerank.nayuki.Pagerank;

public class GrandManitou 
{
	public static int cptCat = 0;
	public static int cptLink = 0;
	public static int cptPage = 0;
	public static int nbpPageLinks = 0;

	public static HashMap<Integer, Category> hashCat;
	public static Map<String, Integer> hashNomIdCat;
	public static Map<Integer, Page> hashPage;
	public static Map<String, Integer> hashNomIdPage; //TO get the Id of the page from its name

	//public static int[][] tabDestSrc;
	public static ArrayList<Paire> tabDestSrc;

	public static Map<Integer, Integer> hashCatToArt;

	public static int cptMismatch_inCategoryNotInPage = 0;
	public static int cptLinkPageNameError = 0;

	public GrandManitou() 
	{
		if(hashCat == null)
			this.hashCat = new HashMap<Integer, Category>();
		if(hashNomIdCat == null)
			this.hashNomIdCat = new HashMap<String, Integer>();
		if(hashPage == null)
			this.hashPage = new HashMap<Integer, Page>();
		if(hashCatToArt == null)
			this.hashCatToArt = new HashMap<Integer, Integer>();
		if(hashNomIdPage == null)
			this.hashNomIdPage = new HashMap<String, Integer>();
		if(tabDestSrc == null)
			this.tabDestSrc = new ArrayList<Paire>();
	}

	public static void addChild(int idChild, String parentCategory)
	{
		try
		{
			int idParent = hashNomIdCat.get(parentCategory); //get category id

			Category c;
			if( (c = hashCat.get(idParent)) != null )
			{
				hashCat.get(idParent).addChild(idChild); // ajout de l'article dans la liste des enfant de la catégory
			}
			else
			{
				System.out.println("parent null");
			}

			Category cat;
			if( (cat = hashCat.get(idChild)) != null) //the child is also a category
			{
				cat.addParent(idParent); // ajout de la category parent dans la liste des parents de la category
			}
		}
		catch (NullPointerException e)
		{
			//NAME DOES NOT EXIST
			//System.out.println("name doesn't exist : " + parentCategory);
		}
	}

	public static void displayChildOfCat(int id)
	{
		Category cat = hashCat.get(id); 

		Set<Integer> listChild = cat.getListChilds();
		System.out.println("nombre d'enfant : " + listChild.size());
		for(Integer i : listChild)
		{
			System.out.print("id : " + i + " ");
			if(hashCat.get(i) != null)
			{
				System.out.print("nom category : " + hashCat.get(i).getNom());
			}
			else if(hashPage.get(i) != null)
			{
				System.out.print("nom page : " + hashPage.get(i).getNom());
			}
			else 
			{
				System.out.print("NO DATA");
			}
			System.out.print("\n");
		}
	}

	public static Page getMaxPageRankOfCat(int idCat)
	{
		Page p = new Page(-1, "", 0);
		Category cat = hashCat.get(idCat);
		for(int idChild : cat.getListChilds()) //can be either a category or a page
		{
			if(hashCat.get(idChild) != null) //If it appears in hashCat, this is a category
			{
				Page newP = getMaxPageRankOfCat(idChild);
				if(newP.getPageRanks() != 0 && p.getPageRanks() != 0)
				{
					if( newP.getPageRanks() > p.getPageRanks())
					{
						p = newP;
					}
				}
				else
				{
					if(newP.getPageRanks() != 0)
					{
						p = newP;
					}
				}
			}
			else if(hashPage.get(idChild) != null) //If it appears in hashPage, this is a page
			{
				Page newP = hashPage.get(idChild);
				if(newP.getPageRanks() != 0 && p.getPageRanks() != 0)
				{
					if( newP.getPageRanks() > p.getPageRanks())
					{
						p = newP;
					}
				}
				else
				{
					if(newP.getPageRanks() != 0)
					{
						p = newP;
					}
				}
			}
		}

		return p;
	}

	/**
	 * return all the pages of a category (subcategories included)
	 * @param idCat
	 * @return
	 */
	public static Set<Page> getAllPagesFromCat(int idCat, HashMap<Integer, Boolean> idDone)
	{
		if(idDone == null)
			idDone = new HashMap<Integer, Boolean>();
		Set<Page> response = new HashSet<Page>();
		Category cat = hashCat.get(idCat);
		for(int idChild : cat.getListChilds()) //can be either a category or a page
		{
			if(hashCat.get(idChild) != null) //If it appears in hashCat, this is a category
			{
				if(idDone.get(idChild) == null)
				{
					idDone.put(idChild, true);
					response.addAll(getAllPagesFromCat(idChild, idDone));
				}
				else //Loop
				{
					//System.out.println(GrandManitou.hashCat.get(idChild).getNom() + " " + idChild + "   probleme parcours 2 fois !");
				}
			}
			else if(hashPage.get(idChild) != null) //If it appears in hashPage, this is a page
			{
				response.add(hashPage.get(idChild));
			}
		}
		return response;
	}

	public static Set<Page> getAllPagesFromCat(String nomCat)
	{
		int idCat = GrandManitou.hashNomIdCat.get(nomCat);
		Set<Page> response = new HashSet<Page>();
		Category cat = hashCat.get(idCat);
		for(int idChild : cat.getListChilds()) //can be either a category or a page
		{
			if(hashCat.get(idChild) != null) //If it appears in hashCat, this is a category
			{
				response.addAll(getAllPagesFromCat(idChild, null));
			}
			else if(hashPage.get(idChild) != null) //If it appears in hashPage, this is a page
			{
				response.add(hashPage.get(idChild));
			}
		}
		return response;
	}

	public static List<Page> sortPages(Set<Page> pages)
	{
		List<Page> pagesAL = new ArrayList( pages); //copy
		Collections.sort(pagesAL);
		Collections.reverse(pagesAL);
		return pagesAL;
	}

	public static void getPageRankOfCat(String cat)
	{
		long startTime = System.currentTimeMillis();
		
		ArrayList<Paire> paires = new ArrayList<Paire>();

		Set<Page> _p = getAllPagesFromCat(cat); //get All the pages from the category
		HashMap<Integer, Page> hash = new HashMap<>();
		for(Page p : _p){
			hash.put(new Integer((int) p.getId()), p);
		}
		for(Page p : _p) //Pour toutes les pages
		{
			Page pbis = new Page(p); //TODO constructeur e copy
		
			Iterator iteLink = pbis.getListLink().iterator();
					
			while(iteLink.hasNext())
			{
				int link = (int) iteLink.next();
				boolean linkOK= false;
				if(hash.get(link) != null){
					linkOK = true;
					Paire paire = new Paire(link, (int)p.getId());
					paires.add(paire);
				}
				if(!linkOK) //If the link is not in our set
				{
					iteLink.remove();
				}
			}
		}

		//________________________________PROCESS BEFORE CALLING PAGERANK____________________________________
		//Sort 
		Paire[] intPair = new Paire[paires.size()];
		for (int i = 0; i < intPair.length; i++) 
			intPair[i] = paires.get(i);
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

		System.out.println("Prétraitement : " + (System.currentTimeMillis() - startTime) );
		
		int iteration = 0;
		startTime = System.currentTimeMillis();
		do
		{
			startTime = System.currentTimeMillis();
			pr.iterateOnce(DAMPING);
			if(iteration%10 == 0)
			{
				System.out.print("Iteration " + iteration);
				System.out.printf(" (%.3f s)%n", (System.currentTimeMillis() - startTime) / 1000.0);
				startTime = System.currentTimeMillis();
			}

			// Calculate (and print) statistics
			double[] pageranks = pr.pageranks;
			changeRatio = Pagerank.printPagerankChangeRatios(prevPageranks, pageranks);
			prevPageranks = pageranks.clone();
			iteration++;
		}
		while(changeRatio[0] < (1 - 0.0000000001) && changeRatio[1] > (1 + 0.0000000001));
		
		pr.setPageRanksToPages();
	}
}
