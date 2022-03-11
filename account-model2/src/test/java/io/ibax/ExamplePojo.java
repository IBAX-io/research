package io.ibax;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamplePojo {

	private String name;
	private int age;

	public ExamplePojo(String name) {
		this.name = name;
	}

}
