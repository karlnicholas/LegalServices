package com.github.karlnicholas.legalservices.user.security.service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

//@TransactionManagement(TransactionManagementType.BEAN)
//@Singleton
//@Service
public class UserScheduledService {
    private Logger logger = LoggerFactory.getLogger(UserScheduledService.class);
    private final SystemService systemService;

//    @Resource private EJBContext context;

    public UserScheduledService(SystemService systemService) {
		super();
		this.systemService = systemService;
	}


//    @Schedule(second="0", minute="10", hour="18", dayOfWeek="Mon-Fri", persistent=false)        // 12:00 am every day
//    @Scheduled(cron = "0 10 18 * * MON-FRI")
    public void opinionReport() {
    	systemService.sendOpinionReports();
    }

//    @Schedule(second="0", minute="15", hour="18", dayOfWeek="Mon-Fri", persistent=false)        // 12:00 am every day
    @Scheduled(cron = "0 15 18 * * MON-FRI")
    public void systemReport() {
        Map<String, Long> memoryMap = new TreeMap<>();
        OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
        memoryMap.put("0 cpuLoad", (long)osMxBean.getSystemLoadAverage());

        ThreadMXBean threadmxBean = ManagementFactory.getThreadMXBean();
        int threadCount = threadmxBean.getThreadCount();
        memoryMap.put("1 cpuRunningThreads", (long)threadCount);

        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memHeapUsage = memBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memBean.getNonHeapMemoryUsage();
        memoryMap.put("2 heapInit", memHeapUsage.getInit() / (1024*1024));
        memoryMap.put("3 heapMax", memHeapUsage.getMax() / (1024*1024));
        memoryMap.put("4 heapCommit", memHeapUsage.getCommitted() / (1024*1024));
        memoryMap.put("5 heapUsed", memHeapUsage.getUsed() / (1024*1024));
        memoryMap.put("5 nonHeapInit", nonHeapUsage.getInit() / (1024*1024));
        memoryMap.put("7 nonHeapMax", nonHeapUsage.getMax() / (1024*1024));
        memoryMap.put("8 nonHeapCommit", nonHeapUsage.getCommitted() / (1024*1024));
        memoryMap.put("9 nonHeapUsed", nonHeapUsage.getUsed() / (1024*1024));
    	systemService.sendSystemReport(memoryMap);
    }
    
//    @Schedule(second="0", minute="20", hour="18", persistent=false)        // 04:00 am every day
//    @Scheduled(cron = "0 20 18 * * *")
//    @Transactional	// user
    public void welcomingService() {
        logger.info("STARTING welcomingService");
		systemService.doWelcomeService();
        logger.info("DONE welcomingService");
    }
/*
    @Schedule(second="0", minute="30", hour="0", persistent=false)        // 12:30 am every day
    public void verifyHousekeeping() {
        // So, do the real work.
        // / accountRepository.findUnverified
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        dayOfYear = dayOfYear - 4;
        if ( dayOfYear < 1 ) {
            year = year - 1;
            dayOfYear = 365 + dayOfYear;
        }
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_YEAR, dayOfYear);
        Date threeDaysAgo = cal.getTime();

        List<User> users = userSession.findAllUnverified();
        for ( User user: users ) {
            if ( user.getCreateDate().compareTo(threeDaysAgo) < 0 ) {
                userSession.delete(user.getId());
                continue;
            }
    
            // Prepare the evaluation context
            verifyMailer.sendEmail(user);
//            System.out.println("Resend = " + account.getEmail());
        }
            
//        String htmlContent = mailTemplateEngine.process("verify.html", ctx);
        logger.info("VerifyEmail's sent"  );
    }
*/
}