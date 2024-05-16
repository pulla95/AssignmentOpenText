package com.texteditor.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Utilities;

public class EditorUI extends JFrame {

  private static final String DIGIT_REGEX = "^\\d+$";
  private static final long serialVersionUID = 1L;
  JTextArea textArea = new JTextArea(0, 0);
  JTextArea lines;
  JScrollPane scroller = new JScrollPane(textArea);
  JMenuBar menuBar = new JMenuBar();

  JMenu FILE = new JMenu("File");
  JMenuItem NEWFILE = new JMenuItem("New", new ImageIcon("new.jpg"));
  JMenuItem OPENFILE = new JMenuItem("Open", new ImageIcon("open.jpg"));
  JMenuItem SAVEFILE = new JMenuItem("Save", new ImageIcon("save.jpg"));
  JMenuItem QUITFILE = new JMenuItem("Quit");

  JMenu EDIT = new JMenu("Edit");
  JMenuItem INSERTEDIT = new JMenuItem("Insert");
  JMenuItem DELETEDIT = new JMenuItem("Delete");

  String file = null;
  String fileN;

  boolean opened = false;

  JPanel statusPanel = new JPanel();

  JLabel statusLabel;
  int ind = 0;

  StringBuffer sbufer;
  String lineNo;
  private String inputString;

