package archivereader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;

import caseparser.JustiaParser;
import model.JustiaOpinion;

public class ReadJustiaFiles {
	private int total = 0;
	private final DateTimeFormatter df1 = DateTimeFormatter.ofPattern("MMM d, yyyy");
	private final Pattern datePattern1 = Pattern.compile("(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\ (([0-9])|([0-2][0-9])|([3][0-1]))\\,\\ \\d{4}");
	private final DateTimeFormatter df2 = DateTimeFormatter.ofPattern("MMM d yyyy");
	private final Pattern datePattern2 = Pattern.compile("(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\ (([0-9])|([0-2][0-9])|([3][0-1]))\\ \\d{4}");
	private final List<JustiaOpinion> loadOpinions = new ArrayList<>();
	private final CitationStore citationStore;
	private final StatutesTitles[] statutesTitles;
	

	public ReadJustiaFiles(CitationStore citationStore, StatutesTitles[] statutesTitles) {
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
				Element o = d.selectFirst("div#opinion");
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
			new JustiaParser(loadOpinions, citationStore, statutesTitles).run();
		}
	}
	private void process(Element o, String n, int loadOpinionNewsPerCallback) {
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
		String date = o.text();
		date = date.substring(0, date.length() > 200 ? 200 : date.length())
				.replace(".", "")
				.replace("January", "Jan")
				.replace("February", "Feb")
				.replace("March", "Mar")
				.replace("April", "Apr")
				.replace("June", "Jun")
				.replace("July", "Jul")
				.replace("August", "Aug")
				.replace("Sept", "Sep")
				.replace("Sepember", "Sep")
				.replace("November", "Nov")
				.replace("October", "Oct")
				.replace("December", "Dec");
		LocalDate caseDate = null;
		Matcher mr = datePattern1.matcher(date);
		if (! mr.find() ) {
			mr = datePattern2.matcher(date);
			if (mr.find() ) {
				caseDate = LocalDate.parse(date.substring(mr.start(), mr.end()), df2);			
			}
		} else {
			caseDate = LocalDate.parse(date.substring(mr.start(), mr.end()), df1);			
		}
		JustiaOpinion justiaOpinion = new JustiaOpinion(Long.valueOf(total++), caseName, caseDate, citation, o);
		loadOpinions.add(justiaOpinion);
		if ( loadOpinions.size() >= loadOpinionNewsPerCallback) {
			new JustiaParser(new ArrayList<>(loadOpinions), citationStore, statutesTitles).run();
//			courtListenerCallback.callBack(loadOpinions);
			loadOpinions.clear();
		}
		
	}
}
