package org.something.delicious;


class SamePackage {
	int zymurgy;
	
	public void zzyzx() {
		final OtherClass other = new OtherClass();
		other.penne = 128;
	}
}

class OtherClass {
	int penne;
	
	public void salami() {
		final SamePackage same = new SamePackage();
		same.zymurgy= 8192;
	}
}
