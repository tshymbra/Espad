package com.espad.httpush;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espad.ei.CSVManager;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esperio.AdapterCoordinator;
import com.espertech.esperio.AdapterCoordinatorImpl;

public class Coordinator {

	private AdapterCoordinator coordinator = null;
	private static final Log log = LogFactory.getLog(Coordinator.class);
	List<CSVOverHTTPPusher> pushers = new ArrayList<CSVOverHTTPPusher>();

	public void InitPlayback(EPServiceProvider epService, String eventDataFolder, List<String> eventDescriptors) {
		if (null == coordinator) {
			coordinator = new AdapterCoordinatorImpl(epService, false);
		}

		for (String eventDescriptor : eventDescriptors) {
			String eventTypeName = CSVManager.GetEventNameFromEventDescriptor(eventDescriptor);
			String [] propertyOrder = CSVManager.GetEventPropertiesNamesFromEventDescriptor(eventDescriptor);

			log.info("ESPAD event definition found in engine "+eventTypeName);

			CSVOverHTTPPusher httpEventPusher = new CSVOverHTTPPusher();

			if (httpEventPusher.TunnelCSVOverHTTP(epService, eventDataFolder, eventTypeName, propertyOrder)) {
				log.info("ESPAD event definition added to CSVOverHTTPPusher "+eventTypeName);
				pushers.add(httpEventPusher);
			}
		}

		if (0 == pushers.size()) {
			log.error("ESPAD coordinator has no adapters, exiting");
			return;
		}
		
		for (CSVOverHTTPPusher pusher : pushers) {
			log.info("ESPAD coordinator will coordinate pusher for "+pusher.getSpec().geteventTypeName());
			coordinator.coordinate(pusher.getInputAdpater());
		}

		coordinator.start();
	}
}
