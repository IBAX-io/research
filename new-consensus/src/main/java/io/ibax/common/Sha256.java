package io.ibax.common;

import cn.hutool.crypto.digest.DigestUtil;

public class Sha256 {
    public static String sha256(String input) {
        return DigestUtil.sha256Hex(input);
    }

}
