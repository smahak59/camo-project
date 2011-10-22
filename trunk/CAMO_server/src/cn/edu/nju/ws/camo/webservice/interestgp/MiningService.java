package cn.edu.nju.ws.camo.webservice.interestgp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MiningService{
	
	private BlockingQueue<Runnable> bkQueue = new LinkedBlockingQueue<Runnable>();
	private ThreadPoolExecutor threadExec = new ThreadPoolExecutor(3, 5, 7, TimeUnit.DAYS, bkQueue);
	
	private static MiningService instance = null;
	
	private MiningService() {}
	
	public static MiningService getInstance() {
		if(instance == null)
			instance = new MiningService();
		return instance;
	}
	
	public void executeJob(Runnable job) {
		threadExec.execute(job);
	}
}
