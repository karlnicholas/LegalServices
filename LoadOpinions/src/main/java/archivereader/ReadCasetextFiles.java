package archivereader;

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
import org.jsoup.select.Elements;

import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;

import caseparser.CasetextParser;
import model.CasetextOpinion;

public class ReadCasetextFiles {
	private int total = 0;
	private final List<CasetextOpinion> loadOpinions = new ArrayList<>();
	private final CitationStore citationStore;
	private final StatutesTitles[] statutesTitles;
	

	public ReadCasetextFiles(CitationStore citationStore, StatutesTitles[] statutesTitles) {
		this.citationStore = citationStore;
		this.statutesTitles = statutesTitles;
	}


	public void loadFiles(String opinionsFileName, int loadOpinionNewsPerCallback) throws IOException {
		ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(opinionsFileName)));
		try {
			ZipEntry zipEntry = zipInputStream.getNextEntry();
			while ( zipEntry != null ) {
				int entrySize = (int) zipEntry.getSize();
				byte[] content = new byte[entrySize];
				int offset = 0;

				while ((offset += zipInputStream.read(content, offset, (entrySize - offset))) != -1) {
					if (entrySize - offset == 0)
						break;
				}
				Document d = Jsoup.parse(new String(content));
				Element o = d.selectFirst("main.case-page");
				zipInputStream.closeEntry();
				String n = zipEntry.getName();
				if ( o != null ) {
					process(o, n, loadOpinionNewsPerCallback);
				}
				zipEntry = zipInputStream.getNextEntry();
			}
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			zipInputStream.close();
		}
		if ( loadOpinions.size() > 0) {
			new CasetextParser(loadOpinions, citationStore, statutesTitles).run();
		}
	}
	private void process(Element o, String n, int loadOpinionNewsPerCallback) {
		Elements ces = o.select("div.citations");
		ces = ces.select("div.citation");
		String citation = null;
		for ( Element ce: ces) {
			String cec = ce.text();
			if ( cec.contains("(")) {
				cec = cec.substring(0, cec.indexOf("(")).trim();
			}
			cec = cec
					.replace(")", "")
//					.replace("Cal. ", "Cal.")
//					.replace("Cal.App. ", "Cal.App.")
					.replace("Cal.App.2d Supp.", "Cal.App.Supp.2d")
					.replace("Cal.App.3d Supp.", "Cal.App.Supp.3d")
					.replace("Cal.App.4th Supp.", "Cal.App.Supp.4th")
					.replace("Cal.App.5th Supp.", "Cal.App.Supp.5th")
					.replace("Cal.App.6th Supp.", "Cal.App.Supp.6th")
					.replace("Cal.App.7th Supp.", "Cal.App.Supp.7th");
			if ( OpinionKey.testValidOpinionKey(cec) ) {
				citation = cec;
				break;
			}
		}
		if ( citation == null ) {
			return;
		}
		System.out.println(citation);
		CasetextOpinion CasetextOpinion = new CasetextOpinion(Long.valueOf(total++), n, null, citation, o);
		loadOpinions.add(CasetextOpinion);
		if ( loadOpinions.size() >= loadOpinionNewsPerCallback) {
			new CasetextParser(new ArrayList<>(loadOpinions), citationStore, statutesTitles).run();
//			courtListenerCallback.callBack(loadOpinions);
			loadOpinions.clear();
		}
		
	}
}
