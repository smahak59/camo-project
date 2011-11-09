package cn.edu.nju.ws.camo.android.command;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CommandSender {

	private BlockingQueue<Runnable> bkQueue = new LinkedBlockingQueue<Runnable>();
	private ThreadPoolExecutor threadExec = new ThreadPoolExecutor(1, 1, 7, TimeUnit.DAYS, bkQueue);
	private static CommandSender instance = null;
	
	private CommandSender() {}
	
	public static CommandSender getInstance() {
		if(instance == null)
			instance = new CommandSender();
		return instance;
	}
	
	public void sendCommand(Command cmd) {
		CommandThread newCmdThread = new CommandThread(cmd);
		threadExec.execute(newCmdThread);
	}
	
	public void destroy() throws InterruptedException {
		threadExec.shutdown();
		threadExec.awaitTermination(1, TimeUnit.DAYS);
	}
	
	class CommandThread extends Thread {
		private Command cmd;
		CommandThread(Command cmd) {
			this.cmd = cmd;
		}
		@Override
		public void run() {
			cmd.execute();
		}
	}
}
