package loadnew;

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

import loadmodelnew.LoadOpinionNew;

public class LoadCourtListenerFiles {
	private final CourtListenerCallback courtListenerCallback;
	int total = 0;
	DateTimeFormatter df1 = DateTimeFormatter.ofPattern("MMM d, yyyy");
	Pattern datePattern1 = Pattern.compile("(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\ (([0-9])|([0-2][0-9])|([3][0-1]))\\,\\ \\d{4}");
	DateTimeFormatter df2 = DateTimeFormatter.ofPattern("MMM d yyyy");
	Pattern datePattern2 = Pattern.compile("(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\ (([0-9])|([0-2][0-9])|([3][0-1]))\\ \\d{4}");
	List<LoadOpinionNew> loadOpinions = new ArrayList<>();

	public LoadCourtListenerFiles(CourtListenerCallback courtListenerCallback) {
		this.courtListenerCallback = courtListenerCallback;
	}

	public void loadFiles(String opinionsFileName, int loadOpinionNewsPerCallback) throws IOException {
		ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(opinionsFileName)));
		try {
			ZipEntry zipEntry = zipInputStream.getNextEntry();
			while ( zipEntry != null ) {
				Document d = Jsoup.parse(new String(zipInputStream.readAllBytes()));
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
			courtListenerCallback.callBack(loadOpinions);
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
		LoadOpinionNew loadOpinionNew = new LoadOpinionNew(Long.valueOf(total++), caseName, caseDate, citation, o);
		loadOpinions.add(loadOpinionNew);
		if ( loadOpinions.size() >= loadOpinionNewsPerCallback) {
			courtListenerCallback.callBack(loadOpinions);
			loadOpinions = new ArrayList<>();
		}
		
	}
}
