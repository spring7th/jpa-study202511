package com.codeit.jpastudy.chap01.repository;

import com.codeit.jpastudy.chap01.entity.Product;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcProductRepository {

    // PostgreSQL 연결 정보
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    // 전통적 JDBC 방식의 INSERT
    public void insertProduct(String name, int price, Product.Category category) {

        // 1. sql을 직접 문자열로 작성합니다.
        String sql = "INSERT INTO product (name, price, category) VALUES(?, ?, ?::category_type)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // 2. 드라이버 커넥터 연동 (데이터베이스 접속 진행)
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

            // 3. SQL 실행을 위한 객체를 받아오기
            pstmt = conn.prepareStatement(sql);

            // 4. ?를 채워서 sql을 완성시키기
            pstmt.setString(1, name);
            pstmt.setInt(2, price);
            pstmt.setString(3, category.toString());

            // 5. 완성된 sql을 실행하는 명령을 내리기.
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // SELECT 전체 조회
    public List<Product> selectAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product ORDER BY id DESC";

        // SELECT는 INSERT, UPDATE, DELETE와는 다르게 sql을 실행하고 나서 후속 조치가 필요합니다. (조회된 내용을 자바 객체로 변환)
        // sql 실행 후 리턴되는 ResultSet 객체를 활용해서 조회 데이터를 자바로 변환합니다.
        try(Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            // 더 이상 조회되는 행이 없을 때까지 반복문을 진행 시키겠다.
            // rs.next()는 메서드 호출 시 데이터를 한 행씩 지목해 줍니다.
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getInt("price"));
                product.setCategory(Product.Category.valueOf(rs.getString("category")));
                product.setCreateAt(rs.getTimestamp("create_at").toLocalDateTime());
                product.setUpdateAt(rs.getTimestamp("update_at").toLocalDateTime());
                products.add(product);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }



}











