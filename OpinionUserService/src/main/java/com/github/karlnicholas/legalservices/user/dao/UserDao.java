package com.github.karlnicholas.legalservices.user.dao;

import com.github.karlnicholas.legalservices.user.model.ApplicationUser;
import com.github.karlnicholas.legalservices.user.model.ERole;
import com.github.karlnicholas.legalservices.user.model.Role;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
/*
create table user (id bigint not null auto_increment
, createdate datetime
, email varchar(255)
, emailupdates bit not null
, firstname varchar(255)
, lastname varchar(255)
, locale varchar(255)
, optout bit not null
, optoutkey varchar(255)
, password varchar(255)
, startverify bit not null
, titles tinyblob
, updatedate datetime
, verified bit not null
, verifycount integer not null
, verifyerrors integer not null
, verifykey varchar(255)
, welcomeerrors integer not null
, welcomed bit not null
, primary key (id)) engine=InnoDB;

 */

@Service
public class UserDao {
    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<ApplicationUser> findByEmail(String email) {
        Optional<ApplicationUser> optionalApplicationUser = Optional.ofNullable(
			jdbcTemplate.query("select id, createdate, email, emailupdates, firstname, lastname, locale, optout, optoutkey, password, startverify, titles, updatedate, verified, verifycount, verifyerrors, verifykey, welcomeerrors, welcomed from user where email = ?",
				ps -> ps.setString(1, email),
				rs -> {
				rs.next();
				String password = rs.getString(10);
				Locale locale = new Locale(rs.getString(7));
				ApplicationUser user = new ApplicationUser(email, password, locale, new HashSet<>());
				user.setId(rs.getLong(1));
				user.setCreateDate(((LocalDateTime) rs.getObject(2)).toLocalDate());
				user.setEmailUpdates(rs.getBoolean(4));
				user.setFirstName(rs.getString(5));
				user.setLastName(rs.getString(6));
				user.setOptout(rs.getBoolean(8));
				user.setOptoutKey(rs.getString(9));
				user.setStartVerify(rs.getBoolean(11));
				// handle titles.
				ByteArrayInputStream bis = new ByteArrayInputStream(rs.getBytes(12));
				try (ObjectInput in = new ObjectInputStream(bis)) {
					user.setTitles((String[]) in.readObject());
				} catch (IOException | ClassNotFoundException ex) {
					ex.printStackTrace();
				}
				//
				user.setUpdateDate(((LocalDateTime) rs.getObject(13)).toLocalDate());
				user.setVerified(rs.getBoolean(14));
				user.setVerifyCount(rs.getInt(15));
				user.setVerifyErrors(rs.getInt(16));
				user.setVerifyKey(rs.getString(17));
				user.setWelcomeErrors(rs.getInt(18));
				user.setWelcomed(rs.getBoolean(19));
				return user;
			})
        );
        if (optionalApplicationUser.isPresent()) {
            optionalApplicationUser.get().getRoles().addAll(jdbcTemplate.query("select r.* from user_roles ur join role r on ur.roles_id = r.id where ur.user_id = ?",
                    ps -> {
                        ps.setLong(1, optionalApplicationUser.get().getId());
                    },
                    (rs, rowNum) -> {
                        Role role = new Role(ERole.valueOf(rs.getString(2)));
                        role.setId(rs.getLong(1));
                        return role;
                    }));
        }
        return optionalApplicationUser;
    }

    public boolean existsByEmail(String email) {
        // TODO Auto-generated method stub
        return false;
    }

    public Long countByEmail(String email) {
        // TODO: implement stub method
        return null;

    }

    public List<ApplicationUser> findUnverified() {
        // TODO: implement stub method
        return null;

    }

    public List<ApplicationUser> findUnwelcomed() {
        // TODO: implement stub method
        return null;

    }

    public Long count() {
        // TODO Auto-generated method stub
        return null;
    }

    @Transactional
    public void insert(ApplicationUser user) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((conn) -> {
            PreparedStatement ps = conn.prepareStatement(
                    "insert into user(createdate, email, emailupdates, firstname, lastname, locale, optout, optoutkey, password, startverify, titles, updatedate, verified, verifycount, verifyerrors, verifykey, welcomeerrors, welcomed) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, user.getCreateDate());
            ps.setString(2, user.getEmail());
            ps.setBoolean(3, user.isEmailUpdates());
            ps.setString(4, user.getFirstName());
            ps.setString(5, user.getLastName());
            ps.setString(6, user.getLocale().toString());
            ps.setBoolean(7, user.isOptout());
            ps.setString(8, user.getOptoutKey());
            ps.setString(9, user.getPassword());
            ps.setBoolean(10, user.isStartVerify());
            // serialize array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
                out.writeObject(user.getTitles());
                out.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            ps.setBytes(11, bos.toByteArray());
            //
            ps.setObject(12, user.getUpdateDate());
            ps.setBoolean(13, user.isVerified());
            ps.setInt(14, user.getVerifyCount());
            ps.setInt(15, user.getVerifyErrors());
            ps.setString(16, user.getVerifyKey());
            ps.setInt(17, user.getWelcomeErrors());
            ps.setBoolean(18, user.isWelcomed());
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        List<Role> roles = jdbcTemplate.queryForStream("select * from role", (rs, i) -> new Role(rs.getLong(1), rs.getString(2))).collect(Collectors.toList());
        List<Role> userRoles = user.getRoles().stream()
                .map(roles::indexOf)
                .filter(i -> i.compareTo(0) >= 0)
                .map(roles::get)
                .collect(Collectors.toList());
        jdbcTemplate.batchUpdate("insert into user_roles(user_id, roles_id) values (?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, user.getId());
                ps.setLong(2, userRoles.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return userRoles.size();
            }
        });
    }

    public void deleteById(Long id) {
        // TODO Auto-generated method stub

    }

    public ApplicationUser getOne(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<ApplicationUser> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

}
