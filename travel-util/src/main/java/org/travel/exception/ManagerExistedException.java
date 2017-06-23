package org.travel.exception;

@SuppressWarnings("serial")
public class ManagerExistedException extends RuntimeException {
	public ManagerExistedException(String msg) {
		super(msg);
	}
}
