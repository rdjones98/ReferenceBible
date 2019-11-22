import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;



public class ReferenceGenerator extends JPanel implements ActionListener, Runnable{
	private JTextField 	inFileName = new JTextField("*.xml");;
	private JLabel     	inFilePath = new JLabel("");;
	private JLabel 	   	outName = new JLabel("Output File:");;
	private JTextField 	version = new JTextField("NASB");;
	private JTextArea  	htmlArea = new JTextArea();
	private JScrollPane htmlPane = new JScrollPane();
	private JButton 	pickFileBtn = new JButton("...");
	private JButton 	goBtn = new JButton("Gen HTML");
	private JButton 	saveBtn = new JButton("Save XML");
	private JButton 	previewBtn = new JButton("previewHTML (You must save&Gen)>");
	private JFXPanel 	jfxPanel; // HTML Panel
	private JTextArea	console = new JTextArea();
	private JScrollPane consolePane = new JScrollPane(); 

	private JTable xmlTable;
	private JScrollPane xmlScroll = new JScrollPane();
	private JLabel status = new JLabel("Select a File/File(s)");
	private String style="";
	
	private String[] batch = {	"C:\\Users\\User\\eclipse-workspace\\Bible\\Proverbs\\*.xml",
								"C:\\Users\\User\\eclipse-workspace\\Bible\\Psalms\\*.xml",
								"C:\\Users\\User\\eclipse-workspace\\Bible\\Revelation\\*.xml",
								"C:\\Users\\User\\eclipse-workspace\\Bible\\Romans\\*.xml",
								"C:\\Users\\User\\eclipse-workspace\\Bible\\Song of Songs\\*.xml",
								"C:\\Users\\User\\eclipse-workspace\\Bible\\Titus\\*.xml",
								"C:\\Users\\User\\eclipse-workspace\\Bible\\Zechariah\\*.xml",
								"C:\\Users\\User\\eclipse-workspace\\Bible\\Zephaniah\\*.xml"};
	private int batchIdx=0;

