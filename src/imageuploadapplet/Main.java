package imageuploadapplet;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.w3c.dom.Element;

import com.sun.java.browser.dom.DOMAccessException;
import com.sun.java.browser.dom.DOMAccessor;
import com.sun.java.browser.dom.DOMAction;
import com.sun.java.browser.dom.DOMService;
import com.sun.java.browser.dom.DOMUnsupportedException;

public class Main extends JApplet {

	private static final long serialVersionUID = -3554025363885986950L;
	
    JButton btnPaste;
    JScrollPane scrollPane;
    JLabel label;
    BufferedImage image;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }

    public void pasteFromClipboard() {
    	 Clipboard clipboard = (Clipboard) AccessController.doPrivileged(new PrivilegedAction() {
    	        public Object run() 
    	        {
    	        	
    	         Clipboard tempClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    	         return tempClipboard;
    	        }
    	    });
//        Clipboard clipboard = this.getToolkit().getSystemClipboard();
        if (clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
            try {
                image = (BufferedImage) clipboard.getData(DataFlavor.imageFlavor);
                int height = scrollPane.getHeight();
                int width = scrollPane.getWidth();
                Image newimg = image.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(newimg));
            } catch (UnsupportedFlavorException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        }
    }

    public void setField()
    {
        final Object applet = this;
        try {
            DOMService service = DOMService.getService(this);
            service.invokeAndWait(new DOMAction()
            {
                public Object run(DOMAccessor accessor) {
                    Element e = accessor.getDocument(applet).getElementById("image");
                    
                    if (e != null)
                        e.setAttribute("value", getImageAsBase64());
                    
                    return null;
                }
            });
        } catch (DOMAccessException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DOMUnsupportedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getImageAsBase64() {
        try {
            Iterator<ImageWriter> it = ImageIO.getImageWritersByMIMEType("image/png");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageOutputStream output = ImageIO.createImageOutputStream(baos);

            ImageWriter writer = it.next();
            writer.setOutput(output);
            writer.write(image);
            writer.dispose();

            return Base64.encodeBytes(baos.toByteArray());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void init() {
        super.init();

        this.setLayout(new BorderLayout());

        label = new JLabel();
        scrollPane = new JScrollPane(label);
        
        this.add(scrollPane, BorderLayout.CENTER);

//        btnPaste = new JButton("Paste");
//        btnPaste.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                pasteFromClipboard();
//                setField();
//            }
//        });
//        this.add(btnPaste, BorderLayout.SOUTH);

    //pasteFromClipboard();
    }

}
