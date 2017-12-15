package dnarevcmpl3;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileSystemView;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.commons.io.FilenameUtils;


public class ReverseComplementAndCombine extends JPanel implements ActionListener {
    private static final long serialVersionUID = -4487732343062917781L;
    JFileChooser fc;
    JButton clear;
    JButton revcmplBtn;
    JFormattedTextField separator;
    JFormattedTextField outputfn;
    JRadioButton fastaNameGiven;
    JRadioButton fastaNameNotGiven;
    JTextArea console;
    boolean FastaNameInFile;

    JList dropZone;
    DefaultListModel listModel;
    JSplitPane childSplitPane, parentSplitPane;
    PrintStream ps;

  public ReverseComplementAndCombine() {
    super(new BorderLayout());

    fc = new JFileChooser();;
    fc.setMultiSelectionEnabled(true);
    fc.setDragEnabled(true);
    fc.setControlButtonsAreShown(false);
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);


    JPanel fcPanel = new JPanel(new BorderLayout());
    fcPanel.add(fc, BorderLayout.CENTER);

    JLabel label = new JLabel("Identifier (within name of reversed seq): ");
	separator = new JFormattedTextField("R.");
    separator.setValue("R.");
    separator.setToolTipText("This is used to determine wether your sequence is in reverse "
    		+ "or forward direction. E.g.: Your file should end on R.txt for a sequence that is "
    		+ "in reverse and it should end on *.txt for forward direction.");
    JLabel label2 = new JLabel("Outputfilename : ");
    String standardOutputName = "revcmpl"; //dataFolder+"\\revcmpl.seq";
	outputfn = new JFormattedTextField(standardOutputName);
	outputfn.setValue(standardOutputName);
	outputfn.setToolTipText("The files (one with the suffix .txt and one with .docx) will be "
			+ "stored in the folder of your input files.");
    JLabel label5 = new JLabel("Input all your files that you want to ");
    label5.setHorizontalAlignment(SwingConstants.RIGHT);
    JLabel label7 = new JLabel("the direction e.g.: reverse or forward,");
    label7.setHorizontalAlignment(SwingConstants.RIGHT);
    
    
	GridLayout experimentLayout = new GridLayout(0,2);
	final JPanel compsToExperiment = new JPanel();
    compsToExperiment.setLayout(experimentLayout);
    JPanel controls = new JPanel();
    controls.setLayout(new GridLayout(2,3));
    compsToExperiment.add(label);
    compsToExperiment.add(separator);
    compsToExperiment.add(label2);
    compsToExperiment.add(outputfn);

    
    clear = new JButton("Clear All");
    clear.addActionListener(this);
    JPanel buttonPanel = new JPanel(new BorderLayout());
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    buttonPanel.add(clear, BorderLayout.LINE_END);
    
