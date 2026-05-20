package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private ConnectionPool connectionPool;

    @Override
    public void init() {
        connectionPool =
                (ConnectionPool) getServletContext()
                        .getAttribute("connectionPool");
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {

            User user = UserMapper.login(
                    email,
                    password,
                    connectionPool
            );

            // SESSION START
            HttpSession session = request.getSession();

            session.setAttribute("currentUser", user);

            response.sendRedirect("index.jsp");

        } catch (DatabaseException e) {

            request.setAttribute("error", e.getMessage());

            request.getRequestDispatcher("login.jsp")
                    .forward(request, response);
        }
    }
}