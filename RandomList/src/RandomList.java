import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by devansh on 11/4/15.
 * 
 * When you can't modify the original list and you want to draw random elements out of it.
 * If you can modify original list just swap the randomly chosen element with the last element.
 * 
 * So if you can't modify the original list, if you copy over to the new list it will be O(n)
 * This implementation will be ~O(n/3)
 */
public class RandomList<E> {

	private static class Range {
		int start, end;

		public Range(int s, int e) {
			start = s;
			end = e;
		}
	}

	private List<E> list;
	private int size = 0;
	private LinkedList<Range> ranges = new LinkedList<>();

	public RandomList(List<E> l) {
		list = l;
		size = l.size();
		ranges.add(new Range(0, size));
	}

	public E nextRandom() {
		int next = (int) (Math.random() * size);
		E res = null;
		int total = 0;

		ListIterator<Range> it = ranges.listIterator(0);
		while (it.hasNext()) {
			Range r = it.next();
			int rangeSize = r.end - r.start;
			if (total + rangeSize > next) {
				int index = r.start + next - total;
				res = list.get(index);
				if (index == r.start) {
					r.start = index + 1;
				} else if (index == r.end - 1) {
					r.end = index;
				} else {
					Range newRange = new Range(index + 1, r.end);
					it.add(newRange);
					r.end = index;
				}
				break;
			}
			total += rangeSize;
		}

		size--;
		return res;
	}

	public static void main(String[] args) throws Exception {

		int size = 10000;

		ArrayList<Integer> list = new ArrayList<>();
		HashSet<Integer> set = new HashSet<>();
		ArrayList<Integer> randomized = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			list.add(i);
			set.add(i);
		}

		RandomList<Integer> randomList = new RandomList<>(list);

		for (int i = 0; i < size; i++) {
			Integer r = randomList.nextRandom();
			if (set.contains(r)) {
				set.remove(r);
			} else {
				throw new Exception("Doesn't work :(");
			}
			randomized.add(r);
		}

		System.out.println(set.size() + " ::: " + randomized.size() + " ::: " + randomList.ranges.size());
		
		long currentRL = System.currentTimeMillis();
		randomList = new RandomList<>(list);
		for (int i = 0; i < size; i++) {
			int r = randomList.nextRandom();
		}
		long endRL = System.currentTimeMillis();
		
		long currentSwap = System.currentTimeMillis();
		int swapSize = size;
		ArrayList<Integer> swapList = new ArrayList<>(list);
		for (int i = 0; i < size; i++) {
			int next = (int) (Math.random() * swapSize);
			int r = swapList.get(next);
			int last = swapList.get(swapSize - 1);
			
			swapList.add(swapSize - 1, r);
			swapList.add(next, last);
			swapSize--;
		}
		long endSwap = System.currentTimeMillis();
		
		
		System.out.println((endRL - currentRL) + " ::: " + (endSwap - currentSwap));
	}
}
