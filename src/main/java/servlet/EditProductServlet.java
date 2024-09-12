package servlet;

import beans.Product;

import utils.DBUtils;
import utils.MyUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/editProduct" })
public class EditProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public EditProductServlet() {
        super();
    }

    // Hiển thị trang chỉnh sửa sản phẩm.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection conn = MyUtils.getStoredConnection(request);

        if (conn == null) {
            throw new ServletException("No database connection available.");
        }

        String code = request.getParameter("code");

        if (code == null || code.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/productList");
            return;
        }

        Product product = null;
        String errorString = null;

        try {
            product = DBUtils.findProduct(conn, code);
            if (product == null) {
                errorString = "Product not found!";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorString = "Error retrieving product: " + e.getMessage();
        }

        request.setAttribute("errorString", errorString);
        request.setAttribute("product", product);

        RequestDispatcher dispatcher = request.getServletContext()
                .getRequestDispatcher("/WEB-INF/views/editProductView.jsp");
        dispatcher.forward(request, response);
    }

    // Xử lý khi người dùng chỉnh sửa thông tin sản phẩm và nhấp vào Submit.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection conn = MyUtils.getStoredConnection(request);

        if (conn == null) {
            throw new ServletException("No database connection available.");
        }

        String code = request.getParameter("code");
        String name = request.getParameter("name");
        String priceStr = request.getParameter("price");
        float price = 0;
        String errorString = null;

        try {
            price = Float.parseFloat(priceStr);
        } catch (NumberFormatException e) {
            errorString = "Invalid price format!";
        }

        if (code == null || code.trim().isEmpty()) {
            errorString = "Product code is missing!";
        } else if (name == null || name.trim().isEmpty()) {
            errorString = "Product name is missing!";
        }

        Product product = new Product(code, name, price);

        if (errorString == null) {
            try {
                DBUtils.updateProduct(conn, product);
            } catch (SQLException e) {
                e.printStackTrace();
                errorString = "Error updating product: " + e.getMessage();
            }
        }

        request.setAttribute("errorString", errorString);
        request.setAttribute("product", product);

        if (errorString != null) {
            RequestDispatcher dispatcher = request.getServletContext()
                    .getRequestDispatcher("/WEB-INF/views/editProductView.jsp");
            dispatcher.forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/productList");
        }
    }
}
