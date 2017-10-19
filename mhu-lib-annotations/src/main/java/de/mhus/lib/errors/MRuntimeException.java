package de.mhus.lib.errors;

import java.util.UUID;

public class MRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private UUID errorId = UUID.randomUUID();
	
	public MRuntimeException(Object ... in) {
		super(MException.argToString(4,in),MException.argToCause(4,in));
	}
	
	@Override
	public String toString() {
		return errorId.toString() + " " + super.toString();
	}
	
	public UUID getId() {
		return errorId;
	}
	
}
