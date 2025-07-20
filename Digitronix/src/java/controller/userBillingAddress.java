package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.BillingAddress;
import hibernate.City;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "userBillingAddress", urlPatterns = {"/userBillingAddress"})
public class userBillingAddress extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession ses = request.getSession(false);

        if (ses != null && ses.getAttribute("user") != null) {
            User user = (User) ses.getAttribute("user");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(BillingAddress.class);
            c.add(Restrictions.eq("userId", user));

            List<BillingAddress> billingList = c.list();
            JsonObject responseObject = new JsonObject();
            responseObject.addProperty("status", false);

            if (!billingList.isEmpty()) {
                BillingAddress billing = billingList.get(0); // If only one is needed
                responseObject.addProperty("firstName", billing.getFirstName());
                responseObject.addProperty("lastName", billing.getLastName());
                responseObject.addProperty("streetAddress", billing.getStreetAddress());
                responseObject.addProperty("email", billing.getEmail());
                responseObject.addProperty("mobile", billing.getMobile());
                responseObject.addProperty("city", billing.getCity().getName());
                responseObject.addProperty("status", true);
            }

            s.close();

            Gson gson = new Gson();
            String toJson = gson.toJson(responseObject);
            response.setContentType("application/json");
            response.getWriter().write(toJson);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject billingData = gson.fromJson(request.getReader(), JsonObject.class);

        String firstName = billingData.get("firstName").getAsString();
        String lastName = billingData.get("lastName").getAsString();
        String streetAddress = billingData.get("streetAddress").getAsString();
        int cityId = billingData.get("cityId").getAsInt();
        String mobile = billingData.get("mobile").getAsString();
        String email = billingData.get("email").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (firstName.isEmpty()) {
            responseObject.addProperty("message", "First name is required.");
        } else if (lastName.isEmpty()) {
            responseObject.addProperty("message", "Last name is required.");
        } else if (streetAddress.isEmpty()) {
            responseObject.addProperty("message", "Street address is required.");
        } else if (mobile.isEmpty() || !mobile.matches("^\\d{10}$")) {
            responseObject.addProperty("message", "Mobile must be 10 digits.");
        } else if (email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            responseObject.addProperty("message", "A valid email is required.");
        } else if (cityId == 0) {
            responseObject.addProperty("message", "Please select a city.");
        } else {

            HttpSession ses = request.getSession();

            if (ses != null && ses.getAttribute("user") != null) {
                User user = (User) ses.getAttribute("user");

                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();

                Criteria criteria = s.createCriteria(BillingAddress.class);
                criteria.add(Restrictions.eq("userId", user));

                BillingAddress billing;

                if (!criteria.list().isEmpty()) {
                    billing = (BillingAddress) criteria.list().get(0); // update existing
                } else {
                    billing = new BillingAddress(); // create new
                    billing.setUserId(user);
                }

                billing.setFirstName(firstName);
                billing.setLastName(lastName);
                billing.setStreetAddress(streetAddress);
                billing.setEmail(email);
                billing.setMobile(mobile);

                City city = (City) s.load(City.class, cityId);
                billing.setCity(city);

                s.merge(billing);
                s.beginTransaction().commit();

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Billing address updated successfully!");

            }
        }
        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }
}
