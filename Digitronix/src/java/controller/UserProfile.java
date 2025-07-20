package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "UserProfile", urlPatterns = {"/UserProfile"})
public class UserProfile extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession ses = request.getSession(false);

        if (ses != null && ses.getAttribute("user") != null) {
            User user = (User) ses.getAttribute("user");
            JsonObject responseObject = new JsonObject();
            responseObject.addProperty("userName", user.getUsername());
            responseObject.addProperty("firstName", user.getFirstName());
            responseObject.addProperty("lastName", user.getLastName());
            responseObject.addProperty("email", user.getEmail());

            Gson gson = new Gson();
            String toJson = gson.toJson(responseObject);
            response.setContentType("application/json");
            response.getWriter().write(toJson);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject userData = gson.fromJson(request.getReader(), JsonObject.class);

        String firstName = userData.get("firstName").getAsString();
        String lastName = userData.get("lastName").getAsString();
        String userName = userData.get("userName").getAsString();
        String email = userData.get("email").getAsString();
        String currentPassword = userData.get("currentPassword").getAsString();
        String newPassword = userData.get("newPassword").getAsString();
        String confirmPassword = userData.get("confirmPassword").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        HttpSession ses = request.getSession();
        User user = (User) ses.getAttribute("user");

        if (firstName.isEmpty()) {
            responseObject.addProperty("message", "First name is required.");
        } else if (lastName.isEmpty()) {
            responseObject.addProperty("message", "Last name is required.");
        } else if (userName.isEmpty() || userName.length() < 3) {
            responseObject.addProperty("message", "Username must be at least 3 characters.");
        } else if (email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            responseObject.addProperty("message", "Valid email is required.");
        } else {
            boolean wantsToChangePassword = !currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty();

            if (wantsToChangePassword) {
                if (currentPassword.isEmpty()) {
                    responseObject.addProperty("message", "Current password is required to change your password.");
                } else if (!BCrypt.checkpw(currentPassword, user.getPassword())) {
                    responseObject.addProperty("message", "Current password is incorrect.");
                } else if (newPassword.length() < 6) {
                    responseObject.addProperty("message", "New password must be at least 6 characters.");
                } else if (!newPassword.equals(confirmPassword)) {
                    responseObject.addProperty("message", "New password and confirm password do not match.");
                } else {
                    // All password validations passed – update the password
                    String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                    user.setPassword(hashedNewPassword);

                    responseObject.addProperty("message", "Profile and password updated successfully.");
                    responseObject.addProperty("status", true);
                }
            } else {
                responseObject.addProperty("message", "Profile updated successfully.");
                responseObject.addProperty("status", true);
            }

            // Update common fields (always)
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(userName);
            user.setEmail(email);

            // Update DB
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            s.beginTransaction();
            s.merge(user);
            s.getTransaction().commit();
            s.close();

            // Update session user
            ses.setAttribute("user", user);
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }
}
