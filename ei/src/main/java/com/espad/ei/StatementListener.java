package com.espad.ei;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;

public class StatementListener implements UpdateListener {

	private static final Log log = LogFactory.getLog(StatementListener.class);
	private final String statementName;
		
	public StatementListener(String sName) {
		statementName = sName;
	}
	
	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if (null != newEvents) {
			FormatAndLog(newEvents, statementName, "ESPAD NE ");
		}

		if (null != oldEvents) {
			FormatAndLog(oldEvents, statementName, "ESPAD OU ");
		}
	}

	private void FormatAndLog(EventBean[] events, String statementName,
			String prefix) {
		String result = "";

		for (EventBean eb : events) {
			if (eb instanceof MapEventBean) {
				result = getCSVNameValues((MapEventBean) eb);
			}
		}
		log.info(prefix + statementName + "," + result);
	}

	private String getCSVNameValues(MapEventBean meb) {

		StringBuilder result = null;

		Map<String, Object> mebProperties = meb.getProperties();
		for (String mebKey : mebProperties.keySet()) {
			// log.info("mebKey is of "+mebKey);

			Object mebValue = mebProperties.get(mebKey);

			if (null == result) {
				result = new StringBuilder(1024);
			} else {
				result.append(",");
			}
			// log.info("mebValue is of "+mebValue.getClass());

			if (mebValue instanceof MapEventBean) {
				MapEventBean mebMebValue = (MapEventBean) mebValue;
				result.append(mebKey);
				result.append("=(");
				result.append(getCSVNameValues(mebMebValue));
				result.append(")");
			} else {

				result.append(mebKey);
				result.append("=");
				result.append(mebValue);
			}
		}
		return null == result ? "" : result.toString();
	}
}