    revcmplBtn = new JButton("Combine and ReverseComplement sequences");
    revcmplBtn.addActionListener(new ActionListener() { 
        public void actionPerformed(ActionEvent e) { 
        	if (listModel.isEmpty()) {
        		console.append("You didn't drop any files into the \"Selected sequences\" "
        				+ "window. \n Please drop your files either from your Windows-Explorer or "
        				+ "locate them through the given explorer within the tool. \n");
        	}
        	else {
        		console.setText(null);
	        	console.append("Starting to ReverseComplement and combine your sequences\n\n");
	        	console.append("Running now: \n");
	        	
	        	// add try catch, maybe user forgot to drag the data...
	        	try {
	        	    //BufferedWriter out = new BufferedWriter(new FileWriter(outputfn.getText(), false));
	        		File tmpFile = new File(listModel.get(0).toString());
	        		String outputFN = tmpFile.getParentFile() + "\\" + outputfn.getText();
	        		
	        		FileWriter outTXT = new FileWriter(outputFN+".txt", false); //outputfn.getText(), false);
	        		
	        		// Blank Document
	        		XWPFDocument document = new XWPFDocument();

	        		// Write the Document in file system
	        		FileOutputStream out = new FileOutputStream(new File(outputFN+".docx"));
	        		
	        		String outputString = "";
	        		String seqName = "";
	        		String sequence = "";
	        		
		        	for (int i = 0; i < listModel.getSize(); i++) {
		        		
		        		//console.append(listModel.get(i).toString());
		        		String[] tmpFileData = openFile(listModel.get(i).toString(), separator.getText());//, true);
		        		seqName = tmpFileData[0];
		        		sequence = tmpFileData[1];
		        		
		        		if (seqName.contains(separator.getText())) {
		        			sequence = revcmpl(sequence);
		//        			System.out.println(seqName+"is in Reverse");
		        			console.append(seqName.replace('\n', ' ')+"- is in reverse, length is "+sequence.length()+"\n");
		        			if (sequence.length()==0) {
		        				System.out.println("ERROR on file "+seqName+". Sequencelength is 0!");
		        			}
		        		}
		        		else {
		        			console.append(seqName.replace('\n', ' ')+", length is "+sequence.length()+"\n");
		        			if (sequence.length()==0) {
		        				System.out.println("ERROR on file "+seqName+". Sequencelength is 0!");
		        			}
		        		}
		        		//System.out.println(seqName);
		        		//System.out.println(seqName+sequence);
		        		
		            	outputString += seqName.replace("\n", "")+"\n"+sequence.replace("\n", "")+"\n";
		            	//System.out.println("OutputString: \n"+outputString);   
		            
		        	}
		        	console.append("Finished combining all of the given sequences\n");
		        	
		        XWPFParagraph paragraph = document.createParagraph();
	 		    XWPFRun paragraphOneRunOne = paragraph.createRun();
	 		    if (outputString.contains("\n")) {
	 		    	String[] lines = outputString.split("\n");
	 	            paragraphOneRunOne.setText(lines[0], 0); // set first line into XWPFRun
	 	            for(int t = 1; t<lines.length; t++){
	 	            	// add break and insert new text
	 	            	paragraphOneRunOne.addBreak();
	 	            	paragraphOneRunOne.setText(lines[t]);
	 	            }
	 	        } else {
	 	         	paragraphOneRunOne.setText(outputString, 0);
	 	        }
		 		document.write(out);
		 		out.close();
		        outTXT.write(outputString);
		        outTXT.close();
		        document.close();

	        	}
	            catch (IOException e1)
	            {
	            	//TODO
	                System.out.println("Exception ");
	                System.out.println(e1.getMessage());
	            }
        	}

        	//console.append(output.toString());
        	//parentSplitPane.validate();
//        	System.out.println("Output:\n"+output);
        } 
    });
    buttonPanel.add(revcmplBtn, BorderLayout.LINE_START);


    
    JPanel leftUpperPanel = new JPanel(new BorderLayout());
    leftUpperPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    leftUpperPanel.add(fcPanel, BorderLayout.CENTER);
    leftUpperPanel.add(buttonPanel, BorderLayout.PAGE_END);
    leftUpperPanel.add(compsToExperiment, BorderLayout.BEFORE_FIRST_LINE);

    JScrollPane leftLowerPanel = new javax.swing.JScrollPane();
    leftLowerPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

    listModel = new DefaultListModel();
    dropZone = new JList(listModel);
    dropZone.setCellRenderer(new FileCellRenderer());
    dropZone.setTransferHandler(new ListTransferHandler(dropZone));
    dropZone.setDragEnabled(true);
    dropZone.setDropMode(javax.swing.DropMode.INSERT);
    dropZone.setBorder(new TitledBorder("Selected sequences"));
    leftLowerPanel.setViewportView(new JScrollPane(dropZone));

    childSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            leftUpperPanel, leftLowerPanel);
    childSplitPane.setDividerLocation(400);
    childSplitPane.setPreferredSize(new Dimension(480, 650));

    console = new JTextArea();
    console.setColumns(40);
    console.setLineWrap(true);
    console.setBorder(new TitledBorder("Console - what happens right now"));

    parentSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                    childSplitPane, console);
    parentSplitPane.setDividerLocation(480);
    parentSplitPane.setPreferredSize(new Dimension(800, 650));

    add(parentSplitPane, BorderLayout.CENTER);

    console.append("This tool combines all selected Files and ReverseComplements those, that are identified by the identifier. \n\n Drop your files into the \"Selected sequences\" window and press \"Combine and ReverseComplement sequences\" as soon as you are done\n\n");
    parentSplitPane.validate();
}
  
public String getSequence(String[] seq, int startPos) {
	
	
	String tmpSeq = "";
	System.out.println("SeqLength: "+seq.length+", startPos: "+startPos);
	for (int i = 0; i < (seq.length-startPos); i++) {
		System.out.println("position: "+(i+startPos)+", Sequence: "+seq[i+startPos]);
		tmpSeq += seq[i+startPos].replace("\n", "").replace("\r", "");
	}

	return tmpSeq;

}

public boolean isFastaFormat(String seq) {
	
	return seq.contains(">");
	
}

public String[] openFile(String fileName, String separator) {
	
	
	//[dna sequence, name of sequence used for Fasta output (e.g. >NA12878)]
	String data[] = new String[2];
	Scanner sc = null;
	File FN = new File(fileName);
	
	try {
		sc = new Scanner(FN);
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		System.out.println("Exception ");
	    System.out.println(e.getMessage());
	}
	List<String> lines = new ArrayList<String>();
	while (sc.hasNextLine()) {
	  lines.add(sc.nextLine());
	}
	String[] SeqArr = lines.toArray(new String[0]);
	
	if (isFastaFormat(SeqArr[0])) {
		data[0] = SeqArr[0].replace("\n", "")+"\n";
		if (fileName.contains(separator)) {
			data[0] = SeqArr[0].replace("\n", "")+separator+"\n";
		}
		data[1] = getSequence(SeqArr, 1);
	}
	else {
		data[0] = ">"+FilenameUtils.removeExtension(FN.getName())+"\n";
		data[1] = getSequence(SeqArr, 0);//tmpFileData[0].replace("\n", "");
	}
	
	return data;
}

