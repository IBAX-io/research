package io.ibax.net.base;

import lombok.Getter;

/**
 * Node communication status
 *
 */
public interface ActiveStatus {

    int DIE = 0;
    int ACTIVITY = 1;

    @Getter
    enum Enum {
    	DIE(0), ACTIVITY(1);

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
