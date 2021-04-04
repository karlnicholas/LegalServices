package archivereader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;

import caseparser.CourtListenerParser;
import model.CourtListenerCluster;
import model.CourtListenerOpinion;

public class ReadCourtListenerFiles {
	private final Logger logger;
	int total = 0;
	private final ObjectMapper om;
	private final CitationStore citationStore;
	private final StatutesTitles[] statutesTitles;
	private final List<CourtListenerCluster> loadOpinions = new ArrayList<>();


	public ReadCourtListenerFiles(CitationStore citationStore, StatutesTitles[] statutesTitles) {
		this.citationStore = citationStore;
		this.statutesTitles = statutesTitles;
		logger = Logger.getLogger(ReadCourtListenerFiles.class.getName());
		om = new ObjectMapper()
	        .findAndRegisterModules()
	        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

	public void loadFiles(String opinionsFileName, String clustersFileName, int loadOpinionsPerCallback) throws IOException {
		Map<Long, CourtListenerOpinion> mapLoadOpinions = new TreeMap<Long, CourtListenerOpinion>();
		long totalCount = 0;
		TarArchiveInputStream tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(opinionsFileName))));
		TarArchiveEntry entry;
		try {
			while ((entry = tarIn.getNextTarEntry()) != null) {
				if (tarIn.canReadEntryData(entry)) {
					int entrySize = (int) entry.getSize();
					byte[] content = new byte[entrySize];
					int offset = 0;

					while ((offset += tarIn.read(content, offset, (entrySize - offset))) != -1) {
						if (entrySize - offset == 0)
							break;
					}
					// System.out.println("Content:" + new String(content));
					// http://www.courtlistener.com/api/rest/v3/clusters/1361768/
					try {
						CourtListenerOpinion courtListenerOpinion = om.readValue(content, CourtListenerOpinion.class);
						mapLoadOpinions.put(courtListenerOpinion.id, courtListenerOpinion);
					} catch ( UnrecognizedPropertyException ex) {
						System.out.println(new String(content));
						throw ex;
					}
				}
			}
		} finally {
			try {
				tarIn.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, null, e);
			}
		}
		
		tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(clustersFileName))));
		try {
			while ((entry = tarIn.getNextTarEntry()) != null) {
				if (tarIn.canReadEntryData(entry)) {
					int entrySize = (int) entry.getSize();
					byte[] content = new byte[entrySize];
					int offset = 0;

					while ((offset += tarIn.read(content, offset, (entrySize - offset))) != -1) {
						if (entrySize - offset == 0)
							break;
					}
					// System.out.println("Content:" + new String(content));
					// http://www.courtlistener.com/api/rest/v3/clusters/1361768/
					CourtListenerCluster courtListenerCluster = om.readValue(content, CourtListenerCluster.class);
					boolean citation = false;
					for ( CourtListenerCluster.Citation citeClass: courtListenerCluster.citations) {
						String cec = citeClass.volume + " " + citeClass.reporter.replace(" " , "") + " " + citeClass.page;
						if ( OpinionKey.testValidOpinionKey(cec) ) {
							citation = true;
							break;
						}
					}
					if ( !citation) {
						continue;
					}
					courtListenerCluster.opinions = new ArrayList<>();
					for ( String subOpinion: courtListenerCluster.sub_opinions) {
						Long id = Long.valueOf(subOpinion
								.replace("https://www.courtlistener.com:80/api/rest/v3/opinions/", "")
								.replace("https://www.courtlistener.com/api/rest/v3/opinions/", "")
								.replace("/", "")
								);
						courtListenerCluster.opinions.add(mapLoadOpinions.get(id));
					}
					for ( CourtListenerOpinion opinion: courtListenerCluster.opinions) {
						if ( opinion.html_columbia == null || opinion.html_columbia.isBlank() ) {
							if ( opinion.html_lawbox == null || opinion.html_lawbox .isBlank() ) {
								continue;
							}
						}
					}
					totalCount++;
					loadOpinions.add(courtListenerCluster);
					if ( loadOpinions.size() >= loadOpinionsPerCallback) {
						new CourtListenerParser(new ArrayList<>(loadOpinions), citationStore, statutesTitles).run();
						loadOpinions.clear();
					}
				}
			}
		} finally {
			try {
				tarIn.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, null, e);
			}
		}
		System.out.println(totalCount);
	}
		//
}
