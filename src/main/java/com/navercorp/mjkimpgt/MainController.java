package com.navercorp.mjkimpgt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.navercorp.mjkimpgt.UserService;

@Controller
public class MainController {
	HashMap<String, Integer> responseCode = new HashMap<String, Integer>();
	
	MainController() throws Exception {
		responseCode.put("success", 0);
		responseCode.put("fail", 1);
	}
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(11);
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@PostMapping(path="/user")
	public @ResponseBody String addNewUser (@RequestParam String userId
			, @RequestParam String name
			, @RequestParam String password) throws JsonProcessingException {
	
		System.out.println("??");
		System.out.println(password);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		
		String encodePass = passwordEncoder.encode(password);
		System.out.println(encodePass);
		
		if (!userService.addUser(userId, name, encodePass)) {
			node.put("responseCode", 1);
			return mapper.writeValueAsString(node);
		}
		
		node.put("responseCode", 0);
		return mapper.writeValueAsString(node);
	}

	@PostMapping(path="/login")
	public @ResponseBody String userLogin(@RequestParam String userId
			, @RequestParam String password) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		
		User user = userRepository.findById(userId).get();
		if (!passwordEncoder.matches(password, user.getPassword())) {
			node.put("responseCode", 1);
			node.put("token", "");
			return mapper.writeValueAsString(node);
		}
		
		String tok = jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
		node.put("responseCode", 0);
		node.put("token", tok);
	    return mapper.writeValueAsString(node);
	}
	
	@DeleteMapping(path="/logout")
	public @ResponseBody String userLogout(@RequestParam String token) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		
		String result = userService.logOut(token);
		if (result != null) {
			node.put("responseCode", 0);
			node.put("userId", result);
			return mapper.writeValueAsString(node);
		}
		
		node.put("responseCode", 0);
		node.put("userId", "");
		return mapper.writeValueAsString(node);
	}
	
	@DeleteMapping(path="/user")
	public @ResponseBody String userDelete(@RequestParam String token) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		if (userService.deleteUser(token)) {
			node.put("responseCode", 0);
			return mapper.writeValueAsString(node);
		}
		
		node.put("responseCode", 1);
		return mapper.writeValueAsString(node);
	}
	
	@PutMapping(path="/user")
	public @ResponseBody String userUpdate(@RequestParam String token,
			@RequestParam String name) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		if (userService.updateUserInfo(token, name)) {
			node.put("responseCode", 0);
			return mapper.writeValueAsString(node);
		}
		else {
			node.put("responseCode", 1);
			return mapper.writeValueAsString(node);
		}
	}
	
	@GetMapping(path="/user")
	public @ResponseBody String getUser(@RequestParam String token) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, String>> userList = userService.getUserList(false);
		ObjectNode node = mapper.createObjectNode();
		node.put("responseCode", 0);
		ArrayNode arrayNode = node.putArray("users");
		
		for (Map<String, String> userMap: userList) {
			arrayNode.add(mapper.convertValue(userMap, ObjectNode.class));
		}
		
		return mapper.writeValueAsString(node);
	}

	@GetMapping(path="/all")
	public @ResponseBody Iterable<User> getAllUsers() {
		return userRepository.findAll();
	}
	
}
