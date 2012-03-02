package httpInterfaceTest;

/**
 * Class that will store our response and will be transformed into
 * a JSON object
 * @author ricard
 *
 */
public class Product{
	public String name;
	public int id;
	public int price;
	
	public Product(){
	}
	
	public Product(String name, int id, int price){
		this.name = name;
		this.id = id;
		this.price = price;
	}
}