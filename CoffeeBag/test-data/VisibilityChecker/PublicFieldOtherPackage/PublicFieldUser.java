package org.swift.p2;

import org.swift.p1.WithPublicField;

public class PublicFieldUser {
	public PublicFieldUser() {
		final WithPublicField other = new WithPublicField();
		other.persephone = 0xBEEFCAFE2048l;
	}
}