package com.example.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.entity.accessLog;

public class AccessLogDAO {

    private final String jdbcURL = "jdbc:sqlserver://webapiangulardemo.mssql.somee.com:1433;databaseName=webapiangulardemo;encrypt=false";
    private final String jdbcUsername = "aperezNWO_SQLLogin_1";
    private final String jdbcPassword = "aperezNWO_SQLLogin_1";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    // JSON name convention is camelCase
    public List<accessLog> getAllLogs() throws SQLException {
        String sql = """
                SELECT TOP 100
                       AL.[ID_column]     AS id_column
                     , AL.[PageName]      AS pageName
                     , AL.[AccessDate]    AS accessDate
                     , AL.[IpValue]       AS ipValue
                FROM
                    dbo.accessLogs AL
                WHERE
                    AL.[LogType] = 1
                AND
                    (AL.PAGENAME LIKE '%DEMO%'
                and
                    AL.PAGENAME LIKE '%PAGE%')
                AND
                    AL.PAGENAME NOT LIKE '%ERROR%'
                AND
                    AL.PAGENAME  NOT LIKE '%PAGE_DEMO_INDEX%'
                AND
                    UPPER(AL.PAGENAME) NOT LIKE '%CACHE%'
                AND
                    AL.IPVALUE <> '::1'
                order by
                    AL.[ID_column] desc
                """;
        //
        List<accessLog> accessLogs = new ArrayList<>();
        try (Connection connection = getConnection();
                //
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet rs = preparedStatement.executeQuery()) {
            //
            while (rs.next()) {
                //
                long id_column = rs.getLong("id_column");
                String pageName = rs.getString("pageName");
                String accessDate = rs.getString("accessDate");
                String ipValue = rs.getString("ipValue");
                //
                accessLogs.add(new accessLog(id_column, pageName, accessDate, ipValue));
            }
        }
        return accessLogs;
    }

}
