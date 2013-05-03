import java.io.FileInputStream;
import java.util.Properties;

public class ServerProperties {

    private String server;
    private String usr;
    private String pwd;
    private String dbName;
    private String backUpDirectory;
    public static int cpt = 1;
    public static long size = 0;

    public ServerProperties() {

        this.loadProperties();

    }

    /**
     * @param args
     */

    public String getServer() {
        return this.server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUsr() {
        return this.usr;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public String getPwd() {
        return this.pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getDbName() {
        return this.dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getBackUpDirectory() {
        return this.backUpDirectory;
    }

    public void setBackUpDirectory(String backUpDirectory) {
        this.backUpDirectory = backUpDirectory;
    }

    public void loadProperties() {

        try {
            // chargement des propri�t�s
            java.util.Properties prop = new Properties();
            FileInputStream input = new FileInputStream("properties/server.properties"); //$NON-NLS-1$
            prop.load(input);
            this.setServer(prop.getProperty("server"));
            this.setUsr(prop.getProperty("usr"));
            this.setPwd(prop.getProperty("pwd"));
            this.setDbName(prop.getProperty("dbName"));
            this.setBackUpDirectory(prop.getProperty("backUpDirectory"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
