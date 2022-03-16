package io.ibax.net.base;

import lombok.Getter;

/**
 * Node role
 * @author ak
 *
 */
public interface NodeStatus {

    int FOLLOWER = 0;
    int LEADER = 1;

    @Getter
    enum Enum {
        FOLLOWER(0), LEADER(1);

        Enum(int code) {
            this.code = code;
        }

        int code;

        public static Enum value(int i) {
            for (Enum value : Enum.values()) {
                if (value.code == i) {
                    return value;
                }
            }
            return null;
        }

    }

}
