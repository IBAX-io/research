package io.ibax.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionInput {

//	Output
	private String ohash;
	private Integer oheight;
	private Long oamount;
	private String oaccount;
	private Boolean otargetable;
	private Long otimestamp;
	private Integer oindex;
	
// 	Transaction
	private String thash;
	private String tfrom;
	private String tto;
	private Long tamount;
	private Long ttimestamp;
	private Integer theight;
	private String tinput;
//	private String toutput;


}
