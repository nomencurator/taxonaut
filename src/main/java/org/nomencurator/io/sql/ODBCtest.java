package org.nomencurator.io.sql;

import java.sql.*;

public class ODBCtest {
  public static void main(String[] args) {
    try {
      // load driver class
      Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
      // connect to the database
      Connection con = DriverManager.getConnection("jdbc:odbc:Cryptomonad");
      // create statement object
      Statement stmt = con.createStatement();
      // SQL to be processed
      String sql = "SELECT * FROM NAMED_OBJECTS";
      // execute the query and get the result set
      ResultSet rs = stmt.executeQuery(sql);
      // loop for each row in the result
      while(rs.next()){
        int no = rs.getInt("OBJECT_ID");
        String lang = rs.getString("OBJECT_TYPE");
        String msg = rs.getString("CONTRIBUTOR");
      }
      // disconnect from the DB
      stmt.close();
      con.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
