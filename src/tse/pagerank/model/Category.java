package tse.pagerank.model;
import java.util.List;

public class Category 
{
	public long id;
	public String nom;
	public List<Category> list;
	
	public Category(long id, String nom)
	{
		this.id = id;
		this.nom = nom;
	} 
}
