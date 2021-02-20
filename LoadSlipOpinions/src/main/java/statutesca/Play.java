package statutesca;

import java.util.*;
import java.util.stream.Collectors;

public class Play {

	  public static void main(String[] args)
	  {
	        List<String> inputs = Arrays.asList("D8", "S1", "S5", "D2", "D15", "S9");
	        System.out.println(inputs);

	        Map<String, List<Integer>>
	        outputs = inputs.stream()
	              .collect(
            		  Collectors.groupingBy(s -> s.startsWith("D") ? "desperate" : "serious", 
//                              Collectors.mapping(n -> Integer.valueOf(n.replaceAll("[DS]", "")), Collectors.toList())
            				  Collectors.mapping(n -> Integer.valueOf(n.replaceAll("[DS]", "")), Collectors.toList()))
            		  );

	        System.out.println(outputs);
	  }
}
