package org.simnation.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class AbstractJavaBean {

	public interface IPropertyName {		
		public String getName();
	}
	
	private final PropertyChangeSupport cs=new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		cs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		cs.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName,PropertyChangeListener listener) {
		cs.addPropertyChangeListener(propertyName,listener);
	}

	public void removePropertyChangeListener(String propertyName,PropertyChangeListener listener) {
		cs.removePropertyChangeListener(propertyName,listener);
	}

	protected void firePropertyChange(String propertyName,Object oldValue,Object newValue) {
		cs.firePropertyChange(propertyName,oldValue,newValue);
	}

}