public String revcmpl(String dna) {
	

	StringBuilder sb = new StringBuilder();

	for (int i = 0; i < dna.length(); i++) {
		char a = dna.charAt(i);

		switch (a) {
		case 'A':
			sb.append("T");
			break;
		case 'T':
			sb.append("A");
			break;
		case 'C':
			sb.append("G");
			break;
		case 'G':
			sb.append("C");
			break;
		case 'R':
			sb.append("Y");
			break;
		case 'Y':
			sb.append("R");
			break;
		case 'M':
			sb.append("K");
			break;
		case 'K':
			sb.append("M");
			break;
		case 'B':
			sb.append("V");
			break;
		case 'D':
			sb.append("H");
			break;
		case 'H':
			sb.append("D");
			break;
		case 'V':
			sb.append("B");
			break;
		case 'N':
			sb.append("N");
			break;
		case 'S':
			sb.append("S");
			break;
		case 'W':
			sb.append("W");
			break;
		case 'U':
			sb.append("A");
			break;
		case 'a':
			sb.append("t");
			break;
		case 't':
			sb.append("a");
			break;
		case 'c':
			sb.append("g");
			break;
		case 'g':
			sb.append("c");
			break;
		case 'r':
			sb.append("y");
			break;
		case 'y':
			sb.append("r");
			break;
		case 'm':
			sb.append("k");
			break;
		case 'k':
			sb.append("m");
			break;
		case 'b':
			sb.append("v");
			break;
		case 'd':
			sb.append("h");
			break;
		case 'h':
			sb.append("d");
			break;
		case 'v':
			sb.append("b");
			break;
		case 'n':
			sb.append("n");
			break;
		case 's':
			sb.append("s");
			break;
		case 'w':
			sb.append("w");
			break;
		case 'u':
			sb.append("a");
			break;
		default:
			sb.append("RORRE");
			break;
		}
	}
	
	dna = sb.reverse().toString();
	
    return dna;
}
  
public void setDefaultButton() {
    getRootPane().setDefaultButton(clear);
    getRootPane().setDefaultButton(revcmplBtn);
}

public void actionPerformed(ActionEvent e) {
    if (e.getSource() == clear) {
        listModel.clear();
        console.setText(null);
        console.append("Drop your files into the \"Selected sequences\" window and press \"Combine and ReverseComplement sequences\" as soon as you are done\n\n");
    }
}

/**
 * Create the GUI and show it. For thread safety,
 * this method should be invoked from the
 * event-dispatching thread.
 */
private static void createAndShowGUI() {
    //Make sure we have nice window decorations.
    JFrame.setDefaultLookAndFeelDecorated(true);
    try {
      //UIManager.setLookAndFeel("de.javasoft.plaf.synthetica.SyntheticaBlackStarLookAndFeel");
        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
    }catch (Exception e){
      e.printStackTrace();
    }

    //Create and set up the window.
    JFrame frame = new JFrame("Combine and ReverseComplement sequences");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    //Create and set up the menu bar and content pane.
    ReverseComplementAndCombine demo = new ReverseComplementAndCombine();
    demo.setOpaque(true); //content panes must be opaque
    frame.setContentPane(demo);

    //Display the window.
    frame.pack();
    frame.setVisible(true);
    demo.setDefaultButton();
}

public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {
            createAndShowGUI();
        }
    });
}
}

class FileCellRenderer extends DefaultListCellRenderer {

    public Component getListCellRendererComponent(JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus) {

        Component c = super.getListCellRendererComponent(
            list,value,index,isSelected,cellHasFocus);

        if (c instanceof JLabel && value instanceof File) {
            JLabel l = (JLabel)c;
            File f = (File)value;
            l.setIcon(FileSystemView.getFileSystemView().getSystemIcon(f));
            l.setText(f.getName());
            l.setToolTipText(f.getAbsolutePath());
        }

        return c;
    }
}

class ListTransferHandler extends TransferHandler {

    private JList list;

    ListTransferHandler(JList list) {
        this.list = list;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
        // we only import FileList
        if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        if (!info.isDrop()) {
            return false;
        }

        // Check for FileList flavor
        if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            displayDropLocation("List doesn't accept a drop of this type.");
            return false;
        }

        // Get the fileList that is being dropped.
        Transferable t = info.getTransferable();
        List<File> data;
        try {
            data = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
        }
        catch (Exception e) { return false; }
        DefaultListModel model = (DefaultListModel) list.getModel();
        for (Object file : data) {
            model.addElement((File)file);
        }
        return true;
    }

    private void displayDropLocation(String string) {
        System.out.println(string);
    }
}