package com.sync.process;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import com.sync.common.MemApi;
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
	private MemApi MemPool = null;
	private CanalConnector connector = null;
	private String thread_name = null;
	private String canal_destination = null;
	private String sign = null;

	public Cache(String name) {
		thread_name = "canal[" + name + "]:";
		canal_destination = name;
		sign = GetProperties.target.get(canal_destination).sign;
	}

	public void process() {
		int batchSize = 1000;
		connector = CanalConnectors.newSingleConnector(
				new InetSocketAddress(GetProperties.canal.ip, GetProperties.canal.port), canal_destination,
				GetProperties.canal.username, GetProperties.canal.password);

		connector.connect();
		connector.subscribe();
		
		try {
			if("redis".equals(GetProperties.target.get(canal_destination).plugin)) {
				RedisPool = new RedisApi(canal_destination);
			}
			if("memcached".equals(GetProperties.target.get(canal_destination).plugin)) {
				MemPool = new MemApi(canal_destination);
			}
			
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
		String table = "";
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
			HashSet<String> versionField = new HashSet<String>();
			table = entry.getHeader().getSchemaName() + "." + entry.getHeader().getTableName();
			versionField.add(table);
			for (RowData rowData : rowChage.getRowDatasList()) {
				if (eventType == EventType.DELETE) {
					updateColumn(versionField, rowData.getBeforeColumnsList(), table);
				} else if (eventType == EventType.INSERT) {
					updateColumn(versionField, rowData.getAfterColumnsList(), table);
				} else {
					updateColumn(versionField, rowData.getBeforeColumnsList(), table);
					updateColumn(versionField, rowData.getAfterColumnsList(), table);
				}
				String text = JSON.toJSONString(versionField);
				try {
					Iterator<String> iterator = versionField.iterator();
					while (iterator.hasNext()) {
						if("redis".equals(GetProperties.target.get(canal_destination).plugin)) {
							RedisPool.incr(sign + Tool.md5(iterator.next()));	
						}
						if("memcached".equals(GetProperties.target.get(canal_destination).plugin)) {
							MemPool.incr(sign + Tool.md5(iterator.next()));	
						}
					}
					if (GetProperties.system_debug > 0) {
						WriteLog.write(canal_destination + ".access", thread_name + "data(" + text + ")");
					}
				} catch (Exception e) {
					WriteLog.write(canal_destination + ".error", thread_name + "redis link failure!" + WriteLog.eString(e));
					ret = false;
				}
			}
			versionField.clear();
			versionField = null;
		}
		return ret;
	}

	private void updateColumn(HashSet<String> versionField, List<Column> columns, String table) {
		for (Column column : columns) {
			String key = table + "." + column.getName();
			if (column.getIsKey() || (GetProperties.target.get(canal_destination).filter.indexOf(key) != -1)) {
				versionField.add(key + "." + column.getValue());
			}
		}
	}

	protected void finalize() throws Throwable {
		if (connector != null) {
			connector.disconnect();
			connector = null;
		}
	}

}