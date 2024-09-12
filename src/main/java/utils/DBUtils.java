package utils;

import beans.Product;
import beans.UserAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    public static UserAccount findUser(Connection conn, String userName, String password) throws SQLException {
        String sql = "SELECT a.User_Name, a.Password, a.Gender FROM User_Account a WHERE a.User_Name = ? AND a.Password = ?";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, userName);
            pstm.setString(2, password);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    String gender = rs.getString("Gender");
                    UserAccount user = new UserAccount();
                    user.setUserName(userName);
                    user.setPassword(password);
                    user.setGender(gender);
                    return user;
                }
            }
        }
        return null;
    }

    public static UserAccount findUser(Connection conn, String userName) throws SQLException {
        String sql = "SELECT a.User_Name, a.Password, a.Gender FROM User_Account a WHERE a.User_Name = ?";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, userName);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    String password = rs.getString("Password");
                    String gender = rs.getString("Gender");
                    UserAccount user = new UserAccount();
                    user.setUserName(userName);
                    user.setPassword(password);
                    user.setGender(gender);
                    return user;
                }
            }
        }
        return null;
    }

    public static List<Product> queryProduct(Connection conn) throws SQLException {
        String sql = "SELECT a.Code, a.Name, a.Price FROM Product a";
        List<Product> list = new ArrayList<>();
        try (PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                String code = rs.getString("Code");
                String name = rs.getString("Name");
                float price = rs.getFloat("Price");
                Product product = new Product();
                product.setCode(code);
                product.setName(name);
                product.setPrice(price);
                list.add(product);
            }
        }
        return list;
    }

    public static Product findProduct(Connection conn, String code) throws SQLException {
        String sql = "SELECT a.Code, a.Name, a.Price FROM Product a WHERE a.Code = ?";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, code);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("Name");
                    float price = rs.getFloat("Price");
                    return new Product(code, name, price);
                }
            }
        }
        return null;
    }

    public static void updateProduct(Connection conn, Product product) throws SQLException {
        String sql = "UPDATE Product SET Name = ?, Price = ? WHERE Code = ?";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, product.getName());
            pstm.setFloat(2, product.getPrice());
            pstm.setString(3, product.getCode());
            pstm.executeUpdate();
        }
    }

    public static void insertProduct(Connection conn, Product product) throws SQLException {
        String sql = "INSERT INTO Product (Code, Name, Price) VALUES (?, ?, ?)";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, product.getCode());
            pstm.setString(2, product.getName());
            pstm.setFloat(3, product.getPrice());
            pstm.executeUpdate();
        }
    }

    public static void deleteProduct(Connection conn, String code) throws SQLException {
        String sql = "DELETE FROM Product WHERE Code = ?";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, code);
            pstm.executeUpdate();
        }
    }
}
