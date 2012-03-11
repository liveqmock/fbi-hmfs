package dep.gateway.hmb8583;

public interface CustomField<T> {

	public T decodeField(String value);

	public String encodeField(T value);

}
