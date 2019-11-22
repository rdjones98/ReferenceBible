import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;




public class XMLGen extends JFrame implements ActionListener{
	JTextField book = new JTextField();
	JTextField abbr = new JTextField();
	JTextField pChapter = new JTextField();
	JTextField chapter  = new JTextField();
	JTextField nChapter = new JTextField();
	JTextField numChapters = new JTextField();
	JTextArea htmlArea = new JTextArea();
	JScrollPane htmlScroll = new JScrollPane();
	JTextField fileName=new JTextField();
	JLabel status = new JLabel("Ready");
	JTextArea dropDown = new JTextArea();
	JScrollPane dropDownScroll = new JScrollPane();

	public static void main(String[] args) {
		JFrame frame = new XMLGen();

	}
	public XMLGen() {
		int y = 20;
		setSize(800,1000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(null);

		JLabel t = new JLabel("Book:");
		t.setSize(100,20);
		t.setLocation(150,y);
		add(t);

		book.setSize(100,20);
		book.setLocation(230, y);
		add(book);

		y+=30;
		JLabel l4 = new JLabel("Abbr:");
		l4.setSize(100, 20);
		l4.setLocation(150, y);
		add(l4);

		abbr.setSize(100,20);
		abbr.setLocation(230, y);
		add(abbr);

		y+=30;
		JLabel l2 = new JLabel("Chapter:");
		l2.setSize(100, 20);
		l2.setLocation(150, y);
		add(l2);

		chapter.setSize(100,20);
		chapter.setLocation(230, y);
		add(chapter);
		chapter.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) 
			{
				focusLostFired();
			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub

			}
		});
		
		JLabel l3=new JLabel("to:");
		l3.setSize(50,20);
		l3.setLocation(350, y);
		
		numChapters.setSize(100,20);
		numChapters.setLocation(400, y);
		add(numChapters);
		numChapters.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				if(numChapters.getText().length() == 0) return;
				String bookName = book.getText();
				String abbrName = abbr.getText();
				int start=Integer.parseInt(chapter.getText());
				int end  =Integer.parseInt(numChapters.getText());
				String s =  "	<div><img src=\"r.png\" href=\"\" onclick=\"bClick(this)\"><img class=\"dn\" src=\"d.png\" href=\"\" onclick=\"bClick(this)\">" + bookName + "\n" +
							"		<div class=\"items\">\r\n";
				
				for(int i=start; i<=end; i++)
					s+= 	"			<a class=\"menuItem\" href=\"\"onclick=\"urlClicked('" + bookName + "/" + abbrName + i + ".html');return false;\">Ch " + i + "</a>\r\n" ;

				s +=    	"		</div>\r\n" + 
							"	</div>\r\n" ;
				
				dropDown.setText(s);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		y+=30;
		pChapter.setSize(150,20);
		pChapter.setLocation(25,y);
		add(pChapter);	

		fileName.setSize(150, 20);
		fileName.setLocation(230, y);
		add(fileName);

		nChapter.setSize(150,20);
		nChapter.setLocation(400,y);
		add(nChapter);


		JButton go = new JButton("Go");
		go.setSize(50,20);
		go.setLocation(400,140);
		go.addActionListener(this);
		add(go);

		htmlScroll.setSize(750,400);
		htmlScroll.setLocation(5, 170);
		htmlScroll.setViewportView(htmlArea);
		add(htmlScroll);
		htmlArea.setLineWrap(true);
		htmlArea.setWrapStyleWord(false);

		dropDownScroll.setSize(750,300);
		dropDownScroll.setLocation(5,590);
		dropDownScroll.setViewportView(dropDown);
		add(dropDownScroll);
		
		status.setSize(200,20);
		status.setLocation(5, 900);
		add(status);
		setVisible(true);
	}
	public void focusLostFired()
	{
		int c = Integer.parseInt(chapter.getText());
		pChapter.setText(book.getText() + "/" + abbr.getText() + (c-1) +".html" );
		fileName.setText(book.getText() + "\\" + abbr.getText() + chapter.getText() +".xml" );
		nChapter.setText(book.getText() + "/" + abbr.getText() + (c+1) +".html" );
	}
	public void actionPerformed(ActionEvent arg0) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() 
			{
				try 
				{
					String gospels = "matthew mark luke john";
					String newtest = "acts romans 1 corinthians 2 corinthians galatians ephesians philippians colossians 1 thessalonians 2 thessalonians 1 timothy 2 timothy titus philemon hebrews james 1 peter 2 peter 1 john 2 john 3 john jude";
					status.setText("Starting...");
					String firstLine = "<verses title=\"%1$s\" prevChapter=\"%2$s\" nextChapter=\"%3$s\">";
					File out = new File(fileName.getText());
					BufferedWriter bw = new BufferedWriter(new FileWriter(out));
					bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
					bw.write(String.format(firstLine, book.getText() + " " + chapter.getText(), pChapter.getText(), nChapter.getText()));

					for( int i=1; i<=200; i++)
					{
						String refs = "";
						// When we get an error we are out of verses.
						try {
							refs = fetch( "https://biblehub.com/crossref/"+book.getText() + "/" + chapter.getText() + "-" + i + ".htm");
						}
						catch(Exception e)
						{
							break;
						}
						String bookAndChapter = book.getText() + " " + chapter.getText() + ":";
						bw.write("\t<verse>\n\t\t<url>" + bookAndChapter + i + "</url>\n");
						htmlArea.setText(refs);
						int idx = refs.indexOf("crossverse");
						while( idx  > 0 )
						{
							int fIdx = refs.indexOf(".htm\">",idx);
							int lIdx = refs.indexOf("</a>",fIdx);
							String ref = refs.substring(fIdx+6,lIdx);
							String lcRef = ref.toLowerCase();
							String lcRefBook = lcRef.substring(0, lcRef.indexOf(" ", 2));  // skip space
							String lcBookAndChapter = bookAndChapter.toLowerCase();
							// Don't include references to the same chapter.
							if( lcRef.indexOf(lcBookAndChapter) == -1)
							{
								if( lcRef.indexOf("revelation") > -1 )
									bw.write("\t\t<rref>" + ref + "</rref>\n" );	// Revelation reference
								else if (gospels.indexOf(lcRefBook) > -1)
									bw.write("\t\t<gref>" + ref + "</gref>\n" );	// gospel reference
								else if (newtest.indexOf(lcRefBook) > -1)
									bw.write("\t\t<nref>" + ref + "</nref>\n" );	// new testament
								else
									bw.write("\t\t<oref>" + ref + "</oref>\n" );
							}
							idx = refs.indexOf("crossverse", lIdx);
						}
						bw.write("\t</verse>\n");
					}

					bw.write("</verses>");
					bw.close();
					status.setText("Finished");
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				if(numChapters.getText().length() > 0)
				{
					int curChap =  Integer.parseInt(chapter.getText());
					int numChaps = Integer.parseInt(numChapters.getText());
					if(curChap <= numChaps)
					{
						curChap ++;
						chapter.setText(curChap+"");
						focusLostFired();
						run();
					}
				}
			}
			
		});
		t.start();
	}

	private String fetch(String aUrl) throws Exception
	{
		aUrl = aUrl.replaceAll("Song of Songs", "songs");
		aUrl = aUrl.replaceAll(" ", "_");
		aUrl = aUrl.toLowerCase();
		System.out.println("Fetching " + aUrl);
		// Make a URL to the web page
		URL url = new URL(aUrl.toLowerCase());

		// Get the input stream through URL Connection
		HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
		con.setRequestProperty("accept-charset", "UTF-8");
		con.setRequestProperty("Accept-Encoding", "identity");
		con.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=windows-1251");



		String line = null;
		StringBuffer sb = new StringBuffer();
		InputStream is =con.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while ((line = br.readLine()) != null) 
		{
			sb.append(line);
		}
		is.close();
		con.disconnect();
		return sb.toString();
	}


}


