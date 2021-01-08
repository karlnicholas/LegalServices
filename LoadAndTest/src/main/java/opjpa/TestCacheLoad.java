package opjpa;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"opca", "opjpa"})
@ConditionalOnProperty(name = "TestCacheLoad.active", havingValue = "true", matchIfMissing = false)
@EnableJpaRepositories(basePackages = {"opca"})
public class TestCacheLoad implements ApplicationRunner {

	Logger logger = Logger.getLogger(TestCacheLoad.class.getName());

	public static void main(String[] args) throws Exception {
		SpringApplication.run(TestCacheLoad.class, args);
	}
	
	@Autowired
	private EntityManager entityManager;
	String nQuery="select \r\n" + 
			"o.id as o_id,\r\n" + 
			"o.countreferringopinions as o_countreferringopinions,\r\n" + 
			"o.opiniondate as o_opiniondate,\r\n" + 
			"o.page as o_page,\r\n" + 
			"o.volume as o_volume,\r\n" + 
			"o.vset as o_vset,\r\n" + 
			"o.title as o_title,\r\n" + 
			"osc.countreferences as osc_countreferences,\r\n" + 
			"sc.designated as sc_designated,\r\n" + 
			"sc.lawcode as sc_lawcode,\r\n" + 
			"sc.sectionnumber as sc_sectionnumber,\r\n" + 
			"oc.countreferringopinions as oc_countreferringopinions,\r\n" + 
			"oc.opiniondate as oc_opiniondate,\r\n" + 
			"oc.page as oc_page,\r\n" + 
			"oc.volume as oc_volume,\r\n" + 
			"oc.vset as oc_vset,\r\n" + 
			"oc.title as oc_title,\r\n" + 
			"ocosc.countreferences as ocosc_countreferences,\r\n" + 
			"ocsc.id as ocsc_id,\r\n" + 
			"ocsc.designated as ocsc_designated,\r\n" + 
			"ocsc.lawcode as ocsc_lawcode,\r\n" + 
			"ocsc.sectionnumber as ocsc_sectionnumber,\r\n" + 
			"sp.filename,\r\n" + 
			"sp.fileextension\r\n" + 
			"from opinionbase o \r\n" + 
			"left outer join opinionstatutecitation osc on osc.opinionbase_id = o.id \r\n" + 
			"left outer join statutecitation sc on sc.id = osc.statutecitation_id\r\n" + 
			"left outer join opinionbase_opinioncitations oboc on oboc.referringopinions_id = o.id\r\n" + 
			"left outer join opinionbase oc on oc.id = oboc.opinioncitations_id\r\n" + 
			"left outer join opinionstatutecitation ocosc on  ocosc.opinionbase_id = oc.id \r\n" + 
			"left outer join statutecitation ocsc on ocsc.id = ocosc.statutecitation_id\r\n" + 
			"join slipproperties sp on o.id = sp.slipopinion_id\r\n" + 
			"where o.dtype=-1344462334;\r\n" + 
			"";
	@Override
	public void run(ApplicationArguments args) throws Exception {
//		StatutesService statutesService = new StatutesServiceClientImpl("http://localhost:8090/");
		@SuppressWarnings("unchecked")
		List<Tuple> l = entityManager.createNativeQuery(nQuery, Tuple.class).getResultList();
		Map<String, Optional<DecodeClass>> c = l.stream().parallel().collect(Collectors.groupingBy(tuple->tuple.get("o_id").toString(), 
				Collectors.mapping(DecodeClass::new, Collectors.reducing(DecodeClass::combine))));
        c.forEach((id, dc)->System.out.println(id+":"+dc.get().slipOpinion+":"+dc.get().slipOpinion.getStatuteCitations().size()));
	}
}
