package com.example.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.entity.personaTable;

public class personasDAO {
    private final String jdbcURL = "jdbc:sqlserver://webapiangulardemo.mssql.somee.com:1433;databaseName=webapiangulardemo;encrypt=false";
    private final String jdbcUsername = "aperezNWO_SQLLogin_1";
    private final String jdbcPassword = "aperezNWO_SQLLogin_1";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    // JSON name convention is camelCase
    public List<personaTable> getAllPersons() throws SQLException {
        String sql = """
                    SELECT
                        [Id_Column]            AS id_column
                        ,[Ciudad]              AS ciudad
                        ,[NombreCompleto]      AS nombreCompleto
                        /*,[ProfesionOficio]   AS profesionOficio*/
                    FROM
                        [dbo].[Persona]
                    ORDER BY
                        Id_Column   asc
                """;
        //
        List<personaTable> personas = new ArrayList<>();
        try (Connection connection = getConnection();
                //
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet rs = preparedStatement.executeQuery()) {
            //
            while (rs.next()) {
                //
                long id_Column = rs.getLong("id_column");
                String ciudad = rs.getString("ciudad");
                String nombreCompleto = rs.getString("nombreCompleto");
                // String profesionOficio = rs.getString("profesionOficio");

                //
                personas.add(new personaTable(id_Column, ciudad, nombreCompleto /* , profesionOficio */));
            }
        }
        return personas;
    }
}
