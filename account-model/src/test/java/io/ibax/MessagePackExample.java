package io.ibax;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePack.PackerConfig;
import org.msgpack.core.MessagePack.UnpackerConfig;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.ExtensionValue;
import org.msgpack.value.FloatValue;
import org.msgpack.value.IntegerValue;
import org.msgpack.value.TimestampValue;
import org.msgpack.value.Value;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

/**
 * This class describes the usage of MessagePack
 */
public class MessagePackExample {
	private MessagePackExample() {
	}

	/**
	 * Basic usage example
	 *
	 * @throws IOException
	 */
	public static void basicUsage() throws IOException {
		// Serialize with MessagePacker.
		// MessageBufferPacker is an optimized version of MessagePacker for packing data into a byte array
		MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
		packer.packInt(1).packString("leo").packArrayHeader(2).packString("xxx-xxxx").packString("yyy-yyyy");
		packer.close(); // Never forget to close (or flush) the buffer

		// Deserialize with MessageUnpacker
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(packer.toByteArray());
		int id = unpacker.unpackInt(); // 1
		String name = unpacker.unpackString(); // "leo"
		int numPhones = unpacker.unpackArrayHeader(); // 2
		String[] phones = new String[numPhones];
		for (int i = 0; i < numPhones; ++i) {
			phones[i] = unpacker.unpackString(); // phones = {"xxx-xxxx", "yyy-yyyy"}
		}
		unpacker.close();

		System.out.println(String.format("id:%d, name:%s, phone:[%s]", id, name, join(phones)));
	}

