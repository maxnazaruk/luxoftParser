package com.luxoft.linkparser.jdbc;

import com.luxoft.linkparser.entity.Link;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcParserDaoTest {
    JdbcParserDao jdbcParserDao = new JdbcParserDao();

    @BeforeEach
    public void setup() throws SQLException {
        jdbcParserDao.clearTable();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if(!jdbcParserDao.checkTableExists()){
            jdbcParserDao.createLinksTable();
        }
    }

    @Test
    public void testInsertInto() throws SQLException {
        Link link = Link.builder().
                url("url")
                .depth(0)
                .number(0)
                .build();

        jdbcParserDao.add(link);
        List<Link> list = jdbcParserDao.findAll();

        String result = list.get(0).toString();

        assertEquals("Link(id=1, url=url, depth=0, number=0)", result);
    }

    @Test
    public void testInsertIntoMultipleObjects() throws SQLException {
        Link link = Link.builder().
                url("url")
                .depth(0)
                .number(0)
                .build();

        Link link1 = Link.builder().
                url("url1")
                .depth(1)
                .number(1)
                .build();

        Link link2 = Link.builder().
                url("url2")
                .depth(2)
                .number(2)
                .build();

        jdbcParserDao.add(link);
        jdbcParserDao.add(link1);
        jdbcParserDao.add(link2);
        List<Link> list = jdbcParserDao.findAll();

        StringBuilder stringBuilder = new StringBuilder();
        for (Link page : list){
            stringBuilder.append(page);
        }

        assertEquals("Link(id=1, url=url, depth=0, number=0)Link(id=2, url=url1, depth=1, number=1)Link(id=3, url=url2, depth=2, number=2)", stringBuilder.toString());
    }

    @Test
    public void testTableExists() throws SQLException {
        if(!jdbcParserDao.checkTableExists()){
            jdbcParserDao.createLinksTable();
        }
        assertTrue(jdbcParserDao.checkTableExists());
    }

    @Test
    public void testTableDropQuery() throws SQLException {
        if(jdbcParserDao.checkTableExists()){
            jdbcParserDao.dropTable();
        }
        assertFalse(jdbcParserDao.checkTableExists());
    }

    @Test
    public void testClearTable() throws SQLException {
        Link link = Link.builder().
                url("url")
                .depth(0)
                .number(0)
                .build();

        jdbcParserDao.add(link);
        List<Link> list = jdbcParserDao.findAll();

        String result = list.get(0).toString();

        // Verify link was added
        assertEquals("Link(id=1, url=url, depth=0, number=0)", result);

        jdbcParserDao.clearTable();

        list = jdbcParserDao.findAll();

        assertTrue(list.isEmpty());
    }

    @Test
    public void testAutoIncrementResetWithClearTableStatement() throws SQLException {
        Link link = Link.builder().
                url("url")
                .depth(0)
                .number(0)
                .build();

        jdbcParserDao.add(link);
        List<Link> list = jdbcParserDao.findAll();

        int id = list.get(0).getId();

        // Verify link was added
        assertEquals(1, id);

        jdbcParserDao.clearTable();

        link = Link.builder().
                url("url")
                .depth(3)
                .number(3)
                .build();

        jdbcParserDao.add(link);

        int resetId = jdbcParserDao.findAll().get(0).getId();

        assertEquals(1, resetId);
    }

    @Test
    public void testLinkWontBeAddedIfNull() throws SQLException {
        Link link = null;

        jdbcParserDao.add(link);
        List<Link> list = jdbcParserDao.findAll();

        assertTrue(list.isEmpty());
    }
}
