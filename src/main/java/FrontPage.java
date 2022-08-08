import com.intellij.uiDesigner.core.*;
import io.kubernetes.client.openapi.*;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class FrontPage extends JFrame {
    private JPanel panelMain;
    private JTextField orgSelect;
    private JTextArea resultTextArea;
    private JRadioButton includeAccessTokenRadioButton;
    private JButton resetButton;
    private JButton submitButton;
    private JLabel orgLabel;
    private JRadioButton findRegion;
    private JTextField executionId;
    private JLabel execIdLabel;
    private JComboBox regionSelectBox;
    private JButton logsButton;
    private static Parser parser = new Parser();

    public FrontPage() {
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parser.ORGID = orgSelect.getText().toLowerCase();
                parser.REGION = (String) regionSelectBox.getSelectedItem();
                resultTextArea.setText("");
                parser = new Parser();
                parser.executionId = executionId.getText();
                if (includeAccessTokenRadioButton.isSelected()) {
                    parser.includeTokenBoolean = true;
                } else {
                    parser.includeTokenBoolean = false;
                }
                if (findRegion.isSelected()) {
                    try {
                        resultTextArea.setText(parser.pullFromProvisionDB());
                    } catch (IOException | InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    try {
                        resultTextArea.setText(parser.run());
                    } catch (IOException | InterruptedException | ApiException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        logsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parser.ORGID = orgSelect.getText().toLowerCase();
                parser.REGION = (String) regionSelectBox.getSelectedItem();
                createFrame();
                parser.printLog();
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String def = "";
                orgSelect.setText(def);
                resultTextArea.setText(def);
                executionId.setText(def);
                includeAccessTokenRadioButton.setSelected(false);
                regionSelectBox.setSelectedItem(def);
                Parser.ACCESSTOKEN = def;
                parser.includeTokenBoolean = false;
                parser.resultJson = new ResultJson();
                parser.ORGID = def;
                parser.executionId = def;
                parser.partnerConnection = null;
                parser.partnerUrl = def;
                parser.orgDomain = def;
                parser.DOMAIN = def;
                parser.REGION = def;
                parser.IS_SANBOX = false;
                parser = new Parser();
                findRegion.setSelected(false);
            }
        });
    }

    public static void createFrame() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Bar Api Logs: " + parser.ORGID);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setOpaque(true);
                JTextArea textArea = new JTextArea(300, 100);
                textArea.setWrapStyleWord(false);
                textArea.setEditable(false);
                textArea.setFont(Font.getFont(Font.SANS_SERIF));
                JScrollPane scroller = new JScrollPane(textArea);
                scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                panel.add(scroller);

                frame.getContentPane().add(BorderLayout.CENTER, panel);
                frame.pack();
                frame.setLocationByPlatform(true);
                frame.setVisible(true);
                frame.setResizable(true);

                textArea.setText(parser.printLog());
//                PrintStream standardOut = System.out;
//                PrintStream standardErr = System.err;
//                PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
//                // re-assigns standard output stream and error output stream
//                System.setOut(printStream);
//                System.setErr(printStream);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Backup Stats");
        frame.setContentPane(new FrontPage().panelMain);
        frame.pack();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelMain = new JPanel();
        panelMain.setLayout(new GridLayoutManager(4, 9, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.setMinimumSize(new Dimension(1000, 500));
        panelMain.setPreferredSize(new Dimension(1000, 500));
        orgSelect = new JTextField();
        orgSelect.setText("");
        panelMain.add(orgSelect, new GridConstraints(0, 1, 1, 8, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        orgLabel = new JLabel();
        orgLabel.setText("ORG");
        panelMain.add(orgLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panelMain.add(scrollPane1, new GridConstraints(3, 0, 1, 8, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        resultTextArea = new JTextArea();
        resultTextArea.setColumns(15);
        resultTextArea.setEditable(false);
        resultTextArea.setLineWrap(true);
        resultTextArea.setRows(15);
        scrollPane1.setViewportView(resultTextArea);
        includeAccessTokenRadioButton = new JRadioButton();
        includeAccessTokenRadioButton.setText("Include Access Token");
        panelMain.add(includeAccessTokenRadioButton, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        resetButton = new JButton();
        resetButton.setText("Reset");
        panelMain.add(resetButton, new GridConstraints(2, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        submitButton = new JButton();
        submitButton.setText("Submit");
        panelMain.add(submitButton, new GridConstraints(2, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        findRegion = new JRadioButton();
        findRegion.setText("Find Region");
        panelMain.add(findRegion, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        execIdLabel = new JLabel();
        execIdLabel.setHorizontalAlignment(4);
        execIdLabel.setText("Exec ID:");
        panelMain.add(execIdLabel, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(102, 19), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Region");
        panelMain.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        executionId = new JTextField();
        panelMain.add(executionId, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(70, 38), null, 0, false));
        regionSelectBox = new JComboBox();
        regionSelectBox.setMaximumRowCount(20);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("");
        defaultComboBoxModel1.addElement("us-east-1");
        defaultComboBoxModel1.addElement("ap-northeast-1");
        defaultComboBoxModel1.addElement("ca-central-1");
        defaultComboBoxModel1.addElement("eu-central-1");
        defaultComboBoxModel1.addElement("eu-west-1");
        defaultComboBoxModel1.addElement("eu-west-2");
        defaultComboBoxModel1.addElement("us-west-2");
        defaultComboBoxModel1.addElement("us-west-1");
        defaultComboBoxModel1.addElement("ap-southeast-2");
        defaultComboBoxModel1.addElement("me-south-1");
        defaultComboBoxModel1.addElement("eu-west-3");
        defaultComboBoxModel1.addElement("eu-south-1");
        defaultComboBoxModel1.addElement("sa-east-1");
        defaultComboBoxModel1.addElement("eu-north-1");
        defaultComboBoxModel1.addElement("ap-east-1");
        defaultComboBoxModel1.addElement("ap-south-1");
        defaultComboBoxModel1.addElement("af-south-1");
        defaultComboBoxModel1.addElement("ap-northeast-2");
        regionSelectBox.setModel(defaultComboBoxModel1);
        panelMain.add(regionSelectBox, new GridConstraints(1, 1, 1, 7, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logsButton = new JButton();
        logsButton.setText("Logs");
        panelMain.add(logsButton, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelMain;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
