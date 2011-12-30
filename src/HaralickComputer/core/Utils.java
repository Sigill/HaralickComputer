package HaralickComputer.core;

import java.util.AbstractCollection;
import java.util.Iterator;

public class Utils {

	public static String combine(AbstractCollection<String> s, String glue) {
		if (s.isEmpty())
			return "";
		
	    Iterator<String> iter = s.iterator();
	    
	    StringBuffer buffer = new StringBuffer(iter.next());
	    
	    while (iter.hasNext()) 
	    	buffer.append(glue).append(iter.next());
	    
	    return buffer.toString();
	}
}
