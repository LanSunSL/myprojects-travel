package org.travel.exception;

@SuppressWarnings("serial")
public class LevelNotEnoughException extends RuntimeException {
	public LevelNotEnoughException(String msg) {
		super(msg);
	}
}
