package com.example.bot.repository;

import com.example.bot.entity.Entity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MyRowMapper implements RowMapper<Entity> {

    @Override
    public Entity mapRow(ResultSet rs, int rowNum) throws SQLException {
        Entity entity = new Entity();
        entity.setId(rs.getLong("id"));
        entity.setProperty1(rs.getString("property1"));
        entity.setProperty2(rs.getString("property2"));
        return entity;
    }
}
