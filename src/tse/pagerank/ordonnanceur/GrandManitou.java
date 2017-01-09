package tse.pagerank.ordonnanceur;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tse.pagerank.model.Category;

public class GrandManitou 
{
	public static int cptCat = 0;
	public static int cptLink = 0;
	
	public static HashMap<Integer, Category> hashCat;
	public static Map<String, Integer> hashNomIdCat;

	public GrandManitou() 
	{
		if(hashCat == null)
			this.hashCat = new HashMap<Integer, Category>();
		if(hashNomIdCat == null)
			this.hashNomIdCat = new HashMap<String, Integer>();
	}

	public static void addChild(int idChild, String parentCategory)
	{
		int idParent = hashNomIdCat.get(parentCategory); //get category id
		hashCat.get(idParent).addChild(idChild); // ajout de l'article dans la liste des enfant de la catégory

		Category cat;
		if( (cat = hashCat.get(idChild)) != null) //the child is also a category
		{
			cat.addParent(idParent); // ajout de la category parent dans la liste des parents de la category
		}
	}

	public static void displayChildOfCat(int id)
	{
		Category cat = hashCat.get(id); 

		List<Integer> listChild = cat.getListChilds();
		System.out.println(listChild.size());
		for(Integer i : listChild)
		{
			if(hashCat.get(i) != null)
			{
				System.out.println(hashCat.get(i).getNom());
			}
		}
	}
}
