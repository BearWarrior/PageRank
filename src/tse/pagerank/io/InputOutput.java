package tse.pagerank.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

public class InputOutput 
{


	public static void readSGZ(File file)
	{

		final String prefixInsert = "INSERT INTO `category` VALUES ";
		final String suffixInsert = ";";

		try 
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), "UTF-8"));

			String line = "";
			long tpsDebut = System.currentTimeMillis();
			while((line = in.readLine()) != null)
			{
				if(line.startsWith(prefixInsert) && line.endsWith(suffixInsert))
				{
					String valuesText = line.substring(prefixInsert.length(), line.length() - suffixInsert.length()); //(...),(...),(...)
					getTuples(line);
				}
			}

			in.close();
			System.out.println(System.currentTimeMillis() - tpsDebut);

		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			// Known error which does not interfer with the reading Unexpected end of ZLIB input stream
			// http://bugs.java.com/bugdatabase/view_bug.do;jsessionid=53ede10dc8803210b03577eac43?bug_id=6519463 
			//e.printStackTrace();
		}	
	}

	public static LinkedList<String> getTuples(String line)
	{
		LinkedList<String> tuples = new LinkedList<String>();

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
				findStart = true;
				
				tuples.add(line.substring(indexD,  indexF));

				//System.out.println(line.substring(indexD+1,  indexF));
			}
		}

		return tuples;
	}
	
	/*public static LinkedList<LinkedList<String>> getColumns(LinkedList<String> tuples)
	{
		
	}*/
}