package com.example.bot.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import com.example.bot.entity.Entity;

import javax.sql.DataSource;
import java.util.List;

public class MyRepository  {

    private final JdbcTemplate jdbcTemplate;

    public MyRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void save(Entity entity) {
        String sql = "INSERT INTO crypto (column1, column2) VALUES (?, ?)";
        jdbcTemplate.update(sql, entity.getProperty1(), entity.getProperty2());
    }




    public Entity findById(Long id) {
        String sql = "SELECT * FROM crypto WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new MyRowMapper());
    }


    public List<Entity> findAll() {
        String sql = "SELECT * FROM crypto";
        return jdbcTemplate.query(sql, new MyRowMapper());
    }


    public void update(Entity entity) {
        String sql = "UPDATE crypto SET column1 = ?, column2 = ? WHERE id = ?";
        jdbcTemplate.update(sql, entity.getProperty1(), entity.getProperty2(), entity.getId());
    }


    public void deleteById(Long id) {
        String sql = "DELETE FROM crypto WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
