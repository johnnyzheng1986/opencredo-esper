package org.opencredo.esper;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

/**
 * The main workhorse of Esper. The template is configured
 * with a set of statements that query the flow of events.
 * Each statement is then associated with a number of listeners
 * who will be notified when there is a result from the statement.
 * 
 * Once setup the template is then used to inform esper of any 
 * events of interest by calling sendEvent(Object).
 * 
 * @author Russ Miles (russell.miles@opencredo.com)
 *
 */
public final class EsperTemplate implements BeanNameAware, InitializingBean, DisposableBean {
	
    private EPServiceProvider epServiceProvider;
    private EPRuntime epRuntime;
    private String name;
    private Set<EsperStatement> statementBeans = new LinkedHashSet<EsperStatement>();

    /**
     * Add a collection of {@link EsperStatement} to the template.
     * 
     * @param statementBeans
     */
    public void setStatements(EsperStatement... statements) {
		for (EsperStatement statement : statements) {
		    this.addStatement(statement);
		}
    }

    /**
     * Adds an {@link EsperStatement} composite to the template.
     * 
     * @param statement The EsperStatement to add to the template. 
     */
    public void addStatement(EsperStatement statement) {
    	statementBeans.add(statement);
    }

    /**
     * Instructs the template to send an event to Esper.
     * Events are then used to satisfy statements, which then
     * alert listeners.
     * 
     * @param event The event that Esper is to be informed of.
     */
    public void sendEvent(Object event) {
    	epRuntime.sendEvent(event);
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(String name) {
    	this.name = name;
    }
    
    /**
     * Initializes the Esper service provider with the 
     * provided statements and associated listeners.
     * 
     * The provider is giving a unique name that is based on
     * the bean name.
     */
    private void setupEsper() {
		epServiceProvider = EPServiceProviderManager.getProvider(name);
		epRuntime = epServiceProvider.getEPRuntime();
		for (EsperStatement statementBean : statementBeans) {
		    EPStatement epStatement = epServiceProvider.getEPAdministrator().createEPL(statementBean.getEPL());
		    statementBean.setEPStatement(epStatement);
		}
    }
    
    /**
     * Tidies up the Esper service provider, which in turn
     * releases any resources being used by Esper.
     */
    private void cleanup() {
    	epServiceProvider.destroy();
    }

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		this.setupEsper();
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	public void destroy() throws Exception {
		this.cleanup();
	}
}
