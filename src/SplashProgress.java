import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

public class SplashProgress {

    protected static SplashProgress instance = new SplashProgress();
    protected SplashProgressWindow window = null;
    protected int increment = 1;

    private SplashProgress() {
    }

    public static SplashProgress instance() {
        return instance;
    }

    public synchronized void start(String text, int max) {
        if (this.window != null) {
            this.window.end();
        }
        this.window = new SplashProgressWindow(" ", max); //$NON-NLS-1$
        this.setValue(text, 0);
    }

    public synchronized void end() {
        if (this.window != null) {
            this.window.end();
            this.window = null;
        }
    }

    public void setValueKeepText(int value) {
        if (this.window != null) {
            if (true == this.window.setValueKeepText(value)) {
                this.window = null;
            }
        }
    }

    public void setValue(String text, int value) {
        if (this.window != null) {
            if (true == this.window.setProgressValue(text, value)) {
                this.window = null;
            }
        }
    }

    public void setIncrement(int inc) {
        this.increment = inc;
    }

    public void increment() {
        if (this.window != null) {
            this.window.increment(this.increment);
        }
    }

    public void increment(String text, int inc) {
        if (this.window != null) {
            this.window.increment(text, inc);
        }
    }

    public void increment(int inc) {
        if (this.window != null) {
            this.window.increment(inc);
        }
    }

    public void toFront() {
        if (this.window != null) {
            this.window.setVisible(true);
        }
    }
}

class SplashProgressWindow extends JWindow {

    /**
     * 
     */
    private static final long serialVersionUID = -7240432135174886897L;

    public static final int PROGRESS_WIDTH = 240;
    public static final int PROGRESS_AND_LABEL_HEIGHT = 50;
    public static final int PROGRESS_ONLY_HEIGHT = 20;

    private JProgressBar progressBar = null;
    private int maxValue = 0;
    JLabel lblText = new JLabel();
    boolean lblDisplayed = true;

    public SplashProgressWindow(String text, int intProgressMaxValue) {
        super();
        this.setVisible(false);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int labelW = 0;
        int labelH = 0;

        // initialise la valeur a laquelle le splash screen doit etre ferme
        this.maxValue = intProgressMaxValue;

        // ajoute la progress bar
        this.progressBar = new JProgressBar(0, intProgressMaxValue);
        this.getContentPane().add(this.progressBar, BorderLayout.SOUTH);

        /*
         * if (filename != null && filename == EMPTY_STRING) { // cree un label avec notre image JLabel image = new JLabel(new ImageIcon(filename)); // ajoute le label au panel getContentPane().add(image, BorderLayout.CENTER); labelW =
         * image.getPreferredSize().width; labelH = image.getPreferredSize().height; }
         */

        if (text != null && !"".equals(text)) { //$NON-NLS-1$
            this.lblText.setText(text);
            Dimension d = this.lblText.getSize();
            labelW = d.width;
            labelH = d.height;
            this.getContentPane().add(this.lblText, BorderLayout.CENTER);
        }
        this.pack();

        // Centers the splash screen
        this.setBounds(screenSize.width / 2 - labelW / 2, screenSize.height / 2 - labelH / 2, PROGRESS_WIDTH, PROGRESS_AND_LABEL_HEIGHT);

        ((JPanel) this.getContentPane()).setBorder(BorderFactory.createEtchedBorder());

        // Makes the splash screen invisible when clicked
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                SplashProgressWindow.this.setVisible(false);
                SplashProgressWindow.this.dispose();
            }
        });

        // Displays the splash screen
        this.setVisible(true);
    }

    /**
     * 
     * @param value
     * @return true if the progress is over, false otherwise
     */
    public synchronized boolean setProgressValue(String text, int value) {
        if (text == null || "".equals(text)) { //$NON-NLS-1$
            if (this.lblDisplayed) {
                this.setVisible(false);
                this.getContentPane().remove(this.lblText);
                this.pack();
                this.lblDisplayed = false;
            }
        } else {
            this.lblText.setText(text);
            if (false == this.lblDisplayed) {
                this.setVisible(false);
                this.getContentPane().add(this.lblText, BorderLayout.CENTER);
                this.pack();
                this.lblDisplayed = true;
            }
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int h = this.lblDisplayed ? PROGRESS_AND_LABEL_HEIGHT : PROGRESS_ONLY_HEIGHT;
        int y = this.lblDisplayed ? (screenSize.height / 2 - PROGRESS_AND_LABEL_HEIGHT / 2) : screenSize.height / 2 + PROGRESS_AND_LABEL_HEIGHT / 2 - PROGRESS_ONLY_HEIGHT;

        this.setBounds(screenSize.width / 2 - PROGRESS_WIDTH / 2, y, PROGRESS_WIDTH, h);

        this.setVisible(true);
        this.progressBar.setValue(value);
        // System.out.println(value+"\t"+lblText.getText());
        // si est arrive a la valeur max : ferme le splash screen en lancant le thread
        if (value >= this.maxValue) {
            this.setVisible(false);
            // dispose();
            return true;
            // try {
            // SwingUtilities.invokeAndWait(closerRunner);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // } catch (InvocationTargetException e) {
            // e.printStackTrace();
            // }
        }
        this.toFront();
        this.update(this.getGraphics());
        return false;
    }

    public synchronized void end() {
        this.setProgressValue("", this.maxValue + 1); //$NON-NLS-1$
    }

    /**
     * 
     * @param i
     * @return true if the progress is over, false otherwise
     */
    public synchronized boolean increment(int i) {
        return this.setValueKeepText(this.progressBar.getValue() + i);
    }

    public synchronized boolean increment(String text, int i) {
        return this.setProgressValue(text, this.progressBar.getValue() + i);
    }

    public synchronized boolean setValueKeepText(int value) {
        String text = ""; //$NON-NLS-1$
        if (this.lblDisplayed) {
            text = this.lblText.getText();
        }
        return this.setProgressValue(text, value);
    }

    public synchronized int getValue() {
        return this.progressBar.getValue();
    }

    // thread pour fermer le splash screen
    final Runnable closerRunner = new Runnable() {

        @Override
        public void run() {
            SplashProgressWindow.this.setVisible(false);
            SplashProgressWindow.this.dispose();
            // System.exit(0);
        }
    };
}