package loadnew;

import java.util.List;

import loadmodelnew.LoadOpinionNew;

public interface CourtListenerCallback {

	void callBack(List<LoadOpinionNew> clOps);
	void shutdown();

}