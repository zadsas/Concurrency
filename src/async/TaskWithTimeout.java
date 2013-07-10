package async;

import java.util.concurrent.TimeoutException;

/**
 * DO NOT INVOKE THE PERFORM METHOD DIRECTLY
 * 
 * @param <T>
 */
public abstract class TaskWithTimeout<T> {
	private Thread threadTiKill;
	private final long timeout;
	private boolean done = false;
	private T result;
	private String taskName;
	private Exception innerException;

	public TaskWithTimeout() {
		this(5000, "<NO-NAME>");
	}

	public TaskWithTimeout(long timeout) {
		this(timeout, "<NO-NAME>");
	}

	public TaskWithTimeout(String callerName) {
		this(5000, callerName);
	}

	public TaskWithTimeout(long timeout, String taskName) {
		this.timeout = timeout;
		this.taskName = taskName;
	}

	public T performWithTimeout() throws Exception {
		threadTiKill = new Executor();
		threadTiKill.start();
		sleep();
		killIfNotDone();
		return result;
	}

	private synchronized void sleep() {
		try {
			this.wait(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private synchronized void finished() {
		done = true;
		this.notifyAll();
	}

	public abstract T perform();

	@SuppressWarnings("deprecation")
	private synchronized void killIfNotDone() throws Exception {
		if (!done) {
			threadTiKill.stop();
			throw new TimeoutException(taskName + " timed out");
		} else if (innerException != null) {
			throw innerException;
		}
	}

	private class Executor extends Thread {
		@Override
		public void run() {
			try {
				result = perform();
			} catch (Exception exception) {
				innerException = exception;
			} finally {
				finished();
			}
		}
	}
}
