package io.ibax.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
	private Integer id;
	private String account;
	private Boolean locked;

//	public Account() {
//	}

	public Account(String account) {
		this.account = account;
	}

}
