import java.util.Map;


class D1<K, V> {
	private Map.Entry<K, V> entry;
	public void d1() {
		
	}
}

class D2<Q extends java.text.spi.DecimalFormatSymbolsProvider> {
	
	public void d2() {
		
	}
}

class D3 {
	public Map<Object, ? super javax.imageio.spi.ImageOutputStreamSpi> d3() {
		return null;
	}
}

class ComplexGeneric {
	public <D extends D1<Object, String>> Object squid(D d, D2<?> e, D3 f) {
		d.d1();
		e.d2();
		f.d3();
		return null;
	}
}
