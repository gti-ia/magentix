package es.upv.dsic.gti_ia.argAgents;

import java.util.Date;


/**
 * This class contains metrics that are used in the CBR algorithm
 * to measure the similarity between case attributes, base on their distance. 
 */
public class Metrics {
	
	/**
	 * This method decide about which is the data type of the 
	 * attributes that are contained in the Strings that will be
	 * compared, and return the distance between them.
	 * 
	 * @param a an String that contains an attribute.
	 * @param b an String that contains an attribute.
	 */
	
	public static float doDist(String a, String b){
		
		// try to convert the String to an integer, long or float
		try{
			int ia = Integer.decode(a).intValue();
			int ib = Integer.decode(b).intValue();
			return dist(ia,ib);
		}catch(NumberFormatException e){
			
		}
		try{
			long la = Long.decode(a).longValue();
			long lb = Long.decode(b).longValue();
			return dist(la,lb);
		}catch(NumberFormatException e){
			
		}
		try{
			float fa = Float.parseFloat(a);
			float fb = Float.parseFloat(b);
			return dist(fa,fb);
		}catch(NumberFormatException e){
			
		}
		
		return dist(a,b);
		
	}
	
	/**
	 * This method calculates the distance between two floats.
	 * @param a a float data
 	 * @param b a float data
	 * @return the distance between two floats.
	 */
	
	public static float dist(float a, float b) {
		return Math.abs(a - b);
	}
	
	/**
	 * This method calculates the distance between two integers.
	 * @param a an integer data
	 * @param b an integer data
	 * @return the distance between two integers.
	 */
	
	public static int dist(int a, int b) {
		return Math.abs(a - b);
	}
	
	/**
	 * This method calculates the distance between two Date Objects.
	 * @param a a Date Object
	 * @param b a Date Object
	 * @return the distance between two Date Objects.
	 */
	
	public static int dist(Date a,Date b){
		return (int)(a.getTime() - b.getTime());
	}
	
	/**
	 * This method calculates the edition distance between two Strings.
	 * @param sp a String Object.
	 * @param tp a String Object.
	 * @return the edition distance between two Strings.
	 */
	
	public static int dist(String sp, String tp) {
		// Levenshtein distance (http://www.merriampark.com/ld.htm, http://www.merriampark.com/ldjava.htm)
		//all String to upper case
		String s = sp.toUpperCase();
		String t = tp.toUpperCase();
		
		int n = s.length(); // length of s
		int m = t.length(); // length of t
		
		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}
		
		int p[] = new int[n + 1]; //'previous' cost array, horizontally
		int d[] = new int[n + 1]; // cost array, horizontally
		int _d[]; //placeholder to assist in swapping p and d
		
		// indexes into strings s and t
		int i; // iterates through s
		int j; // iterates through t
		
		char t_j; // jth character of t
		
		int cost; // cost
		
		for (i = 0; i <= n; i++) {
			p[i] = i;
		}
		
		for (j = 1; j <= m; j++) {
			t_j = t.charAt(j - 1);
			d[0] = j;
			
			for (i = 1; i <= n; i++) {
				cost = s.charAt(i - 1) == t_j ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally
				// left and up +cost
				d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1]
						+ cost);
			}
			
			// copy current distance counts to 'previous row' distance
			// counts
			_d = p;
			p = d;
			d = _d;
		}
		
		// our last action in the above loop was to switch d and p, so p now
		// actually has the most recent cost counts
		return p[n];
	}
}
