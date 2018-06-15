package pirTestBase;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

@SuppressWarnings("serial")
public class PageVisitSpout extends BaseRichSpout {
	private static SpoutOutputCollector outputCollector;
	private Integer totalVisitCount;
	
	@SuppressWarnings("rawtypes") 
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		outputCollector = collector;
		totalVisitCount = 0;
	}

	@Override
	public void nextTuple() {
		String[] urls = {"Task1", "Task2", "Task3"};
		Integer[] userIds = {1, 2, 3, 4, 5};
		Integer[] complexity = {0, 1, 2, 3};
		
		
		String url = urls[ThreadLocalRandom.current().nextInt(urls.length)];
		Integer userId = userIds[ThreadLocalRandom.current().nextInt(userIds.length)];
		Integer complex = complexity[ThreadLocalRandom.current().nextInt(complexity.length)];
		Values values = new Values(url, userId, complex);
		outputCollector.emit(values, values);
		Utils.sleep(100);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("url", "userId", "complex"));
	}
	
	@Override
	public void ack(Object msgId) {
		totalVisitCount ++;
		System.out.printf("Correctly processed: %s .", msgId);
		System.out.println("Total task processed at the moment : " + totalVisitCount);
	}
	
	@Override
	public void fail(Object msgId) {
		System.out.printf("ERROR processing: %s\n", msgId);
		Values tuple = (Values)msgId;
		outputCollector.emit(tuple, msgId);
	}
}