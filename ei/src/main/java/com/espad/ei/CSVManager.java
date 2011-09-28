package com.espad.ei;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esperio.AdapterInputSource;
import com.espertech.esperio.csv.CSVInputAdapter;
import com.espertech.esperio.csv.CSVInputAdapterSpec;

public class CSVManager {

	private final static String EVENT_DEFS_FOLDER_NAME = "eventdefs/";
	private final static String EVENT_DEFS_FILE_NAME = "events.names";

	private final static String EVENT_DEFS_SPLIT_CHARACTER = "-";
	private final static String EVENT_NAME_PROPERTIES_DEF_SPLIT_CHARACTER = ",";
	private final static String EVENT_NAME_PROPERTY_NAME_TO_TYPE_DEF_SPLIT_CHARACTER = " ";

	private static final Log log = LogFactory.getLog(CSVManager.class);

	// Example of a valid event descriptor:
	// PressRelease,timemark,news,person,int powerlevel,long timestamp
	public static String GetEventNameFromEventDescriptor(String eventDescriptor) {
		int firstOccurence = eventDescriptor
				.indexOf(EVENT_NAME_PROPERTIES_DEF_SPLIT_CHARACTER);
		if (-1 == firstOccurence) {
			return eventDescriptor;
		}

		String result = eventDescriptor.substring(0, firstOccurence);
		return result;
	}

	public static String[] GetEventPropertiesFromEventDescriptor(
			String eventDescriptor) {
		int firstOccurence = eventDescriptor
				.indexOf(EVENT_NAME_PROPERTIES_DEF_SPLIT_CHARACTER);
		if (-1 == firstOccurence) {
			return null;
		}

		String eventProperties = eventDescriptor.substring(firstOccurence + 1);
		String[] eventPropertiesParts = eventProperties
				.split(EVENT_NAME_PROPERTIES_DEF_SPLIT_CHARACTER);

		List<String> cleanEventProperties = new ArrayList<String>();
		for (String eventProperty : eventPropertiesParts) {
			String frontCleanEventProperty = ltrim(eventProperty);
			String cleanEventProperty = rtrim(frontCleanEventProperty);

			cleanEventProperties.add(cleanEventProperty);
		}

		return cleanEventProperties.toArray(new String[0]);
	}

	/* remove leading whitespace */
	private static String ltrim(String source) {
		return source.replaceAll("^\\s+", "");
	}

	/* remove trailing whitespace */
	private static String rtrim(String source) {
		return source.replaceAll("\\s+$", "");
	}

	public static CSVInputAdapter startNewCSVInputAdapter(
			EPServiceProvider epServiceProvider, String eventName,
			List<String> propertyOrder) {
        
        AdapterInputSource source = null;
        try {   
            source = new AdapterInputSource(EVENT_DEFS_FOLDER_NAME + eventName + ".csv");
        } catch (Error ex) {
            log.error("ESPAD, error creating AdapterInputSource, please ensure esperio-csv exists in classpath.");
        };

		CSVInputAdapterSpec adapterSpec = new CSVInputAdapterSpec(source,
				eventName);
		adapterSpec.setPropertyOrder(propertyOrder.toArray(new String[0]));

		log.debug("ESPAD   event " + eventName + " got propertyOrder of size ="
				+ adapterSpec.getPropertyOrder().length);
		log.debug("ESPAD defining event{" + eventName + "}");

		// adapterSpec.setTimestampColumn("timestamp");

		// this has accelerated the startup significantly
		adapterSpec.setUsingEngineThread(true);

		CSVInputAdapter inputAdapter = new CSVInputAdapter(epServiceProvider,
				adapterSpec);
		inputAdapter.start();

		return inputAdapter;
	}

	public static String[] GetEventPropertiesNamesFromEventDescriptor(
			String eventDescriptor) {
		String[] cleanEventProperties = GetEventPropertiesFromEventDescriptor(eventDescriptor);
		if (null == cleanEventProperties) {
			return null;
		}

		List<String> propertyNames = new ArrayList<String>();
		for (String eventProperty : cleanEventProperties) {
			String[] propertyTypeToName = eventProperty
					.split(EVENT_NAME_PROPERTY_NAME_TO_TYPE_DEF_SPLIT_CHARACTER);

			if (0 == propertyTypeToName.length) {
				continue;
			}

			String propertyName = "";

			if (propertyTypeToName.length > 1) {
				propertyName = propertyTypeToName[1];
			} else {
				propertyName = propertyTypeToName[0];
			}
			propertyNames.add(propertyName);
		}

		return propertyNames.toArray(new String[0]);
	}

	public static List<String> InitAllEventDefinitionsFromCSV(
			EPServiceProvider epServiceProvider) {
		String eventDescriptorsResourceName = EVENT_DEFS_FOLDER_NAME
				+ EVENT_DEFS_FILE_NAME;
		InputStream configuredEventNames = CSVManager.class.getClassLoader()
				.getResourceAsStream(eventDescriptorsResourceName);
		List<String> result = new ArrayList<String>();
		try {
			String converted = convertStreamToString(configuredEventNames);
			String[] eventDescriptors = converted
					.split(EVENT_DEFS_SPLIT_CHARACTER);

			log.info("ESPAD started processing event descriptors from "
					+ eventDescriptorsResourceName);

			for (String eventDescriptor : eventDescriptors) {
				if (0 == eventDescriptor.length()) {
					continue;
				}

				log.info("ESPAD ed |" + eventDescriptor + "|");

				String eventName = GetEventNameFromEventDescriptor(eventDescriptor);
				if (0 == eventName.length()) {
					log.info("ESPAD skipping event descriptor without event name |"
							+ eventDescriptor + "|");
					continue;
				}

				List<String> propertyOrder = new ArrayList<String>();
				String[] eventPropertiesParts = GetEventPropertiesFromEventDescriptor(eventDescriptor);

				if (null == eventPropertiesParts
						|| 0 == eventPropertiesParts.length) {
					log.info("ESPAD skipping event descriptor without properties for |"
							+ eventName + "|");
					continue;
				}

				for (String eventProperty : eventPropertiesParts) {
					propertyOrder.add(eventProperty);
					log.debug("ESPAD   	 added to propertyOrder |"
							+ eventProperty + "|");
				}

				startNewCSVInputAdapter(epServiceProvider, eventName,
						propertyOrder);

				log.info("ESPAD defined event{" + eventName + "}");
				result.add(eventDescriptor);

				/*
				 * String [] definedPropertyOrder =
				 * adapterSpec.getPropertyOrder(); for (String
				 * propertyOrderElement : definedPropertyOrder) {
				 * log.info("ESPAD propertyOrderElement "+
				 * propertyOrderElement); }
				 */
			}
			log.info("ESPAD finished processing event descriptors from "
					+ eventDescriptorsResourceName);

		} catch (IOException e1) {
			log.error("ESPAD IOException  " + e1);
			e1.printStackTrace();
		}
		return result;
	}

	public static String convertStreamToString(InputStream is)
			throws IOException {
		/*
		 * To convert the InputStream to String we use the Reader.read(char[]
		 * buffer) method. We iterate until the Reader return -1 which means
		 * there's no more data to read. We use the StringWriter class to
		 * produce the string.
		 */
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString().replaceAll("\\ss\\ss+|\\n|\\r",
					EVENT_DEFS_SPLIT_CHARACTER);
		} else {
			return "";
		}
	}
}