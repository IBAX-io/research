package io.ibax.block;

/**
 * Basic properties of an instruction in blockBody
 * 
 */
public class InstructionBase {
    /**
     * Instruction operation, additions, deletions, and changes (1, -1, 2)
     */
    private byte operation;
    /**
     * Operation table name
     */
    private String table;
    /**
     * The final json content to be executed
     */
    private String oldJson;
    /**
     * Business id
     */
    private String instructionId;

    public byte getOperation() {
        return operation;
    }

    public void setOperation(byte operation) {
        this.operation = operation;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getOldJson() {
        return oldJson;
    }

    public void setOldJson(String oldJson) {
        this.oldJson = oldJson;
    }

    public String getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(String instructionId) {
        this.instructionId = instructionId;
    }

    @Override
    public String toString() {
        return "InstructionReverse{" +
                "operation=" + operation +
                ", table='" + table + '\'' +
                ", oldJson='" + oldJson + '\'' +
                ", instructionId='" + instructionId + '\'' +
                '}';
    }
}
