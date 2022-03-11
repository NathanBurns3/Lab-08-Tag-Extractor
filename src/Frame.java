import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.ArrayList;
import java.util.Scanner;

import static java.nio.file.StandardOpenOption.CREATE;

public class Frame extends JFrame {

    JPanel mainPnl;
    JPanel displayPnl;
    JPanel controlPnl;

    JTextArea displayTA;
    JScrollPane scroller;

    JButton pickBtn;
    JButton quitBtn;
    JButton fileBtn;

    public Frame() {
        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());

        createDisplayPanel();
        mainPnl.add(displayPnl, BorderLayout.NORTH);

        createControlPanel();
        mainPnl.add(controlPnl, BorderLayout.SOUTH);

        add(mainPnl);
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void createDisplayPanel() {
        displayPnl = new JPanel();
        displayTA = new JTextArea(39,40);
        displayTA.setEditable(false);
        scroller = new JScrollPane(displayTA);
        displayPnl.setFont(new Font("Comic Sans MS", Font.PLAIN, 10));
        displayPnl.add(scroller);
    }

    private void createControlPanel() {
        controlPnl = new JPanel();
        controlPnl.setLayout(new GridLayout(1,3));

        pickBtn = new JButton("Pick File");
        quitBtn = new JButton("Quit");
        fileBtn = new JButton("Make a File");

        controlPnl.add(pickBtn);
        controlPnl.add(fileBtn);
        controlPnl.add(quitBtn);

        pickBtn.addActionListener((ActionEvent ae) -> {
            JFileChooser chooser = new JFileChooser();
            File selectedFile;
            String rec = "";
            int count = 0;
            try
            {
                // uses a fixed known path:
                //  Path file = Paths.get("c:\\My Documents\\data.txt");
                // use the toolkit to get the current working directory of the IDE
                // Not sure if the toolkit is thread safe...
                File workingDirectory = new File(System.getProperty("user.dir"));
                // Typiacally, we want the user to pick the file so we use a file chooser
                // kind of ugly code to make the chooser work with NIO.
                // Because the chooser is part of Swing it should be thread safe.
                chooser.setCurrentDirectory(workingDirectory);
                // Using the chooser adds some complexity to the code.
                // we have to code the complete program within the conditional return of
                // the filechooser because the user can close it without picking a file
                if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                {
                    selectedFile = chooser.getSelectedFile();
                    Path file = selectedFile.toPath();
                    // Typical java pattern of inherited classes
                    // we wrap a BufferedWriter around a lower level BufferedOutputStream
                    InputStream in =
                            new BufferedInputStream(Files.newInputStream(file, CREATE));
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(in));

                    Scanner myReader = new Scanner(file);
                    displayTA.append(String.valueOf(file));

                    // Make the array of Stop words
                    FileReader f = new FileReader("English Stop Words.txt");
                    BufferedReader b = new BufferedReader(f);
                    String words = b.readLine();
                    String[] stopWords = words.split(" ");


                    // Finally we can read the file LOL!
                    while (myReader.hasNextLine()) {
                        //makes an array and splits up the words in the text file and inserts them
                        String data = myReader.nextLine();
                        String[] preParts = data.split("\\s+|\\,|\\.|\\'|\\:|\\;|\\!|\\)|\\(|\\*");

                        // makes a new array and makes all the words lowercase
                        String[] parts = new String[preParts.length];
                        for (int i = 0; i < preParts.length; i++)
                        {
                            parts[i] = preParts[i].toLowerCase();
                        }

                        // makes a new array that compares the parts array with the 'StopWords' array and inserts the words that don't show up in the 'StopWords' array
                        ArrayList<String> listFinalParts = new ArrayList<String>();
                        for (String var : parts) {
                            for (String var2 : stopWords) {
                                if (var.equals(var2)) {
                                    count++;
                                }
                            }
                            if (count == 0) {
                                listFinalParts.add((var));
                            }
                            count = 0;
                        }
                        String[] finalParts = listFinalParts.toArray(new String[0]);


                        Set<String> st = new HashSet<String>(listFinalParts);
                        for (String s : st) {
                            displayTA.append(s + ": " + Collections.frequency(listFinalParts, s));
                            displayTA.append("\n");
                        }


                        /*
                        for (String var3 : finalParts) {
                            displayTA.append(var3);
                            displayTA.append("\n");
                        }
                         */
                    }

                    myReader.close();
                    System.out.println("\n\nData file read!");
                }
            }
            catch (FileNotFoundException e)
            {
                System.out.println("File not found!!!");
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });

        quitBtn.addActionListener((ActionEvent ae) -> System.exit(0));

        fileBtn.addActionListener((ActionEvent ae) -> {

            if(ae.getSource() == fileBtn)
            {
                try  {
                    FileWriter fileWriter = new FileWriter("TestFile.txt");
                    fileWriter.write(displayTA.getText());
                    fileWriter.close();
                    JOptionPane.showMessageDialog(null, "File Written");

                    /*
                    PrintWriter out = new PrintWriter(new FileWriter("TestFile.txt"));
                    displayTA.write(out);
                    out.close();

                     */

                } catch (IOException e1) {
                    System.err.println("Error occurred");
                    e1.printStackTrace();
                }
            }



            Scanner in = new Scanner(System.in);
            String textFileName = in.nextLine(); //SafeInput.getNonZeroLenString(in, "\nEnter text file name");
            try {
                PrintWriter writer = new PrintWriter(textFileName);
                writer.close();
                System.out.println("Data file written!");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}
