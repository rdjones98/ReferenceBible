
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class BibleViewer extends JPanel 
{
	private JPanel xmlPanel; 
	public static void main(String[] args)
	{
		new BibleViewer();
	}
	public BibleViewer()
	{
		JFXPanel jfxPanel = new JFXPanel();
		ReferenceGenerator gen = new ReferenceGenerator(jfxPanel);
		
		JFrame f = new JFrame();
		f.setSize(1900,1000);
		f.setLayout(new BorderLayout());
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// You should execute this part on the Event Dispatch Thread
		// because it modifies a Swing component 
		f.add(jfxPanel,BorderLayout.CENTER);
		f.add(gen,BorderLayout.WEST);
		// Creation of scene and future interactions with JFXPanel
		// should take place on the JavaFX Application Thread
		Platform.runLater(() -> {
		    WebView webView = new WebView();
		    jfxPanel.setScene(new Scene(webView));
		    webView.getEngine().load("http://localhost");
//		    webView.getEngine().loadContent("<HTML>HI</HTML", "text/html");
		});
		f.setVisible(true);
	}
}