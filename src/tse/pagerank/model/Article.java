package tse.pagerank.model;

/**
 * Class abstraire pour definir soit une page soit une categorie
 * @author Nono
 *
 */
public abstract class Article {
	public long id;
	public String nom;
	public Article(long id, String nom) {
		super();
		this.id = id;
		this.nom = nom;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	
}
