package com.navercorp.mjkimpgt;

import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService{
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(11);
	
	public String getUserName(String userId) {
		User user = userRepository.findById(userId).get();
		return user.getName();
	}
	
//	public String getUserToken(String userId) {
//		User user = userRepository.findById(userId).get();
//		return user.getToken();
//	}
//	
//	public Boolean isLogin(String token) {
//		User user = userRepository.findByToken(token);
//		return user.getToken() == token;
//	}
	
	public List<Map<String, String>> getUserList(Boolean isLogin) {
		Iterable<User> users = userRepository.findAll();
		List<Map<String, String>> userList = new ArrayList<Map<String, String>>();
		for (User u: users) {
//			if (isLogin == true) {
//				if (u.getToken() == null)
//					continue;
//			}
			
			Map<String, String> userMap = new HashMap<String, String>();
			userMap.put("userId", u.getId());
			userMap.put("name", u.getName());
			userList.add(userMap);
		}
		
		return userList;
	}
	
	public Boolean updateUserInfo(String token, String name) {
		try {
			User user = userRepository.findByToken(token);
			user.setName(name);
			userRepository.save(user);
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	public Boolean addUser(String userId, String name, String password) {
		try {
			User n = new User();
			n.setId(userId);
			n.setName(name);
			n.setPassword(password);
			n.setRoles(Collections.singletonList("ROLE_USER"));
//			n.setToken("");
			userRepository.save(n);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public Boolean deleteUser(String token) {
		try {
			userRepository.deleteById(token);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	public Boolean logIn(String userId, String password) {
		try {
			User u = userRepository.findById(userId).get();

			if (!passwordEncoder.matches(password, u.getPassword())) {
				return false;
			}
			
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public String logOut(String token) {
		if (jwtTokenProvider.validateToken(token)) {
			String userId = userRepository.findByToken(token).getId();
			userRepository.deleteToken(token);
			return userId;
		} else {
			return null;
		}
	}
	
	public UserDetails loadUserByUsername(String userId) {
        try {
			return userRepository.findById(userId).orElseThrow(Exception::new);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	
}
