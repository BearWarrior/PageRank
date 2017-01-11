package tse.pagerank.ordonnanceur;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tse.pagerank.model.Category;
import tse.pagerank.model.Page;

public class GrandManitou 
{
	public static int cptCat = 0;
	public static int cptLink = 0;
	public static int cptPage = 0;

	public static HashMap<Integer, Category> hashCat;
	public static Map<String, Integer> hashNomIdCat;
	public static Map<Integer, Page> hashPage;
	
	public static Map<Integer, Integer> hashCatToArt;

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

		List<Integer> listChild = cat.getListChilds();
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
}
