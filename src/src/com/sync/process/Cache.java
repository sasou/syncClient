package com.sync.process;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.sync.common.GetProperties;
import com.sync.common.RedisApi;
import com.sync.common.Tool;
import com.sync.common.WriteLog;
import com.alibaba.fastjson.JSON;

/**
 * Cache Producer
 * 
 * @author sasou <admin@php-gene.com> web:http://www.php-gene.com/
 * @version 1.0.0
 */
public class Cache implements Runnable {
	private RedisApi RedisPool = null;
	private CanalConnector connector = null;
	private String thread_name = null;
	private String canal_destination = null;

	public Cache(String name) {
		thread_name = "canal[" + name + "]:";
		canal_destination = name;
	}

	public void process() {
		int batchSize = 1000;
		connector = CanalConnectors.newSingleConnector(
				new InetSocketAddress(GetProperties.canal.ip, GetProperties.canal.port), canal_destination,
				GetProperties.canal.username, GetProperties.canal.password);

		connector.connect();
		connector.subscribe();
		
		try {
			RedisPool = new RedisApi(canal_destination);
			WriteLog.write(canal_destination, thread_name + "Start-up success!");
			while (true) {
				Message message = connector.getWithoutAck(batchSize); // get batch num
				long batchId = message.getId();
				int size = message.getEntries().size();
				if (!(batchId == -1 || size == 0)) {
					if (syncEntry(message.getEntries())) {
						connector.ack(batchId); // commit
					} else {
						connector.rollback(batchId); // rollback
					}
				}
			}
		} catch (Exception e) {
			WriteLog.write(canal_destination, thread_name + WriteLog.eString(e));
		}
	}

	public void run() {
		while (true) {
 			try {
 				process();
 			} catch (Exception e) {
 				WriteLog.write(canal_destination, thread_name + "canal link failure!");
 			} finally {
 				if (connector != null) {
 					connector.disconnect();
 					connector = null;
 				}
 			}
 		}
	}

	private boolean syncEntry(List<Entry> entrys) {
		String topic = "";
		int no = 0;
		boolean ret = true;
		for (Entry entry : entrys) {
			if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN
					|| entry.getEntryType() == EntryType.TRANSACTIONEND) {
				continue;
			}
			RowChange rowChage = null;
			try {
				rowChage = RowChange.parseFrom(entry.getStoreValue());
			} catch (Exception e) {
				throw new RuntimeException(
						thread_name + "parser of eromanga-event has an error , data:" + entry.toString(), e);
			}

			EventType eventType = rowChage.getEventType();
			Map<String, Object> data = new HashMap<String, Object>();
			Map<String, Object> head = new HashMap<String, Object>();
			head.put("binlog_file", entry.getHeader().getLogfileName());
			head.put("binlog_pos", entry.getHeader().getLogfileOffset());
			head.put("db", entry.getHeader().getSchemaName());
			head.put("table", entry.getHeader().getTableName());
			head.put("type", eventType);
			data.put("head", head);
			topic = Tool.makeTargetName(canal_destination, entry.getHeader().getSchemaName(), entry.getHeader().getTableName());
			no = (int) entry.getHeader().getLogfileOffset();
			for (RowData rowData : rowChage.getRowDatasList()) {
				if (eventType == EventType.DELETE) {
					data.put("before", makeColumn(rowData.getBeforeColumnsList()));
				} else if (eventType == EventType.INSERT) {
					data.put("after", makeColumn(rowData.getAfterColumnsList()));
				} else {
					data.put("before", makeColumn(rowData.getBeforeColumnsList()));
					data.put("after", makeColumn(rowData.getAfterColumnsList()));
				}
				String text = JSON.toJSONString(data);
				try {
					RedisPool.rpush(topic, text);
					if (GetProperties.system_debug > 0) {
						WriteLog.write(canal_destination + ".access", thread_name + "data(" + topic + "," + no + ", " + text + ")");
					}
				} catch (Exception e) {
					WriteLog.write(canal_destination + ".error", thread_name + "redis link failure!" + WriteLog.eString(e));
					ret = false;
				}
			}
			data.clear();
			data = null;
		}
		return ret;
	}

	private Map<String, Object> makeColumn(List<Column> columns) {
		Map<String, Object> one = new HashMap<String, Object>();
		for (Column column : columns) {
			one.put(column.getName(), column.getValue());
		}
		return one;
	}

	protected void finalize() throws Throwable {
		if (connector != null) {
			connector.disconnect();
			connector = null;
		}
	}

}