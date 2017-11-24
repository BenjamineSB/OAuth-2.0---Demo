package com.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

//import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

@SpringBootApplication
@RestController
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableResourceServer
public class ResourceServer extends ResourceServerConfigurerAdapter{

    //Basics
    public static void main(String[] args) {
        SpringApplication.run(ResourceServer.class, args);
    }

    private String message = "Hello world!";

    @PreAuthorize("#oauth2.hasScope('dbook_public_read')")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Map<String, String> home() {
        return Collections.singletonMap("message", message);
    }

    @PreAuthorize("#oauth2.hasScope('dbook_public_write')")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public void updateMessage(@RequestBody String message) {
        this.message = message;
    }

    @PreAuthorize("#oauth2.hasScope('dbook_user_read')")
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Map<String, String> user(Principal user) {
        return Collections.singletonMap("message", "user is: " + user.toString());
    }


    //Configured for auth_code
    /*
    @Override // [3]
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                // Just for laughs, apply OAuth protection to only 2 resources
                .requestMatchers().antMatchers("/","/admin/beans").and()
                .authorizeRequests()
                .anyRequest().access("#oauth2.hasScope('resource-server-read')"); //[4]
        // @formatter:on
    }
    */
    /*
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(RESOURCE_ID);
    }
    */



    //Own Func
    UserData userData = new UserData();
    private static final String SUCCESS_RESULT="success";
    private static final String FAILURE_RESULT="failure";

    @PreAuthorize("#oauth2.hasScope('dbook_user_read')")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<User> getUsers(){
        return userData.getAllUsers();
    }

    @PreAuthorize("#oauth2.hasScope('dbook_user_read')")
    @RequestMapping(value = "/users/{userid}", method = RequestMethod.GET)
    public User getUser(@PathVariable("userid") int userid){

        return userData.getUser(userid);
    }

    @PreAuthorize("#oauth2.hasScope('dbook_user_write')")
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String createUser(@FormParam("id") int id,
                             @FormParam("name") String name,
                             @FormParam("profession") String profession,
                             @Context HttpServletResponse servletResponse) throws IOException{
        User user = new User(id, name, profession);
        int result = userData.addUser(user);
        if(result == 1){
            return SUCCESS_RESULT;
        }
        return FAILURE_RESULT;
    }

    @PreAuthorize("#oauth2.hasScope('dbook_user_write')")
    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateUser(@FormParam("id") int id,
                             @FormParam("name") String name,
                             @FormParam("profession") String profession,
                             @Context HttpServletResponse servletResponse) throws IOException{
        User user = new User(id, name, profession);
        int result = userData.updateUser(user);
        if(result == 1){
            return SUCCESS_RESULT;
        }
        return FAILURE_RESULT;
    }

    @PreAuthorize("#oauth2.hasScope('dbook_user_write')")
    @RequestMapping(value = "/users", method = RequestMethod.DELETE)
    public String deleteUser(@PathVariable("userid") int userid){
        int result = userData.deleteUser(userid);
        if(result == 1){
            return SUCCESS_RESULT;
        }
        return FAILURE_RESULT;
    }

    @PreAuthorize("#oauth2.hasScope('dbook_user_read')")
    @RequestMapping(value = "/users", method = RequestMethod.OPTIONS)
    public String getSupportedOperations(){

        return "GET, PUT, POST, DELETE";
    }

}