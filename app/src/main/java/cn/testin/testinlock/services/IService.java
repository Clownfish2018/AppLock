package cn.testin.testinlock.services;

public interface IService {
	
	public void start();

	public void stop();

	public void startWork(String packageName);
	
	public void doWork();

	public void stopWork();
}
