package filtros;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTestHarness {

	public static void main(String[] args) {

		String expr = "aaaa AND bbb";
		Pattern pattern = Pattern
				.compile("((\\s*\\(*)*[a-zA-Z0-9]+((\\s*\\)\\s*)+|\\s+)(AND|OR))+\\s+[a-zA-Z0-9]+(\\s*\\)*)*");
		Matcher matcher = pattern.matcher(expr);

		if (matcher.find()) {
			System.out.println(matcher.group());
			if (matcher.end() - matcher.start() != expr.length())
				System.out.println("Sintaxis incorrecta");
			else
				System.out.println("Sintaxis correcta");
		} else
			System.out.println("Sintaxis incorrecta");
		
		int i = 0;
		int parentesis = 0;
		while(i < expr.length()){
			if(expr.charAt(i) == '(') parentesis++;
			if(expr.charAt(i) == ')') parentesis--;
			i++;
		}
		if(parentesis != 0)
			System.out.println("Error, cadena mal parentizada");
		else
			System.out.println("Cadena correctamente parentizada");
	}
}

