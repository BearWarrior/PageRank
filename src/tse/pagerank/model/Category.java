package tse.pagerank.model;
import java.util.ArrayList;
import java.util.List;

public class Category extends Article
{

	public List<Category> listParents;
	public List<Article> listFilles;
	
	public Category(long id, String nom)
	{
		super(id, nom);
		this.listParents = new ArrayList<>();
		this.listFilles = new ArrayList<>();
		
	} 
}
