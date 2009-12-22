package org.opencredo.esper.sample;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * A listener used for testing and sample purposes.
 * 
 * @author Russ Miles (russell.miles@opencredo.com)
 * 
 */
public class CallRecordingListener implements UpdateListener {
	
	private int numberOfTimesInvoked = 0;
	
	public int getNumberOfTimesInvoked() {
		return numberOfTimesInvoked;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.espertech.esper.client.UpdateListener#update(com.espertech.esper.
	 * client.EventBean[], com.espertech.esper.client.EventBean[])
	 */
	public void update(EventBean[] eventBeans, EventBean[] eventBeans1) {
		this.numberOfTimesInvoked++;
	}
	
	public void updateFromSIChannel(Object event) {
		this.numberOfTimesInvoked++;
	}
}