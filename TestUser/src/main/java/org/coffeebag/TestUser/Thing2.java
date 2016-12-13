package org.coffeebag.TestUser;

import java.util.Collections;
import java.util.List;

import org.coffeebag.TestUser.p2.Collision;

import static org.coffeebag.TestUser.p2.Collision.name;

public class Thing2 {
	public Thing2() {
		// Modify Collision.name
		// Both the field and the method are imported
		name = 3;
		name();	
		
		// Use Collision, which is private
		new Collision();
		
		// Use fields of App
		final App app = new App();
		app.canneloni = 37;
		app.rigatoni = 314;
	}
	
	private void reticulate() {
		discombobulate("hydrogen");
	}
	
	private <T extends CharSequence> char discombobulate(T thing) {
		return thing.charAt(0);
	}
	
	private <T extends java.time.zone.ZoneRulesException> void defenestrate(T thing) {
		
		// Use a fully-qualified class
		final java.awt.geom.Point2D.Double point = new java.awt.geom.Point2D.Double(2, 3);
		// Use an imported class
		final List<Object> things = Collections.emptyList();
		
	}
}
