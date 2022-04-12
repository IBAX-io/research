package io.ibax.block;

import java.util.List;

/**
 * block body, which stores an array of transactions
 * 
 */
public class BlockBody {
    private List<Instruction> instructions;

    @Override
    public String toString() {
        return "BlockBody{" +
                "instructions=" + instructions +
                '}';
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.instructions = instructions;
    }
}
