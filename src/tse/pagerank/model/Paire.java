package tse.pagerank.model;

import java.util.ArrayList;
import java.util.Arrays;

public class Paire implements Comparable<Paire>
{
	int x;
	int y;
	public Paire(int p_x, int p_y) 
	{
		this.x = p_x;
		this.y = p_y;
	}
	
	/*
	 * dest
	 * nbSource
	 * src1
	 * ...
	 * srcn
	 * dest
	 * nbSource
	 */
	public static int[] fromPaireToPageRankTab(Paire[] paires)
	{
		ArrayList<Integer> listLinks = new ArrayList<Integer>();
		//System.out.println(paires.length);
		for (int i = 0; i < paires.length-1;) 
		{
			int dest = paires[i].x;
			listLinks.add(dest);
			int nbSrc = 0;
			int j = i + 1;
			//System.out.println(j + "  " + paires.length);
			while(paires[j].x == dest && j < paires.length-1) //While this is the same destination
			{
				j++;
			}
			//We have an interval where the destination is the same
			nbSrc = j - i;
			int borne = nbSrc + i;
			listLinks.add(nbSrc);
			for(; i < borne; i++)
			{
				listLinks.add(paires[i].y);
			}
		}
		
		int[] ret = new int[listLinks.size()];
		for (int i = 0; i < ret.length-1 ; i++)
			ret[i] = listLinks.get(i);
		return ret;
	}
	
	@Override
	public int compareTo(Paire arg0) 
	{
		if(this.x > arg0.x)
			return 1;
		else if(this.x < arg0.x)
			return -1;
		else
			return 0;	
	}
}
