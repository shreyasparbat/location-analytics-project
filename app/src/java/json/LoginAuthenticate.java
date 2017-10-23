/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.dao.LoginDAO;

/**
 *
 * @author Joleen Mok
 */
@WebServlet(name = "LoginAuthenticate", urlPatterns = {"/json/authenticate"})
public class LoginAuthenticate extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *  
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonOutput = new JsonObject();

        String userName = request.getParameter("username");
        String password = request.getParameter("password");
        LoginDAO loginDao = new LoginDAO(); //creating object for LoginDao. This class contains main logic of the application.
        String userValidate = loginDao.authenticateUser(userName, password); //Calling authenticateUser function
        if (userValidate.equals("Invalid user credentials")) //If function returns success string then user will be rooted to Home page
        {
            //fails 
            jsonOutput.addProperty("status", "error");
            jsonOutput.addProperty("error", "invalid username/password"); 

        } else {
            //success 
            jsonOutput.addProperty("status", "success"); 
            jsonOutput.addProperty("token", JWTUtility.sign("secret", userName));   
        }
        //convert gson to a string and print out output  
        String prettyJson = gson.toJson(jsonOutput);
        out.println(prettyJson);

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
