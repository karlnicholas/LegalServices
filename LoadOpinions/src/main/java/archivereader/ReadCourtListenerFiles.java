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
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;

import caseparser.CourtListenerParser;
import model.CourtListenerApiOpinion;
import model.CourtListenerCluster;
import model.CourtListenerOpinion;

public class ReadCourtListenerFiles {
	private final Pattern pattern;
	private final Logger logger;
	int total = 0;
	private final ObjectMapper om;
	private final CitationStore citationStore;
	private final StatutesTitles[] statutesTitles;
	

	public ReadCourtListenerFiles(CitationStore citationStore, StatutesTitles[] statutesTitles) {
		this.citationStore = citationStore;
		this.statutesTitles = statutesTitles;
		pattern = Pattern.compile("/");
		logger = Logger.getLogger(ReadCourtListenerFiles.class.getName());
		om = new ObjectMapper();
	}

	public void loadFiles(String opinionsFileName, String clustersFileName, int loadOpinionsPerCallback) throws IOException {
		Map<Long, CourtListenerOpinion> mapLoadOpinions = new TreeMap<Long, CourtListenerOpinion>();
		TarArchiveInputStream tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(clustersFileName))));
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
					CourtListenerCluster courtListenerCluster = om.readValue(content, CourtListenerCluster.class);
					if (courtListenerCluster.getPrecedential_status() != null && courtListenerCluster.getPrecedential_status().equals("Published")) {
						CourtListenerOpinion courtListenerOpinion = new CourtListenerOpinion(courtListenerCluster);
//						if (loadOpinion.getCaseName() != null || !loadOpinion.getCaseName().trim().isEmpty()) {
							mapLoadOpinions.put(courtListenerOpinion.getId(), courtListenerOpinion);
//						}
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
		//
		tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(opinionsFileName))));
		try {
			boolean working = true;
			while (working) {
				List<CourtListenerOpinion> clOps = getCases(tarIn, om, mapLoadOpinions, loadOpinionsPerCallback);
//				if ( ++count <= 1 ) {
//					continue;
//				}
				if (clOps.size() == 0) {
					working = false;
//					courtListenerCallback.shutdown();
					break;
				}
				new CourtListenerParser(new ArrayList<>(clOps), citationStore, statutesTitles).run();
//				courtListenerCallback.callBack(clOps);
// courtListenerCallback.shutdown();
// break;
			}
		} finally {
			try {
				tarIn.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, null, e);
			}
		}
	}

	private List<CourtListenerOpinion> getCases(TarArchiveInputStream tarIn, ObjectMapper om,
			Map<Long, CourtListenerOpinion> mapLoadOpinions, int loadOpinionsPerCallback) throws IOException {
		TarArchiveEntry entry;
		int count = 0;
		List<CourtListenerOpinion> clOps = new ArrayList<CourtListenerOpinion>(loadOpinionsPerCallback);
		while ((entry = tarIn.getNextTarEntry()) != null) {
			if (tarIn.canReadEntryData(entry)) {
/*				
if ( ++total < 38 )
	continue;
*/
				int entrySize = (int) entry.getSize();
				byte[] content = new byte[entrySize];
				int offset = 0;

				while ((offset += tarIn.read(content, offset, (entrySize - offset))) != -1) {
					if (entrySize - offset == 0)
						break;
				}
				// System.out.println("Content:" + new String(content));
				CourtListenerApiOpinion op = om.readValue(content, CourtListenerApiOpinion.class);
				Long id = Long.valueOf(pattern.split(op.getResource_uri())[7]);
				CourtListenerOpinion courtListenerOpinion = mapLoadOpinions.get(id);
				if (courtListenerOpinion != null) {
					if (op.getHtml_lawbox() != null ) {
						courtListenerOpinion.setHtml_lawbox(op.getHtml_lawbox());
						courtListenerOpinion.setOpinions_cited(op.getOpinions_cited());
						clOps.add(courtListenerOpinion);
					}
					mapLoadOpinions.remove(id);
				}
				/*
				 * if( op.getPrecedentialStatus() == null ) { continue; } if
				 * (op.getPrecedentialStatus().toLowerCase().equals(
				 * "unpublished")) { continue; } if (op.getHtmlLawbox() == null)
				 * { continue; } if
				 * (op.getCitation().getCaseName().trim().length() == 0) {
				 * System.out.print('T'); continue; }
				 */

				if (++count >= loadOpinionsPerCallback)
					break;
/*				
if ( total >= 39 )
	break;
*/					
			}
		}
		return clOps;
	}
}
