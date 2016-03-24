import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StableMatchEmployees {

	public static void main(String[] args) throws Exception {

		FileReader fileReader = new FileReader(args.length == 0 ? "file.txt" : args[0]);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line = null;
        int currentState = 0; // 0 -> Group 1, 1 -> Group 2, 2 -> Group 1 prefs, 3 -> group 2 prefs
        
        List<String> group1 = new ArrayList<>();
        List<String> group2 = new ArrayList<>();
        
        List<Integer> group1Num = new ArrayList<>();
        List<Integer> group2Num = new ArrayList<>();
		
        List<List<Integer>> group1Prefs = new ArrayList<>();
		List<List<Integer>> group2Prefs = new ArrayList<>();
		
		int currentIndex = 0;
		boolean justSawEmpty = true;
		int lineNo = 0;
        
        while((line = bufferedReader.readLine()) != null && currentState < 4) {
        	lineNo++;
        	line = line.trim();
        	
        	if (line.isEmpty()) {
        		if (justSawEmpty) {
        			continue;
        		}
        		
        		justSawEmpty = true;
        		
    			if (currentState == 0 || currentState == 1) {
    				List<Integer> currentNum = currentState == 0 ? group1Num : group2Num;
    				currentNum.add(currentIndex);
    			}
    			
    			currentState++;
    			currentIndex = 0;
    			continue;
    		} else {
    			justSawEmpty = false;
    		}
        	
        	if (currentState == 0 || currentState == 1) {
        		
        		List<String> current = currentState == 0 ? group1 : group2;
        		List<Integer> currentNum = currentState == 0 ? group1Num : group2Num;
        		
        		String[] parts = line.split(":");
        		String v = parts[0].trim();
        		currentNum.add(currentIndex);
        		
        		if (parts.length == 1) {
        			current.add(v);
        			currentIndex++;
        		} else {
        			int num = Integer.parseInt(parts[parts.length - 1].trim());
        			if (num <= 0) {
        				throw new Exception("Need a positive value on line " + lineNo);
        			}
        			for (int i = 1; i <= num; i++) {
        				current.add(v);
        				currentIndex++;
        			}
        		}
        	}
        	
        	if (currentState == 2 || currentState == 3) {
        		List<List<Integer>> current = currentState == 2 ? group1Prefs : group2Prefs;
        		List<Integer> currentCount = currentState == 2 ? group1Num : group2Num;
        		List<Integer> otherCount = currentState == 2 ? group2Num : group1Num;
        		List<String> otherGroup = currentState == 2 ? group2 : group1;
        		
        		List<Integer> prefs = new ArrayList<Integer>(otherGroup.size());
        		for (int i = 0; i < otherGroup.size(); i++) {
        			prefs.add(0);
        		}
        		
        		String[] parts = line.split(" ");
        		
        		int currentCounter = 0;
        		for (int i = 0; i < parts.length; i++) {
        			int otherIndex = Integer.parseInt(parts[i].trim()) - 1;
        			if (otherIndex < 0 || otherIndex >= otherCount.size()) {
        				throw new Exception("Invalid id " + (otherIndex + 1) + " on line " + lineNo);
        			}
        			int from = otherCount.get(otherIndex);
        			int to = otherCount.get(otherIndex + 1);
        			for (int j = from; j < to; j++) {
        				prefs.set(j, currentCounter);
        				currentCounter++;
        			}
        		}
        		
        		int from = currentCount.get(currentIndex);
    			int to = currentCount.get(currentIndex + 1);
    			
    			for (int j = from; j < to; j++) {
    				current.add(prefs);
    			}
    			
    			currentIndex++;
        	}
        	
        }   

        bufferedReader.close(); 
		
        
        stableMatch(group1, group2, group1Prefs, group2Prefs);
	}

	public static void stableMatch(List<String> group1, List<String> group2,
			List<List<Integer>> group1Prefs, List<List<Integer>> group2Prefs) {

		if (group1.size() == group2.size()) {
			stableMatchExact(group1, group2, group1Prefs, group2Prefs);
			return;
		}

		List<String> lowerName = group1.size() < group2.size() ? group1
				: group2;
		List<String> higherName = group1.size() > group2.size() ? group1
				: group2;
		List<List<Integer>> lowerPrefs = group1.size() < group2.size() ? group1Prefs
				: group2Prefs;
		List<List<Integer>> higherPrefs = group1.size() > group2.size() ? group1Prefs
				: group2Prefs;

		int diff = Math.abs(group1.size() - group2.size());

		List<Integer> nobodyPrefs = new ArrayList<Integer>();
		for (int j = 0; j < higherName.size(); j++) {
			nobodyPrefs.add(j);
		}

		for (int i = 0; i < diff; i++) {
			lowerName.add("Nobody");
			List<Integer> p = new ArrayList<>(nobodyPrefs);
			Collections.shuffle(p);
			lowerPrefs.add(p);

			for (int j = 0; j < higherPrefs.size(); j++) {
				higherPrefs.get(j).add(lowerName.size() - 1);
			}
		}
		
		stableMatchExact(group1, group2, group1Prefs, group2Prefs);
	}

	public static void stableMatchExact(List<String> group1,
			List<String> group2, List<List<Integer>> group1Prefs,
			List<List<Integer>> group2Prefs) {

		LinkedList<Integer> g1 = new LinkedList<Integer>();
		HashMap<Integer, Integer> match = new HashMap<>();

		for (int i = 0; i < group1.size(); i++) {
			g1.add(i);
		}

		while (!g1.isEmpty()) {
			int current = g1.poll();
			List<Integer> prefs = group1Prefs.get(current);

			for (int matchNo : prefs) {
				if (match.containsKey(matchNo)) {
					int otherMatch = match.get(matchNo);
					if (group2Prefs.get(matchNo).get(current) < group2Prefs
							.get(matchNo).get(otherMatch)) {
						match.put(matchNo, current);
						g1.add(otherMatch);
						break;
					}
				} else {
					match.put(matchNo, current);
					break;
				}

			}
		}

		for (Map.Entry<Integer, Integer> m : match.entrySet()) {
			System.out.println(group2.get(m.getKey()) + " with "
					+ group1.get(m.getValue()));
		}
	}
}
