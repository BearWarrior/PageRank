package tse.pagerank.model;
import java.util.ArrayList;
import java.util.List;

public class Category extends Article
{

	public List<Integer> listParents;
	public List<Integer> listChilds;
	
	public Category(long id, String nom)
	{
		super(id, nom);
		this.listParents = new ArrayList<>();
		this.listChilds = new ArrayList<>();
	} 
	
	public void addChild(int id)
	{
		listChilds.add(id);
	}
	
	public void addParent(int id)
	{
		listParents.add(id);
	}
	
	public List<Integer> getListChilds()
	{
		return listChilds;
	}
}
