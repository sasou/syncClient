package com.sync.process;

import java.net.InetSocketAddress;
import java.util.ArrayList;
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

import com.alibaba.fastjson.JSON;

/**
 * Redis Producer
 * 
 * @author sasou <admin@php-gene.com> web:http://www.php-gene.com/
 * @version 1.0.0
 */
public class Redis implements Runnable {
	private RedisApi RedisPool = null;
	private CanalConnector connector = null;
	private int system_debug = 0;
	private String thread_name = null;
	private String canal_destination = null;

	public Redis(String name) {
		thread_name = "canal[" + name + "]:";
		canal_destination = name;
	}

	public void process() {
		system_debug = GetProperties.system_debug;

		int batchSize = 1000;
		connector = CanalConnectors.newSingleConnector(
				new InetSocketAddress(GetProperties.canal_ip, GetProperties.canal_port), canal_destination,
				GetProperties.canal_username, GetProperties.canal_password);

		connector.connect();
		if (!"".equals(GetProperties.canal_filter)) {
			connector.subscribe(GetProperties.canal_filter);
		} else {
			connector.subscribe();
		}

		connector.rollback();

		try {
			RedisPool = new RedisApi();
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
		} finally {
			if (connector != null) {
				connector.disconnect();
				connector = null;
			}
		}
	}

	public void run() {
		while (true) {
			try {
				process();
			} catch (Exception e) {
				System.out.println(thread_name + "canal link failure!");
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
			topic = "sync_" + entry.getHeader().getSchemaName() + "_" + entry.getHeader().getTableName();
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
					if (system_debug > 0) {
						System.out.println(thread_name + "data(" + topic + "," + no + ", " + text + ")");
					}
				} catch (Exception e) {
					if (system_debug > 0) {
						System.out.println(thread_name + "redis link failure!");
					}
					ret = false;
				}
			}
			data.clear();
			data = null;
		}
		return ret;
	}

	private List<Map<String, Object>> makeColumn(List<Column> columns) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Column column : columns) {
			Map<String, Object> one = new HashMap<String, Object>();
			one.put("name", column.getName());
			one.put("value", column.getValue());
			one.put("update", column.getUpdated());
			list.add(one);
		}
		return list;
	}

	protected void finalize() throws Throwable {
		if (connector != null) {
			connector.disconnect();
			connector = null;
		}
	}

}