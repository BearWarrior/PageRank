package tse.pagerank.model;

import java.util.HashSet;
import java.util.Set;

import tse.pagerank.ordonnanceur.GrandManitou;

public class Page extends Article implements Comparable<Page>
{
	Set<Integer> listLink;

	protected double pagerank;

	public Page(long id, String nom, double p_pagerank) 
	{
		super(id, nom);
		listLink = new HashSet<Integer>();
		this.pagerank = p_pagerank;
	}

	public void addLink(int id_dest)
	{
		listLink.add(id_dest);
	}

	public Set<Integer> getListLink()
	{
		return listLink;
	}

	public void displayLinks()
	{
		for(Integer i : this.listLink)
		{
			System.out.println(GrandManitou.hashPage.get(i));
		}
	}

	@Override
	public String toString()
	{
		return "id: " + this.id + "		nom: " + this.nom + "		pageRan: " + Math.log10(pagerank);
	}

	public void setPagerank(double p_pagerank)
	{
		pagerank = p_pagerank;
	}

	public double getPageRanks()
	{
		return pagerank;
	}

	@Override
	public int compareTo(Page arg0) 
	{
		if(this.pagerank > arg0.pagerank)
		{
			return 1;
		}
		else if(this.pagerank < arg0.pagerank)
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}
}
