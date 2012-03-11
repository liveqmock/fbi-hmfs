package dep.gateway.hmb8583.impl;

import dep.gateway.hmb8583.TraceNumberGenerator;

/**
 * 流水跟踪号 临时用
 */
public class SimpleTraceGenerator implements TraceNumberGenerator {

	private int value = 0;

	public SimpleTraceGenerator(int initialValue) {
		if (initialValue < 1 || initialValue > 999999) {
			throw new IllegalArgumentException("Initial value must be between 1 and 999999");
		}
		value = initialValue - 1;
	}

	public int getLastTrace() {
		return value;
	}

	public synchronized int nextTrace() {
		value++;
		if (value > 999999) {
			value = 1;
		}
		return value;
	}

}
