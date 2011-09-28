package com.espad.ei;

import java.util.List;

import com.espertech.esper.client.EPServiceProvider;

public class CSVEventInitialiser {

    public List<String> sendEventDefinitions(EPServiceProvider epService)
    {
    	return CSVManager.InitAllEventDefinitionsFromCSV(epService);
    }
}
