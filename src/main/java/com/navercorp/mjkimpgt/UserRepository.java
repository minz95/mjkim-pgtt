package com.navercorp.mjkimpgt;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.navercorp.mjkimpgt.User;

public interface UserRepository extends CrudRepository<User, String> {
	User findByToken(@Param("token") String token);
	
	@Modifying
    @Query("UPDATE User u SET u.token = NULL WHERE u.token = :token")
    void deleteToken(@Param("token") String token);
}
