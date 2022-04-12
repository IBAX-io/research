package io.ibax.net.body;

import io.ibax.msg.VoteMsg;

/**
 * vote
 *
 */
public class VoteBody extends BaseBody {
    private VoteMsg voteMsg;

    public VoteBody() {
        super();
    }

    public VoteBody(VoteMsg voteMsg) {
        super();
        this.voteMsg = voteMsg;
    }

    public VoteMsg getVoteMsg() {
        return voteMsg;
    }

    public void setVoteMsg(VoteMsg voteMsg) {
        this.voteMsg = voteMsg;
    }
}
