package io.ibax.mapper;
import java.util.Collection;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import io.ibax.data.City;

@Mapper
public interface CityMapper {

	@Insert("INSERT INTO city (name, state, country) VALUES(#{name}, #{state}, #{country})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void insert(City city);

	@Select("SELECT id, name, state, country FROM city ")
	Collection<City> findAll();
}
