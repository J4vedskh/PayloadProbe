package com.ofss.mock.rest.xml;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import org.glassfish.jersey.jackson.JacksonFeature;

public class RestApplication extends Application {
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(XmlController.class);
		classes.add(JacksonFeature.class);
		return classes;
	}
}
