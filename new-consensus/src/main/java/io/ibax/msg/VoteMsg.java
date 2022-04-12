package io.ibax.msg;


public class VoteMsg {
    private byte voteType;
    private String hash;
    private int number;
    private String publicKey;
    private boolean agree;

    @Override
    public String toString() {
        return "VoteMsg{" +
                "voteType=" + voteType +
                ", hash='" + hash + '\'' +
                ", number=" + number +
                ", publicKey='" + publicKey + '\'' +
                ", agree=" + agree +
                '}';
    }

    public byte getVoteType() {
        return voteType;
    }

    public void setVoteType(byte voteType) {
        this.voteType = voteType;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public boolean isAgree() {
        return agree;
    }

    public void setAgree(boolean agree) {
        this.agree = agree;
    }
}
