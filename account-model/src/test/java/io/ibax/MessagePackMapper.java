//
package io.ibax;

import org.msgpack.jackson.dataformat.MessagePackFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;

public class MessagePackMapper extends ObjectMapper {
	private static final long serialVersionUID = 3L;

	public static class Builder extends MapperBuilder<MessagePackMapper, Builder> {
		public Builder(MessagePackMapper m) {
			super(m);
		}
	}

	public MessagePackMapper() {
		this(new MessagePackFactory());
	}

	public MessagePackMapper(MessagePackFactory f) {
		super(f);
	}

	public static Builder builder() {
		return new Builder(new MessagePackMapper());
	}

	public static Builder builder(MessagePackFactory f) {
		return new Builder(new MessagePackMapper(f));
	}
}