	private static String join(String[] in) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < in.length; ++i) {
			if (i > 0) {
				s.append(", ");
			}
			s.append(in[i]);
		}
		return s.toString();
	}

	/**
	 * Packing various types of data
	 *
	 * @throws IOException
	 */
	public static void packer() throws IOException {
		// Create a MesagePacker (encoder) instance
		MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();

		// pack (encode) primitive values in message pack format
		packer.packBoolean(true);
		packer.packShort((short) 34);
		packer.packInt(1);
		packer.packLong(33000000000L);

		packer.packFloat(0.1f);
		packer.packDouble(3.14159263);
		packer.packByte((byte) 0x80);

		packer.packNil();

		// pack strings (in UTF-8)
		packer.packString("hello message pack!");

		// [Advanced] write a raw UTF-8 string
		byte[] s = "utf-8 strings".getBytes(MessagePack.UTF8);
		packer.packRawStringHeader(s.length);
		packer.writePayload(s);

		// pack arrays
		int[] arr = new int[] { 3, 5, 1, 0, -1, 255 };
		packer.packArrayHeader(arr.length);
		for (int v : arr) {
			packer.packInt(v);
		}

		// pack map (key -> value) elements
		packer.packMapHeader(2); // the number of (key, value) pairs
		// Put "apple" -> 1
		packer.packString("apple");
		packer.packInt(1);
		// Put "banana" -> 2
		packer.packString("banana");
		packer.packInt(2);

		// pack binary data
		byte[] ba = new byte[] { 1, 2, 3, 4 };
		packer.packBinaryHeader(ba.length);
		packer.writePayload(ba);

		// Write ext type data: https://github.com/msgpack/msgpack/blob/master/spec.md#ext-format-family
		byte[] extData = "custom data type".getBytes(MessagePack.UTF8);
		packer.packExtensionTypeHeader((byte) 1, 10); // type number [0, 127], data byte length
		packer.writePayload(extData);

		// Pack timestamp
		packer.packTimestamp(Instant.now());

		// Succinct syntax for packing
		packer.packInt(1).packString("leo").packArrayHeader(2).packString("xxx-xxxx").packString("yyy-yyyy");
	}

	/**
	 * An example of reading and writing MessagePack data
	 *
	 * @throws IOException
	 */
	public static void readAndWriteFile() throws IOException {
		File tempFile = File.createTempFile("target/tmp", ".txt");
		tempFile.deleteOnExit();

		// Write packed data to a file. No need exists to wrap the file stream with BufferedOutputStream, since MessagePacker has its own buffer
		MessagePacker packer = MessagePack.newDefaultPacker(new FileOutputStream(tempFile));
		packer.packInt(1);
		packer.packString("Hello Message Pack!");
		packer.packArrayHeader(2).packFloat(0.1f).packDouble(0.342);
		packer.close();

		// Read packed data from a file. No need exists to wrap the file stream with an buffer
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(new FileInputStream(tempFile));

		while (unpacker.hasNext()) {
			// [Advanced] You can check the detailed data format with getNextFormat()
			// Here is a list of message pack data format: https://github.com/msgpack/msgpack/blob/master/spec.md#overview
			MessageFormat format = unpacker.getNextFormat();

			// You can also use unpackValue to extract a value of any type
			Value v = unpacker.unpackValue();
			switch (v.getValueType()) {
			case NIL:
				v.isNilValue(); // true
				System.out.println("read nil");
				break;
			case BOOLEAN:
				boolean b = v.asBooleanValue().getBoolean();
				System.out.println("read boolean: " + b);
				break;
			case INTEGER:
				IntegerValue iv = v.asIntegerValue();
				if (iv.isInIntRange()) {
					int i = iv.toInt();
					System.out.println("read int: " + i);
				} else if (iv.isInLongRange()) {
					long l = iv.toLong();
					System.out.println("read long: " + l);
				} else {
					BigInteger i = iv.toBigInteger();
					System.out.println("read long: " + i);
				}
				break;
			case FLOAT:
				FloatValue fv = v.asFloatValue();
				float f = fv.toFloat(); // use as float
				double d = fv.toDouble(); // use as double
				System.out.println("read float: " + d);
				break;
			case STRING:
				String s = v.asStringValue().asString();
				System.out.println("read string: " + s);
				break;
			case BINARY:
				byte[] mb = v.asBinaryValue().asByteArray();
				System.out.println("read binary: size=" + mb.length);
				break;
			case ARRAY:
				ArrayValue a = v.asArrayValue();
				for (Value e : a) {
					System.out.println("read array element: " + e);
				}
				break;
			case EXTENSION:
				ExtensionValue ev = v.asExtensionValue();
				if (ev.isTimestampValue()) {
					// Reading the value as a timestamp
					TimestampValue ts = ev.asTimestampValue();
					Instant tsValue = ts.toInstant();
				} else {
					byte extType = ev.getType();
					byte[] extValue = ev.getData();
				}
				break;
			}
		}
	}

	/**
	 * Example of using custom MessagePack configuration
	 *
	 * @throws IOException
	 */
	public static void configuration() throws IOException {
		MessageBufferPacker packer = new PackerConfig().withSmallStringOptimizationThreshold(256) // String
				.newBufferPacker();

		packer.packInt(10);
		packer.packBoolean(true);
		packer.close();

		// Unpack data
		byte[] packedData = packer.toByteArray();
		MessageUnpacker unpacker = new UnpackerConfig().withStringDecoderBufferSize(16 * 1024) // If your data contains many large strings (the default is 8k)
				.newUnpacker(packedData);
		int i = unpacker.unpackInt(); // 10
		boolean b = unpacker.unpackBoolean(); // true
		unpacker.close();
	}

	public static void main(String[] args) throws Exception {
		MessageBufferPacker msgpack = MessagePack.newDefaultBufferPacker();
//		msgpack.

		Map<String, Object> body = Maps.newHashMap();

//		byte[] data = msgpack.write(body);
//		byte[] hash = getSHA(data, "SHA-256");
//		MessagePack messagePack=new MessagePack()

// link:		https://github.com/msgpack/msgpack-java/tree/develop/msgpack-jackson
//		Basic usage
		{
//		Serialization/Deserialization of POJO
			// Instantiate ObjectMapper for MessagePack
			ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());

			// Serialize a Java object to byte array
			ExamplePojo pojo = new ExamplePojo("komamitsu");
			byte[] bytes = objectMapper.writeValueAsBytes(pojo);

			// Deserialize the byte array to a Java object
			ExamplePojo deserialized = objectMapper.readValue(bytes, ExamplePojo.class);
			System.out.println(deserialized.getName()); // => komamitsu
		}
		{
//		Serialization/Deserialization of List

			// Instantiate ObjectMapper for MessagePack
			ObjectMapper objectMapper = new MessagePackMapper();

			// Serialize a List to byte array
			List<Object> list = new ArrayList<>();
			list.add("Foo");
			list.add("Bar");
			list.add(42);
			byte[] bytes = objectMapper.writeValueAsBytes(list);

			// Deserialize the byte array to a List
			List<Object> deserialized = objectMapper.readValue(bytes, new TypeReference<List<Object>>() {
			});
			System.out.println(deserialized); // => [Foo, Bar, 42]
		}
		{
//		Serialization/Deserialization of Map
			// Instantiate ObjectMapper for MessagePack
			ObjectMapper objectMapper = new MessagePackMapper();

			// Serialize a Map to byte array
			Map<String, Object> map = new HashMap<>();
			map.put("name", "komamitsu");
			map.put("age", 42);
			byte[] bytes = objectMapper.writeValueAsBytes(map);
			{
				// Deserialize the byte array to a Map
				Map<String, Object> deserialized = objectMapper.readValue(bytes, new TypeReference<Map<String, Object>>() {
				});
				System.out.println(deserialized); // => {name=komamitsu, age=42}

			}
			{
				ExamplePojo deserialized = objectMapper.readValue(bytes, ExamplePojo.class);
				System.out.println(deserialized);// => {name=komamitsu, age=42}
			}

		}
		{
//		Example of Serialization/Deserialization over multiple languages

//		Java
			ObjectMapper objectMapper = new MessagePackMapper();
			// Serialize
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("foo", "hello");
			obj.put("bar", "world");
			byte[] bs = objectMapper.writeValueAsBytes(obj);
			// bs => [-126, -93, 102, 111, 111, -91, 104, 101, 108, 108, 111,
			// -93, 98, 97, 114, -91, 119, 111, 114, 108, 100]
		}
	}
}