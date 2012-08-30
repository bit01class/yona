package controllers;

import java.util.regex.Pattern;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Constants;
import views.html.login;
import views.html.signup;

public class UserApp extends Controller {
    public static final String SESSION_USERID = "userId";
    public static final String SESSION_USERNAME = "userName";

    public static Result login() {
        return ok(login.render("title.login"));
    }
    
    public static Result logout() {
        session().clear();
        flash(Constants.SUCCESS, "user.logout.success");
        return redirect(routes.Application.index());
    }
    
    public static Result authenticate() {
        User user = form(User.class).bindFromRequest().get();
        if(User.authenticate(user)){
            if(user.id == null) user = User.findByLoginId(user.loginId);
            setUserInfoInSession(user);
            return redirect(routes.Application.index());
        } else {
            flash(Constants.WARNING, "user.login.failed");
            return redirect(routes.UserApp.login());
        }
    }
    
    public static Result signup() {
        return ok(signup.render("title.signup", form(User.class)));
    }
    
    public static Result saveUser() {
        Form<User> newUserForm = form(User.class).bindFromRequest();
        validate(newUserForm);
        if(newUserForm.hasErrors())
            return badRequest(signup.render("title.signup", newUserForm));
        else {
            User user = newUserForm.get();
            user.save();
            setUserInfoInSession(user);
            return redirect(routes.Application.index());
        }
    }

    public static void setUserInfoInSession(User user) {
        session(SESSION_USERID, String.valueOf(user.id));
        session(SESSION_USERNAME, user.name);
    }

    public static User currentUser() {
        String userId = session().get(SESSION_USERID);
        if (userId == null) {
            throw new IllegalStateException("please login");
        }
        return User.findById(Long.valueOf(userId));
    }
    
    public static void validate(Form<User> newUserForm) {
        // loginId가 빈 값이 들어오면 안된다.
        if(newUserForm.field("loginId").value() == "") {
            newUserForm.reject("loginId", "user.wrongloginId.alert");
        }
        
        // password가 빈 값이 들어오면 안된다.
        if(newUserForm.field("password").value() == "") {
            newUserForm.reject("password", "user.wrongPassword.alert");
        }
        
        //중복된 loginId로 가입할 수 없다.
        if(User.isLoginId(newUserForm.field("loginId").value())){
           newUserForm.reject("loginId", "user.loginId.duplicate"); 
        }
    }
}
