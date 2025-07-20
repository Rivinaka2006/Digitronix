package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "Login", urlPatterns = {"/Login"})
public class Login extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject login = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String email = login.get("email").getAsString();
        String password = login.get("password").getAsString();

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email can not be empty");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Please enter valid email");
        } else if (password.isEmpty()) {
            responseObject.addProperty("message", "Password can not be empty");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(User.class);
            c.add(Restrictions.eq("email", email));

            List<User> userList = c.list();

            if (userList.isEmpty()) {
                responseObject.addProperty("message", "Invalid Credentials!");
            } else {
                User u = userList.get(0);

                if (!BCrypt.checkpw(password, u.getPassword())) {
                    responseObject.addProperty("message", "Invalid Credentials!");
                } else {
                    responseObject.addProperty("status", true);

                    HttpSession ses = request.getSession(); // create session

                    if (!u.getVerification().equals("Verified")) {
                        ses.setAttribute("email", email);
                        responseObject.addProperty("message", "1"); // not verified
                    } else {
                        ses.setAttribute("user", u);
                        responseObject.addProperty("message", "2"); // verified
                    }
                }
            }

            s.close();
        }
        //sending response
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }
}