	public static void main(String[] args) 
	{
		JFrame frame = new JFrame();
		frame.setSize(800,1100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Reference Bible Generator");
		frame.add(new ReferenceGenerator(null));
		frame.setVisible(true);
	}
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(800,1100);
	}
	public ReferenceGenerator(JFXPanel ajfxPanel) {
		int col1 = 20;
		int col2 = 100;
		int col3 = 400;
		
		jfxPanel = ajfxPanel;
		setLayout(null);
		JLabel j = new JLabel("Version:");
		j.setSize(100,20);
		j.setLocation(col1,20);
		add(j);

		version.setSize(100,20);
		version.setLocation(col2,20);
		add(version);


		JLabel jl = new JLabel("File Name:");
		jl.setSize(100,20);
		jl.setLocation(col1,50);
		add(jl);

		inFileName.setSize(300,20);
		inFileName.setLocation(col2,50);
		add(inFileName);

		pickFileBtn.setSize(20,20);
		pickFileBtn.setLocation(col3,50);
		pickFileBtn.addActionListener(this);
		add(pickFileBtn);

		inFilePath.setSize(800-(col3+30),20);
		inFilePath.setLocation(col3+30,50);
		add(inFilePath);

		outName.setSize(300,20);
		outName.setLocation(col1,80);
		add(outName);

		saveBtn.setSize(100,20);
		saveBtn.setLocation(100,110);
		saveBtn.addActionListener(this);
		add(saveBtn);

		goBtn.setDefaultCapable(true);
		goBtn.setSize(100,20);
		goBtn.setLocation(250,110);
		goBtn.addActionListener(this);
		add(goBtn);


		if(jfxPanel != null )
		{
			previewBtn.setSize(250,20);
			previewBtn.setLocation(400,110);
			previewBtn.addActionListener(this);
			add(previewBtn);
		}


		/*		htmlPane.setLocation(5,550);
		htmlPane.setSize(780,300);
		htmlPane.setViewportView(htmlArea);
		add(htmlPane);
		 */		
		
		
		// Create a JTable for editing the XML
	    TableModel model = new DefaultTableModel(new Object[]{"c1", "c2","c3","ref","ref verse","Add","Del"},0) {
	        public boolean isCellEditable(int rowIndex, int mColIndex) {
	        	return mColIndex >=3;
	        }
	      };

	    
		xmlTable = new JTable(model);
		xmlTable.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) 
			{
				DefaultTableModel model = (DefaultTableModel)xmlTable.getModel();
				int row = xmlTable.rowAtPoint(evt.getPoint());
				int col = xmlTable.columnAtPoint(evt.getPoint());
				if (row >= 0 && col >= 0) 
				{
					if( col == 5 )
					{
						Object[] rowData = new Object[] {"","", "","","","+","-" };
						model.insertRow(row, rowData);
					}
					else if( col == 6 )
					{
						model.removeRow(row);
					}

				}
			}
		});
		xmlTable.setFillsViewportHeight(true);
		xmlScroll.setSize(780,550);
		xmlScroll.setLocation(5,150);
		xmlScroll.setViewportView(xmlTable);
		add(xmlScroll);

		consolePane.setSize(780, 200);
		consolePane.setLocation(5, 710);
		consolePane.setViewportView(console);
		add(consolePane);
		
		status.setSize(800,20);
		status.setLocation(5,920);
		add(status);

		setVisible(true);

		try {
			style=new String(Files.readAllBytes(Paths.get("style.css")));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent arg0)
	{
		try {
			if(arg0.getSource() == pickFileBtn)
			{
				JFileChooser fileChooser = new JFileChooser("C:\\Users\\User\\eclipse-workspace\\Bible\\");
				fileChooser.setFileFilter(new FileNameExtensionFilter("*.xml", "xml"));
				int result = fileChooser.showOpenDialog(this);
				if (result == JFileChooser.APPROVE_OPTION) {
					
					xmlTable.setModel(new DefaultTableModel(new Object[]{"c1", "c2","c3","ref","ref verse","Add","Del"},0) {
				        public boolean isCellEditable(int rowIndex, int mColIndex) {
				        	return mColIndex >=3;
				        }
				      });
					xmlTable.getColumnModel().getColumn(2).setPreferredWidth(400);
					xmlTable.getColumnModel().getColumn(4).setPreferredWidth(400);
					DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
					centerRenderer.setHorizontalAlignment(JLabel.CENTER);
					xmlTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
					xmlTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
					
					File selectedFile = fileChooser.getSelectedFile();
					inFileName.setText(selectedFile.getAbsolutePath());
					inFilePath.setText(selectedFile.getAbsolutePath());

					Scanner sc = new Scanner(Paths.get(inFilePath.getText()));
					//					String data = new String(Files.readAllBytes(Paths.get(inFilePath.getText()))); 
					//					xmlArea.setText(data);
					while(sc.hasNextLine())
					{
						DefaultTableModel model = (DefaultTableModel) xmlTable.getModel();
						String[] pieces = sc.nextLine().split("\t");
						Object[] row = new Object[] {pieces[0],	(1<pieces.length) ? pieces[1]:"", "","","","+","-" };
						if( pieces.length > 2 )
						{
							String ref = pieces[2];
							if( ref.indexOf("<url>") > -1 )
								row[2] = ref;
							else
							{
								int i = ref.indexOf(">");
								row[3] = ref.substring(1,i);
								row[4] = ref.substring(i+1, ref.lastIndexOf("<"));
							}
						}
						model.addRow(row);
					}
				}	
			}
			else if(arg0.getSource() == goBtn )
			{
				Thread t = new Thread(this);
				t.start();
			}
			else if (arg0.getSource() == saveBtn)
			{
				writeXmlTableToFile(inFilePath.getText());
				//				Files.write(Paths.get(inFilePath.getText()), xmlArea.getText().getBytes());
			}
			else if( arg0.getSource() == previewBtn)
			{
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						writeXmlTableToFile("deleteMe");
						InputSource in = new InputSource("deleteMe");
						StringWriter html = new StringWriter();
						BufferedWriter bw = new BufferedWriter(html);
						try {
							htmlFromXML(in, bw);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						finally
						{
							setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}

						String h = html.getBuffer().toString();
						htmlArea.setText("<html><style>"+style+"</style>"+h.substring(6));
						Platform.runLater(() -> {
							WebView webView = new WebView();
							jfxPanel.setScene(new Scene(webView));
							//			    webView.getEngine().load("http://www.stackoverflow.com/");
							webView.getEngine().loadContent(htmlArea.getText(), "text/html");
						});
					}
				});
				t.start();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void writeXmlTableToFile(String aFile)
	{
		try {
			String f = Paths.get(aFile).toString();
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
	
			DefaultTableModel model = (DefaultTableModel) xmlTable.getModel();
			for( int i=0; i<model.getRowCount(); i++)
			{
				String s = model.getValueAt(i, 0).toString() + "\t" +  model.getValueAt(i, 1) + "\t" + model.getValueAt(i, 2) ;
				String ref = model.getValueAt(i,3).toString();
				if( ref.length() > 1 )
				{
					s = s + "<" + ref  + ">" + model.getValueAt(i, 4) + "</" + ref + ">";  
				}
				bw.write(s);
				bw.newLine();
			}
			bw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void run() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		
//		for( int i=0; i<batch.length; i++)  // only for batch run
		{
//		inFileName.setText(batch[i]);
		
		try 
		{
			String fName = inFileName.getText();
			if( fName.contains("*.xml"))
			{
				System.out.println("Generating...");
				String path = fName.substring(0,fName.indexOf("*"));
				try (Stream<Path> walk = Files.walk(Paths.get(path))) 
				{
					List<String> result = walk.map(x -> x.toString())
							.filter(f -> f.endsWith(".xml")).collect(Collectors.toList());

					result.forEach(f -> {
						genFile(f);
					});

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				genFile( inFileName.getText());
			}
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
		finally {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			status.setText("Finished!");
		}
		}

	}
	private void genFile(String fName) 
	{
		try {
			System.out.println("Generating " + fName + "...");
			console.append("Generating " + fName + "...\n");
			console.setCaretPosition(console.getDocument().getLength());
			inFilePath.setText(fName);
			String data = ""; 
			data = new String(Files.readAllBytes(Paths.get(fName))); 

			//			xmlArea.setText(data);

			String outNm = fName;
			outNm = outNm.substring(0,outNm.indexOf(".xml")) + ".html";
			outName.setText("Output File Name: " + outNm);
			File out = new File(outNm);

			// open file for writing
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));

			InputSource in = new InputSource(fName);
			htmlFromXML(in, bw);

			if( bw!=null)
				bw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void htmlFromXML(InputSource in, BufferedWriter bw) throws Exception
	{
		// Read XML file
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(in);
		doc.getDocumentElement().normalize();

		Node fNode = doc.getFirstChild();
		String title = ((Element)fNode).getAttribute("title");
		String prevChap = ((Element)fNode).getAttribute("prevChapter");
		String nextChap = ((Element)fNode).getAttribute("nextChapter");
		printHeader(title, prevChap, nextChap, bw);
		printNode(fNode, bw);
		printLine("</div>\n</body>\n</html>", bw);
	}
	private void printNode(Node e, BufferedWriter fw) throws Exception
	{
		String topic = "\t<tr><td class='1$%s'>2$%s</td></tr>";
		Thread.currentThread().yield();
		String nodeName = e.getNodeName();
		if( nodeName.equals("item") || nodeName.equals("verse"))
		{
			printLine("<table>\n", fw);
		}
		else if( nodeName.equals("topic")  )
		{
			String desc = e.getChildNodes().item(0).getNodeValue();

			String out = String.format(topic, nodeName, desc);
			out = checkForHighLight(e, out);

			printLine(out, fw);
			printLine("</table>\n<table>", fw);
		}
		else if( nodeName.equals("c1"))
		{
			String desc = e.getChildNodes().item(0).getNodeValue();


			String out = "\t<tr><td class='sp'></td><td colspan='2' class='" + nodeName + "'>";
			out +=desc;
			out += "</td><td></td></tr>";

			printLine(out, fw);
		}
		else if( nodeName.equals("c2"))
		{
			String desc = e.getChildNodes().item(0).getNodeValue();


			String out = "\t<tr><td class='sp'></td><td></td><td class='" + nodeName + "'>";
			out +=desc;
			out += "</td></tr>";

			printLine(out, fw);
		}
		else if (nodeName.equals("br"))
		{
			String out = "\t<tr><td>&nbsp</td></tr>";
			printLine(out,fw);
		}
		else if (e.getNodeName().equals("url"))
		{
			String desc= e.getChildNodes().item(0).getNodeValue();

			String ref = formatSearchStr( desc);
			String url = "https://www.biblegateway.com/passage/?search=" + ref + "&version=" + version.getText();
			String res = fetch( url );

			String outFormat = "	<tr>\n"+
					"		<td class='verse'><a target='_blank' href=\"%1$s\">%2$s</a>%3$s</td>\n"+
					"		<td><table class='refs'>\n";
			String out = String.format(outFormat, url, formatVerse(desc), res);
			printLine(out, fw);
		}
		else if (nodeName.endsWith("ref")  )
		{
			String refFormat  = "			<tr>\n"+
								"				<td class='sp'></td>\n"+
								"				<td class='%1$s'>%2$s<a target='_blank' href=\"%3$s\">%4$s</a></td>\n"+
								"			</tr>\n"; 

			String desc= e.getChildNodes().item(0).getNodeValue();

			String ref = formatSearchStr( desc);
			String url = "https://www.biblegateway.com/passage/?search=" + ref + "&version="+ version.getText();
			String res = fetch( url );

			res = checkForHighLight(e, res);
			String fDesc = formatRefVerse(desc);
			String out = String.format(refFormat, nodeName,  res, url, fDesc );
			
			
			printLine(out, fw);
		}

		for( int i=0; i<e.getChildNodes().getLength(); i++ )
		{
			printNode(e.getChildNodes().item(i), fw);
		}
		if( e.getNodeName().equals("item")  || e.getNodeName().equals("verse"))
			printLine(	"			</table>\n" +
						"		</td>\n"+
						"	</tr>\n" +
						"</table>", fw);
	}

	private String checkForHighLight(Node aNode, String aStr)
	{
		if( aNode.getAttributes().getNamedItem("red") != null )
		{
			String hl = aNode.getAttributes().getNamedItem("red").getNodeValue();
			aStr = highLight("red", hl, aStr);
		}
		if( aNode.getAttributes().getNamedItem("green") != null )
		{
			String hl = aNode.getAttributes().getNamedItem("green").getNodeValue();
			aStr = highLight("green", hl, aStr);
		}
		if( aNode.getAttributes().getNamedItem("pink") != null )
		{
			String hl = aNode.getAttributes().getNamedItem("pink").getNodeValue();
			aStr = highLight("pink", hl, aStr);
		}
		if( aNode.getAttributes().getNamedItem("yellow") != null )
		{
			String hl = aNode.getAttributes().getNamedItem("yellow").getNodeValue();
			aStr = highLight("yellow", hl, aStr);
		}
		if( aNode.getAttributes().getNamedItem("blue") != null )
		{
			String hl = aNode.getAttributes().getNamedItem("blue").getNodeValue();
			aStr = highLight("blue", hl, aStr);
		}
		return aStr;
	}
	private String highLight( String aColor, String verses, String aStr)
	{

		String vs[] = verses.split(","); 
		for( String s:vs)
		{
			String idx[] = s.split("-");
			int st  = Integer.parseInt(idx[0]);
			int end = Integer.parseInt(idx[1])+1;
			aStr = aStr.substring(0, st) + "<span class='"+aColor+"'>" + aStr.substring(st, end) + "</span>" + aStr.substring(end);
		}
		return aStr;
	}
	private void printHeader(String aTitle, String prevChap, String nextChap, BufferedWriter fw) throws Exception {
		
		String out= "<!DOCTYPE html>\n"+
					"<html>\n"+
					"<head>\n"+
					"	<link rel='stylesheet' type='text/css' href='../style.css'>\n"+
					"	<meta prevChapter='" + prevChap + "' nextChapter='" + nextChap + "'>\n" +
					"	<title>" + aTitle + "</title>\n"+
					"</head>\n";
			
		printLine(out, fw);
	}			
	private void printLine(String aLine, BufferedWriter bw) throws Exception
	{
		if( bw!= null)
			bw.write(aLine + "\r\n");
		//		System.out.println(aLine);

	}
	private String formatSearchStr(String aVerse) {
		while(aVerse.indexOf(" ")>-1)
		{
			int idx=aVerse.indexOf(" ");
			aVerse = aVerse.substring(0,idx) + "%20" + aVerse.substring(idx+1);
		}
		return aVerse;
	}

	private String formatVerse(String aVerse)
	{
		int idx=aVerse.lastIndexOf(":");
		if( idx > -1 )
			aVerse = aVerse.substring(idx+1);
		aVerse = "<sup>" +aVerse +"</sup>";
		return aVerse;
	}
	private String formatRefVerse(String aVerse)
	{
		while(aVerse.indexOf(" ")>-1)
		{
			int idx=aVerse.indexOf(" ");
			aVerse = aVerse.substring(0,idx) + "&nbsp;" + aVerse.substring(idx+1);
		}
		return aVerse;
	}
	private String fetch(String aUrl) throws Exception
	{
		status.setText("fetching " + aUrl + "...");
		// Make a URL to the web page
		URL url = new URL(aUrl);

		// Get the input stream through URL Connection
		HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
		con.setRequestProperty("accept-charset", "UTF-8");
		//	        con.setRequestProperty("content-type", "application/x-www-form-urlencoded");
		con.setRequestProperty("Accept-Encoding", "identity");
		con.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=windows-1251");

		InputStream is =con.getInputStream();

		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line = null;
		while ((line = br.readLine()) != null) 
		{
			if( line.indexOf("<meta property=\"og:description\"")>-1)
			{
				int s = line.indexOf("content=") + 9;
				int e = line.lastIndexOf("/>");
				String str = line.substring(s,e-1);

				byte []c = str.getBytes();
				StringBuffer sb = new StringBuffer();
				for( int i=0; i<c.length; i++)
					if( c[i]==-100)
						sb.append('"');
					else if(c[i] == 63 && c[i-1] == -128)
						sb.append('"');
					else if(c[i] == -108 && c[i-1] == -128)
						sb.append(" - ");
					else if( c[i]>0)
						sb.append( (char)c[i]);

				int idx = sb.indexOf(" - ");
				if( idx > -1 && idx < 35 )
				{
					sb.insert(sb.indexOf(" - "), "</b>");
					sb.insert(0, "<b>");
				}
				return  sb.toString();
			}
		}
		is.close();
		con.disconnect();

		return "";
	}



}
