package io.ibax.mapper;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Mapper;

import io.ibax.model.Keys;

@Mapper
public interface KeysMapper {

	Keys selectKeysById(BigDecimal id);
}
