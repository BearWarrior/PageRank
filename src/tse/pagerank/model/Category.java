package tse.pagerank.model;
import java.util.HashSet;
import java.util.Set;

public class Category extends Article
{
	public Set<Integer> listParents;
	public Set<Integer> listChilds;
	public int idCategory;
	
	public Category(long id, String nom,int idCat)
	{
		super(id, nom);
		this.idCategory = idCat;
		this.listParents = new HashSet<>();
		this.listChilds = new HashSet<>();
	} 
	
	public void addChild(int id)
	{
		listChilds.add(id);
	}
	
	public void addParent(int id)
	{
		listParents.add(id);
	}
	
	public Set<Integer> getListChilds()
	{
		return listChilds;
	}
}
