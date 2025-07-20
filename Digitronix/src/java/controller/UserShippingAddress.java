package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.BillingAddress;
import hibernate.City;
import hibernate.HibernateUtil;
import hibernate.ShippingAddress;
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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "UserShippingAddress", urlPatterns = {"/UserShippingAddress"})
public class UserShippingAddress extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession ses = request.getSession(false);

        if (ses != null && ses.getAttribute("user") != null) {
            User user = (User) ses.getAttribute("user");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(ShippingAddress.class);
            c.add(Restrictions.eq("userId", user));

            List<ShippingAddress> shippingList = c.list();
            JsonObject responseObject = new JsonObject();
            responseObject.addProperty("status", false);

            if (!shippingList.isEmpty()) {
                ShippingAddress shipping = shippingList.get(0); // If only one is needed
                responseObject.addProperty("firstName", shipping.getFirstName());
                responseObject.addProperty("lastName", shipping.getLastName());
                responseObject.addProperty("streetAddress", shipping.getAddressLine1());
                responseObject.addProperty("streetAddress1", shipping.getAddressLine2());
                responseObject.addProperty("city", shipping.getCity().getName());
                responseObject.addProperty("postcode", shipping.getPostcodeZip());
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
        JsonObject shippingData = gson.fromJson(request.getReader(), JsonObject.class);

        String firstName = shippingData.get("firstName").getAsString();
        String lastName = shippingData.get("lastName").getAsString();
        String streetAddress = shippingData.get("streetAddress").getAsString();
        String streetAddress1 = shippingData.get("streetAddress1").getAsString();
        int cityId = shippingData.get("cityId").getAsInt();
        String postcode = shippingData.get("postcode").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (firstName == null || firstName.trim().isEmpty()) {
            responseObject.addProperty("message", "First name is required.");
        } else if (lastName == null || lastName.trim().isEmpty()) {
            responseObject.addProperty("message", "Last name is required.");
        } else if (streetAddress == null || streetAddress.trim().isEmpty()) {
            responseObject.addProperty("message", "Address Line 1 is required.");
        } else if (cityId == 0) {
            responseObject.addProperty("message", "Valid city must be selected.");
        } else if (postcode == null || postcode.trim().isEmpty()) {
            responseObject.addProperty("message", "Postcode/ZIP is required.");
        } else if (!postcode.matches("^\\d{4,10}$")) {
            responseObject.addProperty("message", "Postcode must be 4 to 10 digits.");
        } else {

            HttpSession ses = request.getSession();

            if (ses != null && ses.getAttribute("user") != null) {
                User user = (User) ses.getAttribute("user");

                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();

                Criteria criteria = s.createCriteria(ShippingAddress.class);
                criteria.add(Restrictions.eq("userId", user));

                ShippingAddress shipping;

                if (!criteria.list().isEmpty()) {
                    shipping = (ShippingAddress) criteria.list().get(0); // update existing
                } else {
                    shipping = new ShippingAddress(); // create new
                    shipping.setUserId(user);
                }

                shipping.setFirstName(firstName);
                shipping.setLastName(lastName);
                shipping.setAddressLine1(streetAddress);
                shipping.setAddressLine2(streetAddress1);
                shipping.setPostcodeZip(postcode);

                City city = (City) s.load(City.class, cityId);
                shipping.setCity(city);

                s.merge(shipping);
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
