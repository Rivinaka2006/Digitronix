package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Role;
import hibernate.User;
import hibernate.UserStatus;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "Register", urlPatterns = {"/Register"})
public class Register extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject user = gson.fromJson(request.getReader(), JsonObject.class);

        String userName = user.get("userName").getAsString();
        final String email = user.get("email").getAsString();
        String password = user.get("password").getAsString();
        String hashedNewPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (userName.isEmpty()) {
            responseObject.addProperty("message", "Username cannot be empty");
        } else if (email.isEmpty()) {
            responseObject.addProperty("message", "email cannot be empty");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Please enter valid email");
        } else if (password.isEmpty()) {
            responseObject.addProperty("message", "password cannot be empty");
        } else if (!Util.isPasswordValid(password)) {
            responseObject.addProperty("message", "The password must contain at least a uppercase, lowercase, "
                    + "number, special characters and to be minimum eight character long");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria criteria = s.createCriteria(User.class); //finding whether the user is already registered
            criteria.add(Restrictions.eq("email", email));

            if (!criteria.list().isEmpty()) {  //if there is a user with entered email
                responseObject.addProperty("message", "User with this email already exists");
            } else {

                Role roleId = (Role) s.get(Role.class, 2);
                UserStatus statusId = (UserStatus) s.get(UserStatus.class, 1);

                User u = new User();
                u.setEmail(email);
                u.setUsername(userName);
                u.setPassword(hashedNewPassword);
                u.setRole(roleId);
                u.setStatus(statusId);

                //generate verification code
                final String verificationCode = Util.generateCode();
                u.setVerification(verificationCode);
                //generate verification code

                s.save(u);
                s.beginTransaction().commit();

                //send email
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Mail.sendMail(email, "Digitronix-Verification", "<h1>" + verificationCode + "</h1>");
                    }
                }).start();
                //send email

                //session management
                HttpSession ses = request.getSession();
                ses.setAttribute("email", email);
                //session management

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Registration success. Please check your email for the verification code");
            }
            s.close();
        }
        //sending response
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }
}
