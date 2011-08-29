package org.aksw.linkedqa.domain;

public interface Factory<I, O> {
	O create(I arg);
}
