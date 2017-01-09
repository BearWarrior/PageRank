package tse.pagerank.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

import tse.pagerank.model.Category;
import tse.pagerank.ordonnanceur.GrandManitou;

public class InputOutput 
{
	public static void readCategories(File file, int nbColumn, String type)
	{
		final String prefixInsertCat = "INSERT INTO `category` VALUES ";
		final String suffixInsert = ";";

		final String prefixInsertLink = "INSERT INTO `categorylinks` VALUES ";
		
		try 
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), "UTF-8"));

			String line = "";
			
			while((line = in.readLine()) != null)
			{
				if(type == "category")
				{
					if(line.startsWith(prefixInsertCat) && line.endsWith(suffixInsert))
					{
						getTuples(line, nbColumn, type);
					}
				}
				else
				{
					if(line.startsWith(prefixInsertLink) && line.endsWith(suffixInsert))
					{
						getTuples(line, nbColumn, type);
					}
				}
			}

			in.close();

		} catch (IOException e) 
		{
			// Known error which does not interfer with the reading Unexpected end of ZLIB input stream
			// http://bugs.java.com/bugdatabase/view_bug.do;jsessionid=53ede10dc8803210b03577eac43?bug_id=6519463 
			//e.printStackTrace();
		}	
	}

	public static void getTuples(String line, int nbColumn, String type)
	{
		int indexD = 0;
		int indexF = 0;
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
					indexF = line.indexOf(");", indexF + 1);
					
				String temp = line.substring(indexD,  indexF);
				String[] array = temp.split(",");
				
				if(array.length >= nbColumn+1)
				{
					findStart = true;

					
					switch(type)
					{
					case "category":
						getCategories(line.substring(indexD +1,  indexF));
						break;
					case "link":
						System.out.println(line.substring(indexD +1,  indexF));
						getLinks(line.substring(indexD +1,  indexF));
						break;
					}

					indexD = indexF -1;
				}
			}
		}
	}

	private static void getCategories(String tuple)
	{
		String[] array = tuple.split(",");
		Category cat = new Category(Integer.parseInt(array[0]), array[1]);

		GrandManitou.hashCat.put(Integer.parseInt(array[0]), cat);	
		GrandManitou.hashNomIdCat.put(array[1], Integer.parseInt(array[0]));
		
		GrandManitou.cptCat++;
	}
	
	private static void getLinks(String tuple)
	{
		//get ID (0) and parent category (1)
		String[] array = tuple.split(",");
		GrandManitou.addChild(Integer.parseInt(array[0]), array[1]);
		GrandManitou.cptLink++;
	}
}


/*

public static void getTuples(String line, int nbColumn, String type)
{
	int indexD = 0;
	int indexF = 0;
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
			indexF = line.indexOf(')', indexF + 1);

			String temp = line.substring(indexD,  indexF);
			String[] array = temp.split(",");
			if(array.length >= nbColumn+1)
			{
				findStart = true;

				
				switch(type)
				{
				case "category":
					getCategories(line.substring(indexD +1,  indexF));
					break;
				case "link":
					System.out.println(line.substring(indexD +1,  indexF));
					getLinks(line.substring(indexD +1,  indexF));
					break;
				}

				indexD = indexF -1;
			}
		}
	}
}


*/