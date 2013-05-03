import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class BackUpManager extends ServerProperties {

    private String server;
    private String usr;
    private String pwd;
    private String dbName;
    private String backUpDirectory;
    public static int cpt = 1;
    public static long size = 0;
    public static BackUpManager bm = new BackUpManager();

    public BackUpManager() {

        super();

    }

    /**
     * @param args
     */
    @Override
    public String getServer() {
        return this.server;
    }

    @Override
    public void setServer(String server) {
        this.server = server;
    }

    @Override
    public String getUsr() {
        return this.usr;
    }

    @Override
    public void setUsr(String usr) {
        this.usr = usr;
    }

    @Override
    public String getPwd() {
        return this.pwd;
    }

    @Override
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public String getDbName() {
        return this.dbName;
    }

    @Override
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public String getBackUpDirectory() {
        return this.backUpDirectory;
    }

    @Override
    public void setBackUpDirectory(String backUpDirectory) {
        this.backUpDirectory = backUpDirectory;
    }

    public String getStringDate() {
        String format = "yyyyMMddHmmss";

        java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat(format);
        java.util.Date date = new java.util.Date();

        return formater.format(date);
    }

    public String CreateFile() {
        try {
            String fileName = this.getDbName() + "_" + this.getStringDate();
            String path = this.getBackUpDirectory() + "\\backup.bat";
            new File(this.getBackUpDirectory() + "\\" + fileName).mkdir();
            String command = "mysqldump.exe -h " + this.getServer() + " -u " + this.getUsr() + " -p" + this.getPwd() + "  --opt " + this.getDbName() + " > " + this.getBackUpDirectory() + "\\" + fileName + "\\" + fileName + ".sql";
            command += "\r\n" + "attrib +h " + this.getBackUpDirectory() + "\\" + fileName + "\\" + fileName + ".sql";
            command += "\r\n" + "exit";
            FileWriter fw = new FileWriter(path, false);

            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(command);

            bw.flush();
            bw.close();
            fw.close();
            String cmd = "cmd.exe /k start " + path;
            Runtime.getRuntime().exec(cmd);
            System.out.println(command);
            return fileName;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String zip(String filename) {
        String zipFileName = "";
        try {
            cpt = 100;

            zipFileName = this.getBackUpDirectory() + "\\" + filename + "\\" + filename + ".zip";
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(dest);
            FileInputStream fic = null;
            File file = new File(this.getBackUpDirectory() + "\\" + filename + "\\" + filename + ".sql");
            boolean isHidden = file.isHidden();
            while(!file.exists() || !isHidden) {
                SplashProgress.instance().setValue("Creation des fichiers de sauvegarde", (100 - (cpt--)) / 5);
                isHidden = file.isHidden();
            }
            SplashProgress.instance().setValue("Archivage...", (100 - (cpt--)) / 10);
            fic = new FileInputStream(this.getBackUpDirectory() + "\\" + filename + "\\" + filename + ".sql");
            zos.putNextEntry(new ZipEntry(filename + ".sql"));
            Database db = new Database();
            db.ExecuteMAJ("UPDATE backupInfos SET dateOfLastBackup = STR_TO_DATE('" + filename.substring(filename.indexOf("_") + 1) + "','%Y%m%d%H%i%s')");
            for (int c = fic.read(); c != -1; c = fic.read()) {
                zos.write(c);
                SplashProgress.instance().setValue("Archivage en cours...", (100 - (cpt--)) / 100);
            }

            zos.closeEntry();
            fic.close();
            file.delete();
            zos.close();

        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return zipFileName;
    }

    public void unzip(String filename) {
        String zipFileName = "";
        ZipInputStream zipInputStream = null;
        ZipEntry zipEntry = null;
        byte[] buffer = new byte[2048];
        try {
            zipFileName = this.getBackUpDirectory() + "\\" + filename + "\\" + filename + ".zip";
            zipInputStream = new ZipInputStream(new FileInputStream(zipFileName));
            zipEntry = zipInputStream.getNextEntry();
            while(zipEntry != null) {
                if (zipEntry.getName().equalsIgnoreCase(filename + ".sql")) {
                    FileOutputStream file = new FileOutputStream(this.getBackUpDirectory() + "\\" + filename + "\\" + filename + ".sql");
                    int n;

                    while((n = zipInputStream.read(buffer, 0, 2048)) > -1) {
                        file.write(buffer, 0, n);
                        SplashProgress.instance().setValue("Decompression en cours ...", 20 + 40 / n);
                    }

                    file.close();
                    zipInputStream.closeEntry();

                }
                zipEntry = zipInputStream.getNextEntry();
            }

        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    public static void restore(String fileName) {
        try {
            BackUpManager bm = new BackUpManager();
            SplashProgress.instance().start("Restauration de la base de données", 100);
            SplashProgress.instance().setValue("Decompression", 20);
            bm.unzip(fileName);
            SplashProgress.instance().setValue("Restauration ...", 70);
            String path = bm.getBackUpDirectory() + "\\restore.bat";
            new File(bm.getBackUpDirectory() + "\\" + fileName).mkdir();
            String command = "mysql -h " + bm.getServer() + " -u " + bm.getUsr() + " -p" + bm.getPwd() + "  " + bm.getDbName() + " < " + bm.getBackUpDirectory() + "\\" + fileName + "\\" + fileName + ".sql";
            command += "\r\n" + "exit";
            FileWriter fw = new FileWriter(path, false);
            SplashProgress.instance().setValue("Restauration ...", 80);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(command);

            bw.flush();
            bw.close();
            fw.close();
            SplashProgress.instance().setValue("Restauration ...", 90);
            String cmd = "cmd.exe /k start " + path;
            Runtime.getRuntime().exec(cmd);
            SplashProgress.instance().setValue("Restauration ...", 98);
            System.out.println(command);
            SplashProgress.instance().end();
            JOptionPane.showMessageDialog(null, "<html>Restauration effectuée avec succès!</html>", "Notification", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void backup() {
        try {
            BackUpManager bm = new BackUpManager();
            Database db = new Database();
            Desktop desktop = Desktop.getDesktop();

            ResultSet rs = db.ExecuteSelect("SELECT date_format(dateOfLastBackup,'%Y%m%d%H%i%s') -date_format(dateOfLastDBUpdate,'%Y%m%d%H%i%s'),date_format(dateOfLastBackup,'%Y%m%d%H%i%s') FROM backupinfos b;");
            if ((rs.next())) {
                if (rs.getLong(1) > 0) {
                    File file = new File("\\\\" + bm.getBackUpDirectory() + "\\" + bm.getDbName() + "_" + rs.getString(2) + "\\" + bm.getDbName() + "_" + rs.getString(2) + ".zip");
                    if (file.exists()) {
                        JOptionPane.showMessageDialog(null, "<html>Aucune modofication n'a été éffectuée au niveau de la base  depuis la dernière modification.<br>Appuyer sur OK pour voir le fichier de backup.</html>");
                        desktop.open(new File("\\\\" + bm.getBackUpDirectory() + "\\" + bm.getDbName() + "_" + rs.getString(2)));
                        return;
                    }
                }
            }

            SplashProgress.instance().start("Sauvegarde  de la base de données", 500);

            String buFileName = bm.CreateFile();
            if (buFileName != null) {
                bm.zip(buFileName);
            }

            SplashProgress.instance().end();
            JOptionPane.showMessageDialog(null, "<html>Sauvegarde effectuée avec succès!<br>Appuyer sur OK pour voir le fichier de backup.</html>", "Notification", JOptionPane.INFORMATION_MESSAGE);
            desktop.open(new File("\\\\" + bm.getBackUpDirectory() + "\\" + buFileName));

        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static String getBackupLabel(String filename) {
        String stringDate = filename.substring(filename.indexOf("_") + 1);
        String format = "EEEE dd MMMM yyyy à  H:mm:ss";
        SimpleDateFormat formater = new java.text.SimpleDateFormat("dd/MM/yyyy H:mm:ss");
        Date date = null;
        try {
            date = formater.parse(stringDate.substring(6, 8) + "/" + stringDate.substring(4, 6) + "/" + stringDate.substring(0, 4) + " " + stringDate.substring(8, 10) + ":" + stringDate.substring(10, 12) + ":" + stringDate.substring(12, 14));
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        formater = new java.text.SimpleDateFormat(format);

        return formater.format(date);
    }

    public static JTable getBackupTable() {
        JTable table = null;
        try {
            // DefaultTableModel model = new DefaultTableModel();
            File rep = new File(bm.getBackUpDirectory());
            List<String> listOfBackupDirs = new ArrayList<String>(Arrays.asList(rep.list()));
            List<String> listOfBackupFiles = new ArrayList<String>();
            if (rep.exists() && listOfBackupDirs.size() > 0) {

                for (int i = listOfBackupDirs.size() - 1; i >= 0; i--) {
                    if ((listOfBackupDirs.get(i).contains(bm.getDbName() + "_")) && (new File("\\\\" + bm.getBackUpDirectory() + "\\" + listOfBackupDirs.get(i) + "\\" + listOfBackupDirs.get(i) + ".zip")).exists()) {
                        listOfBackupFiles.add(getBackupLabel(listOfBackupDirs.get(i)));
                        listOfBackupFiles.add(listOfBackupDirs.get(i));

                    }
                }

            }
            Object[][] rowData = new Object[listOfBackupFiles.size()][2];
            int j = 0, i = 0;

            while(j < listOfBackupFiles.size()) {
                rowData[i][0] = listOfBackupFiles.get(j);
                rowData[i][1] = listOfBackupFiles.get(j + 1);
                j += 2;
                i++;
            }

            DefaultTableModel model = new DefaultTableModel(rowData, new String[] { "Date du backup", "Restaurer à  cette date", });
            while(i != model.getRowCount()) {
                model.removeRow(i);
            }
            table = new JTable() {

                /**
                 *
                 */
                private static final long serialVersionUID = -8108459626186595363L;

                @Override
                public boolean isCellEditable(int row, int column) {

                    return (column == 1);

                }
            };
            table.setModel(model);
            table.getColumn("Restaurer à  cette date").setCellRenderer(new ButtonRenderer("Restaurer"));
            table.getColumn("Restaurer à  cette date").setCellEditor(new ButtonEditor(new JCheckBox(), "Restaurer"));

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }
    // public static void main(String[] args) {
    // // TODO Auto-generated method stub
    //
    // BackUpManager.backup();
    // // BackUpManager.restore("biomod_20130430165356");
    // System.exit(0);
    // }
}
