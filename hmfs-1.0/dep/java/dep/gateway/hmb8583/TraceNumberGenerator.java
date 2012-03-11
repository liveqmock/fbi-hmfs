package dep.gateway.hmb8583;

public interface TraceNumberGenerator {

	public int nextTrace();

	public int getLastTrace();

}