  EditorUI() {
    super("Text Editor");
    this.setSize(800, 600);
    this.getContentPane().setLayout(new BorderLayout());
    textArea.setLineWrap(true);
    textArea.requestFocus(true);
    this.getContentPane().add(scroller, BorderLayout.CENTER);
    this.getContentPane().add(statusPanel, BorderLayout.SOUTH);
    textArea.setDragEnabled(true);

    lines = new JTextArea("1");
    lines.setBackground(Color.LIGHT_GRAY);
    lines.setEditable(false);
    textAreaListener();

    scroller.getViewport().add(textArea);
    scroller.setRowHeaderView(lines);
    scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    MouseListener popupListener = new PopupListener();
    textArea.addMouseListener(popupListener);

    FILE.add(NEWFILE);
    FILE.add(OPENFILE);
    FILE.add(SAVEFILE);
    FILE.addSeparator();
    FILE.add(QUITFILE);

    EDIT.add(INSERTEDIT);
    EDIT.add(DELETEDIT);

    menuBar.add(FILE);
    menuBar.add(EDIT);
    this.setJMenuBar(menuBar);

    // ACTION FOR NEW FILE ON THE MENUBAR
    NEWFILE.addActionListener((ActionEvent e) -> {
      opened = false;
      if (textArea.getText().equals("")) {
        System.out.print("text is empty");
      } else {
        int confirm = JOptionPane.showConfirmDialog(null, "Would you like to save?", "New File",
            JOptionPane.YES_NO_CANCEL_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
          saveFile();
          textArea.setText(null);
          statusPanel.removeAll();
          statusPanel.validate();
        } else if (confirm == JOptionPane.CANCEL_OPTION) {
        } else if (confirm == JOptionPane.NO_OPTION) {
          textArea.setText(null);
          statusPanel.removeAll();
          statusPanel.validate();
        }
      }
    });

    // OPEN BUTTON ON THE TOOLBAR
    OPENFILE.addActionListener((ActionEvent e) -> {
      openFile();
    });

    // SAVE BUTTON ON THE TOOLBAR
    SAVEFILE.addActionListener((ActionEvent e) -> {
      saveFile();
    });

    // ACTION FOR DELETE OPTION
    DELETEDIT.addActionListener((ActionEvent e) -> {
      try {
        inputString = JOptionPane.showInputDialog(null, "Delete At");
        if (inputString != null && !inputString.isEmpty()) {
          if (inputString.matches(DIGIT_REGEX)) {
            int lineNO = Integer.parseInt(inputString) - 2;
            if (lineNO < textArea.getLineCount()) {
              for (int i = 0; i < textArea.getLineCount(); i++) {
                if (i == lineNO) {
                  int offset = getLineStartOffset(i);
                  int rowStart = Utilities.getRowStart(textArea, offset);
                  int rowEnd = Utilities.getRowEnd(textArea, offset);
                  Document document = textArea.getDocument();
                  int len = rowEnd - rowStart + 1;
                  if (rowStart + len > document.getLength()) {
                    len--;
                  }
                  document.remove(rowStart, len);
                }
              }
            } else {
              JOptionPane.showMessageDialog(new JFrame(), "No input to delete", "Dialog",
                  JOptionPane.ERROR_MESSAGE);
            }
          }
        } else {
          JOptionPane.showMessageDialog(new JFrame(), "Provide valid line number", "Dialog",
              JOptionPane.ERROR_MESSAGE);
        }
      } catch (BadLocationException ex) {
        ex.printStackTrace();
      }
    });

    INSERTEDIT.addActionListener((ActionEvent e) -> {
      try {
        sbufer = new StringBuffer(textArea.getText());
        inputString = JOptionPane.showInputDialog(null, "Insert At");
        if (inputString != null && !inputString.isEmpty()) {
          if (inputString.matches(DIGIT_REGEX)) {
            int lineNO = Integer.parseInt(inputString) - 2;
            if (lineNO < textArea.getLineCount()) {
              for (int i = 0; i < textArea.getLineCount(); i++) {
                if (i == lineNO) {
                  int offset = getLineStartOffset(i);
                  int rowStart = Utilities.getRowStart(textArea, offset);
                  int rowEnd = Utilities.getRowEnd(textArea, offset);
                  Document document = textArea.getDocument();
                  int len = rowEnd - rowStart + 1;
                  if (rowStart + len > document.getLength()) {
                    len--;
                  }
                  textArea.insert(System.getProperty("line.separator"), rowEnd + 1);
                }
              }
            }
          } else {
            JOptionPane.showMessageDialog(new JFrame(), "Provide valid line number", "Dialog",
                JOptionPane.ERROR_MESSAGE);
          }
        }
      } catch (IllegalArgumentException npe) {
        JOptionPane.showMessageDialog(null, "Line not found");
      } catch (NullPointerException nfe) {
        nfe.printStackTrace();
      } catch (BadLocationException e1) {
        e1.printStackTrace();
      }
    });

    // Quits THE APPLICATION AND CHECKS FOR ANY CHANGES MADE
    QUITFILE.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
    QUITFILE.addActionListener((ActionEvent e) -> {
      int confirm = JOptionPane.showConfirmDialog(null, "Would you like to save?",
          "Quit Application", JOptionPane.YES_NO_CANCEL_OPTION);

      if (confirm == JOptionPane.YES_OPTION) {
        saveFile();
        dispose();
        System.exit(0);
      } else if (confirm == JOptionPane.CANCEL_OPTION) {
      } else {
        dispose();
        System.exit(0);
      }
    });
  }

