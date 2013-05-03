/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Database {

    Statement stmt = null;
    Connection cn = null;

    public Database() {
        try {
            this.cn = this.Connexion();
            this.stmt = this.cn.createStatement();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // connection
    public static void echo(String str) {
        System.out.println(str);

    }

    public Connection Connexion() {

        try {
            // pour sql server Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            // cn=DriverManager.getConnection("jdbc:odbc:LocalServer","","");
            Class.forName("org.gjt.mm.mysql.Driver");
            ServerProperties serverProperties = new ServerProperties();
            String url = "jdbc:mysql://" + serverProperties.getServer() + "/" + serverProperties.getDbName(), user = serverProperties.getUsr(), pwd = serverProperties.getPwd();

            this.cn = DriverManager.getConnection(url, user, pwd);
            System.out.println("CONNEXION REUSSIE!");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return this.cn;
    }

    // Mise à jour
    public int ExecuteMAJ(String sql) {
        int n = 0;
        try {

            n = this.stmt.executeUpdate(sql);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return n;
    }

    // executer un select
    public ResultSet ExecuteSelect(String sql) {
        ResultSet rs = null;
        try {
            rs = this.stmt.executeQuery(sql);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return rs;
    }

    // fermer connexion
    public void FermerConnexion() {
        try {
            this.cn.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
