package org.coffeebag.TestUser;

import org.coffeebag.annotations.Access;
import org.coffeebag.annotations.Visibility;

@Access(level = Visibility.PUBLIC)
public class App
{
	
	public int canneloni;
	public int rigatoni;
	
	@Access(level = Visibility.SCOPED, scope = "org.coffeebag")
    public void sparkle() {
    	
    }
    
    public void squiggle() {
    	
    }
    
    public void lasagna() {
    	
    }
    
    public void spaghetti(int count) {
    	final int doubleCount = count * 2 + 3;
    }
}