  // FUNCTION CALLED BY THE SAVE BUTTON
  public void saveFile() {
    String line = textArea.getText();
    if (opened == true) {
      try {
        FileWriter output = new FileWriter(file);
        BufferedWriter bufout = new BufferedWriter(output);
        bufout.write(line, 0, line.length());
        JOptionPane.showMessageDialog(null, "Save Successful");
        bufout.close();
        output.close();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    } else {
      JFileChooser fc = new JFileChooser();
      int result = fc.showSaveDialog(new JPanel());

      if (result == JFileChooser.APPROVE_OPTION) {
        fileN = String.valueOf(fc.getSelectedFile());

        try {
          FileWriter output = new FileWriter(fileN);
          BufferedWriter bufout = new BufferedWriter(output);
          bufout.write(line, 0, line.length());
          JOptionPane.showMessageDialog(null, "Save Successful");
          bufout.close();
          output.close();
          opened = true;
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }
    }
  }

  // FUNCTION TO OPEN THE FILE
  public void openFile() {
    statusPanel.removeAll();
    statusPanel.validate();
    textArea.setText(null);
    JFileChooser fc = new JFileChooser();
    int result = fc.showOpenDialog(new JPanel());

    if (result == JFileChooser.APPROVE_OPTION) {
      String file = String.valueOf(fc.getSelectedFile());
      // String dirn = fc.getDirectory();

      File fil = new File(file);
      NEWFILE.setEnabled(false);

      // START THIS THREAD WHILE READING FILE
      Thread loader = new FileLoader(fil, textArea.getDocument());
      loader.start();
      statusPanel.removeAll();
      statusPanel.revalidate();
    } else {
    }
  }

  /**
   * Thread to load a file into the text storage model
   */
  class FileLoader extends Thread {

    JLabel state;

    FileLoader(File f, Document doc) {
      setPriority(4);
      this.f = f;
      this.doc = doc;
    }

    public void run() {
      try (Reader in = new FileReader(f)) {
        // initialize the statusbar
        statusPanel.removeAll();
        JProgressBar progress = new JProgressBar();
        progress.setMinimum(0);
        progress.setMaximum((int) f.length());
        statusPanel.add(new JLabel("opened so far "));
        statusPanel.add(progress);
        statusPanel.revalidate();

        char[] buff = new char[4096];
        int nch;
        while ((nch = in.read(buff, 0, buff.length)) != -1) {
          doc.insertString(doc.getLength(), new String(buff, 0, nch), null);
          progress.setValue(progress.getValue() + nch);
        }
        statusPanel.removeAll();
        statusPanel.revalidate();
      } catch (IOException e) {
        System.err.println(e.toString());
      } catch (BadLocationException e) {
        System.err.println(e.getMessage());
      }
      NEWFILE.setEnabled(true);
    }

    Document doc;
    File f;
  }

  private void textAreaListener() {
    textArea.getDocument().addDocumentListener(new DocumentListener() {
      public String getText() {
        int caretPosition = textArea.getDocument().getLength();
        Element root = textArea.getDocument().getDefaultRootElement();
        String text = "1" + System.getProperty("line.separator");
        for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
          text += i + System.getProperty("line.separator");
        }
        return text;
      }

      @Override
      public void changedUpdate(DocumentEvent de) {
        lines.setText(getText());
      }

      @Override
      public void insertUpdate(DocumentEvent de) {
        lines.setText(getText());
      }

      @Override
      public void removeUpdate(DocumentEvent de) {
        lines.setText(getText());
      }

    });
  }

  public int getLineStartOffset(int line) throws BadLocationException {
    Element map = textArea.getDocument().getDefaultRootElement();
    if (line < 0) {
      throw new BadLocationException("Negative line", -1);
    } else if (line >= map.getElementCount()) {
      throw new BadLocationException("No such line", textArea.getDocument().getLength() + 1);
    } else {
      Element lineElem = map.getElement(line);
      return lineElem.getStartOffset();
    }
  }

  public int getLineEndOffset(int line) throws BadLocationException {
    Element map = textArea.getDocument().getDefaultRootElement();
    if (line < 0) {
      throw new BadLocationException("Negative line", -1);
    } else if (line >= map.getElementCount()) {
      throw new BadLocationException("No such line", textArea.getDocument().getLength() + 1);
    } else {
      Element lineElem = map.getElement(line);
      return lineElem.getEndOffset();
    }
  }

  public static void main(String[] args) {
    EditorUI editorUI = new EditorUI();
    editorUI.setVisible(true);
  }

  class PopupListener extends MouseAdapter {

    public JPopupMenu pop = new JPopupMenu();

    public void mousePressed(MouseEvent e) {

      maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
      maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
      if (e.isPopupTrigger()) {
        pop.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }
}
