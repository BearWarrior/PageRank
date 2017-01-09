package tse.pagerank.main;

import java.io.File;

import tse.pagerank.io.InputOutput;
import tse.pagerank.ordonnanceur.GrandManitou;

public class Main {
	public static void main(String[] args) 
	{
		GrandManitou gm = new GrandManitou();

		//File file = new File("enwiki-20161020-category.sql.gz");
		File fileCat = new File("simplewiki-20161020-category.sql.gz");
		File fileCatLinks = new File("simplewiki-20161020-categorylinks.sql.gz");


		long tpsDebut = System.currentTimeMillis();


		InputOutput.readCategories(fileCat, 4, "category");

		System.out.println(System.currentTimeMillis() - tpsDebut);

		InputOutput.readCategories(fileCatLinks, 6, "link");

		System.out.println(System.currentTimeMillis() - tpsDebut);


		System.out.println("______________________________");
		/*for(int i = 0; i < GrandManitou.hashCat.get(7933).getListChilds().size(); i++)
		{
			if( GrandManitou.hashCat.get(GrandManitou.hashCat.get(7933).getListChilds().get(i)) != null )
				GrandManitou.hashCat.get(GrandManitou.hashCat.get(7933).getListChilds().get(i)).getNom();
		}*/

System.out.println(GrandManitou.cptCat + "    " + GrandManitou.cptLink);

		//GrandManitou.displayChildOfCat(7933);

	}
}
