package model;

import java.net.URL;
import java.util.List;

public class CourtListenerClustersSave {
	private long count;
	private URL next;
	private URL previous;
	private List<CourtListenerCluster> results;
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public URL getNext() {
		return next;
	}
	public void setNext(URL next) {
		this.next = next;
	}
	public URL getPrevious() {
		return previous;
	}
	public void setPrevious(URL previous) {
		this.previous = previous;
	}
	public List<CourtListenerCluster> getResults() {
		return results;
	}
	public void setResults(List<CourtListenerCluster> results) {
		this.results = results;
	}
}
