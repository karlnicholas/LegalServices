package loadnew;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import loadmodelnew.LoadOpinionNew;

public class LoadCourtListenerFiles {
	private final CourtListenerCallback courtListenerCallback;
	int total = 0;

	public LoadCourtListenerFiles(CourtListenerCallback courtListenerCallback) {
		this.courtListenerCallback = courtListenerCallback;
	}

	public void loadFiles(String opinionsFileName, int loadOpinionNewsPerCallback) throws IOException {
		List<LoadOpinionNew> loadOpinions = new ArrayList<>();
		ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(opinionsFileName)));
		try {
			ZipEntry zipEntry = zipInputStream.getNextEntry();
			while ( zipEntry != null ) {
				Document d = Jsoup.parse(new String(zipInputStream.readAllBytes()));
				Element o = d.selectFirst("div#opinion");
				zipInputStream.closeEntry();
				String n = zipEntry.getName();
				int liop = n.lastIndexOf('(');
				String caseName = n.substring(0, liop);
				String citation = n.substring(liop+1)
						.replace(")", "")
						.replace("Cal. ", "Cal.")
						.replace("Cal.App. ", "Cal.App.")
						.replace("Cal.App.2d Supp.", "Cal.App.Supp.2d")
						.replace("Cal.App.3d Supp.", "Cal.App.Supp.3d")
						.replace("Cal.App.4th Supp.", "Cal.App.Supp.4th")
						.replace("Cal.App.5th Supp.", "Cal.App.Supp.5th")
						.replace("Cal.App.6th Supp.", "Cal.App.Supp.6th")
						.replace("Cal.App.7th Supp.", "Cal.App.Supp.7th");
				if ( citation.equals("26 Cal.4th 236")) {
					System.out.println("n = " + n);
				}
				LoadOpinionNew loadOpinionNew = new LoadOpinionNew(Long.valueOf(total++), caseName, citation, o);
				loadOpinions.add(loadOpinionNew);
				if ( loadOpinions.size() >= loadOpinionNewsPerCallback) {
					courtListenerCallback.callBack(loadOpinions);
					loadOpinions = new ArrayList<>();
				}
				zipEntry = zipInputStream.getNextEntry();
			}
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			zipInputStream.close();
		}
		if ( loadOpinions.size() > 0) {
			courtListenerCallback.callBack(loadOpinions);
		}
	}

}
