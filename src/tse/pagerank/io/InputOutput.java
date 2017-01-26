package tse.pagerank.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import tse.pagerank.model.Category;
import tse.pagerank.model.Page;
import tse.pagerank.model.Paire;
import tse.pagerank.ordonnanceur.GrandManitou;

public class InputOutput 
{
	public static void readCategories(File file, int nbVirgule, String type)
	{
		final String prefixInsertCat = "INSERT INTO `category` VALUES ";
		final String prefixInsertCatLink = "INSERT INTO `categorylinks` VALUES ";
		final String prefixInsertPage = "INSERT INTO `page` VALUES ";
		final String prefixInsertPageLink = "INSERT INTO `pagelinks` VALUES ";
		final String suffixInsert = ";";

		int nbLigneCat = 0;
		int nbLigneCatLink = 0;
		int nbLignePage = 0;

		try 
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), "UTF-8"));

			String line = "";

			//long timeStart = System.currentTimeMillis();

			while((line = in.readLine()) != null)
			{
				if(type == "category")
				{
					if(line.startsWith(prefixInsertCat) && line.endsWith(suffixInsert))
					{
						getTuples(line, nbVirgule, type);
						nbLigneCat ++;
					}
				}
				else if(type == "link")
				{
					if(line.startsWith(prefixInsertCatLink) && line.endsWith(suffixInsert))
					{
						getTuples(line, nbVirgule, type);
						nbLigneCatLink ++;
					}
				}
				else if (type == "page")
				{
					if(line.startsWith(prefixInsertPage) && line.endsWith(suffixInsert))
					{
						getTuples(line, nbVirgule, type);
						nbLignePage ++;
					}
				}
				else if (type == "pageLinks")
				{
					if(line.startsWith(prefixInsertPageLink) && line.endsWith(suffixInsert))
					{
						getTuples(line, nbVirgule, type);
						nbLignePage ++;
					}
				}
				//if(nbLigne %1000 == 0)
				//System.out.println("type : " + type + "  cpt : " + nbLigne + "  "  + (System.currentTimeMillis() - timeStart ));
			}
			/*if(type == "category")
				System.out.println("nbLigneCat = " + nbLigneCat);
			else if(type == "link")
				System.out.println("nbLigneCatLink = " + nbLigneCatLink);
			else if (type == "page")
				System.out.println("nbLignePage = " + nbLignePage);*/

			in.close();

		} catch (IOException e) 
		{
			// Known error which does not interfer with the reading Unexpected end of ZLIB input stream
			// http://bugs.java.com/bugdatabase/view_bug.do;jsessionid=53ede10dc8803210b03577eac43?bug_id=6519463 
			//e.printStackTrace();
			System.out.println(e);
			if(type == "category")
				System.out.println("type : " + type + "  nbLigne = " + nbLigneCat);
			else
				System.out.println("type : " + type + "  nbLigne = " + nbLigneCatLink);
		}	
	}

	public static void getTuples(String line, int nbColumn, String type)
	{
		int indexD = 0;
		int indexF = 0;
		int maxEcart = 0;
		boolean findStart = true;

		while (indexD >= 0)
		{
			if(findStart)
			{
				indexD = line.indexOf('(', indexD + 1);
				findStart = false;
			}
			else
			{
				indexF = line.indexOf("),(", indexF + 1);
				if(indexF == -1) 
					indexF = line.indexOf(");", indexD);

				try{
					String temp = line.substring(indexD,  indexF);
					String[] array = temp.split(",");
					if(array.length >= nbColumn+1)
					{
						findStart = true;

						if(indexF-indexD > maxEcart)
						{
							maxEcart = indexF-indexD;
						}
						//System.out.println(line.substring(indexD +1,  indexF));
						switch(type)
						{
						case "category":
							getCategories(line.substring(indexD +1,  indexF));
							break;
						case "link":
							getLinks(line.substring(indexD +1,  indexF));
							break;
						case "page":
							getPage(line.substring(indexD +1,  indexF));
							break;
						case "pageLinks":
							getPageLinks(line.substring(indexD +1,  indexF));
							break;
						}
						indexD = indexF -1;
					}
				}
				catch(StringIndexOutOfBoundsException e)
				{
					System.out.println(e);
					System.out.println(type);
					System.out.println(line.length());
					System.out.println(line.substring(indexD,  indexD+20));
					System.out.println(indexD +" " +  indexF);

					try {
						Thread.sleep(2000000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					};
				}
			}
		}
	}

	// /!\ WARNING FOR THE FOLLOWING METHODS :
	//The name are collected badly, we keep the "'" at the beginning and at the end and the string can be cut if there is a coma
	
	private static void getCategories(String tuple)
	{
		String[] array = tuple.split(",");
		int c;
		if( GrandManitou.hashNomIdCat.get(array[1]) != null) //the name exists
		{
			c = GrandManitou.hashNomIdCat.get(array[1]); //get the id
			GrandManitou.hashCat.get(c).idCategory = Integer.parseInt(array[0]); //On affecte a une categorie pré existante un ID de catégorie (diff de l'id d'article)
			GrandManitou.hashCatToArt.put(Integer.parseInt(array[0]), c); //On fait un lien entre l'id catégorie et l'id article
		}
		else // SI un article est dans catégory.sql mais qu'il n'est pas dans page on l'oublie (~200/650000)
		{
			// ignore it
			GrandManitou.cptMismatch_inCategoryNotInPage++;
		}
	}

	private static void getLinks(String tuple)
	{
		String[] array = tuple.split(",");

		GrandManitou.addChild(Integer.parseInt(array[0]), array[1]);
		GrandManitou.cptLink++;
	}

	private static void getPage(String tuple)
	{
		String[] array = tuple.split(",");
		if(array[5] != "1") //Is not a redirect
		{
			if(Integer.parseInt(array[1]) == 14 ) //Category
			{
				Category cat = new Category(Integer.parseInt(array[0]), array[2], -1);

				if(GrandManitou.hashCat.put(Integer.parseInt(array[0]), cat) != null)
					GrandManitou.cptCat++;
				GrandManitou.hashNomIdCat.put(array[2], Integer.parseInt(array[0]));

			}
			else if(Integer.parseInt(array[1]) == 0 ) // real content article
			{
				Page page = new Page(Integer.parseInt(array[0]), array[2], 0); //not yet a pagerank
				GrandManitou.hashPage.put(Integer.parseInt(array[0]), page);
				GrandManitou.cptPage++;

				GrandManitou.hashNomIdPage.put(array[2], Integer.parseInt(array[0]));
			}
		}
	}

	// /!\ Warning, in function of the version of the dump, array[2] and array[3] swap (title and namespaceDestination)
	private static void getPageLinks(String tuple)
	{
		String[] array = tuple.split(",");

		if(Integer.parseInt(array[1]) == 0) //If its a real page 
		{
			int idSrc = Integer.parseInt(array[0]);
			String title = array[2];

			int idPageDest = 0;
			if(GrandManitou.hashNomIdPage.get(title) != null)
			{
				idPageDest = GrandManitou.hashNomIdPage.get(title);
				try
				{
					GrandManitou.hashPage.get(idSrc).addLink(idPageDest); //Add a destination to the src page
					Paire temp = new Paire(idPageDest,idSrc);
					GrandManitou.tabDestSrc.add(temp); //Add a row    idDest -> idSrc for the pageRank
					GrandManitou.nbpPageLinks++;
				} 
				catch(NullPointerException e)
				{
					// 4 errors on simple wiki : 192249 	542257 	394434  165556 
					// --> They have different namespaces in pagelinks and in page 
					//System.out.println(idSrc);
				}
			}
			else //Bad name
			{
				GrandManitou.cptLinkPageNameError++;
			}
		}
	}
}