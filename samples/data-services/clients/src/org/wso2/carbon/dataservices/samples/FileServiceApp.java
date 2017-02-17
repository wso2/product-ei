/*
 *  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.dataservices.samples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FileServiceApp extends JApplet implements ActionListener, ListSelectionListener {

	private static final int WINDOW_HEIGHT = 300;

	private static final int WINDOW_WIDTH = 500;

	private static final long serialVersionUID = 1L;
	
	private JPanel centerPane = new JPanel(new BorderLayout());
	
	private JPanel eastPane = new JPanel(new BorderLayout());
	
	private JPanel southPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	
	private JList filesList = new JList();
	
	private JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	
	private JScrollPane listScrollPane = new JScrollPane();
	
	private JButton addFileBtn = new JButton("Upload");
	
	private JButton deleteFileBtn = new JButton("Delete");
	
	private JButton previewFileBtn = new JButton("Preview");
	
	private JButton downloadFileBtn = new JButton("Download");
	
	private JButton refreshFileBtn = new JButton("Refresh");
	
	private JTextArea infoTextArea = new JTextArea();
	
	private String host;
	
	private int port;
	
	private long downloadId = 0;
	
	private Map<Long, ByteArrayOutputStream> previewDataMap = new HashMap<Long, ByteArrayOutputStream>();
	
	public FileServiceApp() {
		this.host = BaseSample.HOST_IP;
		this.port = Integer.parseInt(BaseSample.HOST_HTTP_PORT);
	}
	
	public void init() {		
		this.centerPane.setBackground(Color.BLACK);
		this.centerPane.setPreferredSize(new Dimension(250, 250));
		this.centerPane.setBorder(BorderFactory.createTitledBorder("Preview"));
		this.eastPane.setBorder(BorderFactory.createTitledBorder("Files"));
		
		centerSplitPane.add(this.centerPane, JSplitPane.LEFT);
		centerSplitPane.add(this.eastPane, JSplitPane.RIGHT);
		centerSplitPane.setOneTouchExpandable(true);
		
		infoTextArea.setBorder(BorderFactory.createTitledBorder("Details:-"));
		infoTextArea.setEditable(false);
		this.setInfoText(null, null, null);
		
		this.addFileBtn.addActionListener(this);
		this.deleteFileBtn.addActionListener(this);
		this.previewFileBtn.addActionListener(this);
		this.downloadFileBtn.addActionListener(this);
		this.refreshFileBtn.addActionListener(this);
		this.filesList.addListSelectionListener(this);
		
		this.listScrollPane.setViewportView(this.filesList);
		
		this.eastPane.add(this.listScrollPane, BorderLayout.CENTER);
		this.eastPane.add(this.infoTextArea, BorderLayout.SOUTH);
		this.southPane.add(this.addFileBtn);
		this.southPane.add(this.deleteFileBtn);
		this.southPane.add(this.previewFileBtn);
		this.southPane.add(this.downloadFileBtn);
		this.southPane.add(this.refreshFileBtn);
		this.getContentPane().add(this.centerSplitPane, BorderLayout.CENTER);
		this.getContentPane().add(this.southPane, BorderLayout.SOUTH);
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		
		this.refreshFilesList();
    }
	
	private void downloadFinished(long downloadId, String fileName, String type) {
		ByteArrayOutputStream byteOut = this.previewDataMap.get(downloadId);
		try {
			byte[] data = byteOut.toByteArray();
			byteOut.close();
			this.previewDataMap.remove(downloadId);
			this.previewData(data, fileName, type);
		} catch (IOException e) {
			this.showError(e.getMessage());
		}
	}
	
	/**
	 * returns [0] - width, [1] - height.
	 */
	private double[] getScaledSize(double targetWidth, double targetHeight, double sourceWidth, double sourceHeight) {
		double[] vals = new double[2];
		if (sourceWidth < targetWidth && sourceHeight < targetHeight) {
			vals[0] = sourceWidth;
			vals[1] = sourceHeight;
		} else {
			double x1 = sourceHeight / sourceWidth;
			double x2 = targetHeight / targetWidth;
			if (x1 > x2) {
				vals[1] = targetHeight;
				vals[0] = sourceWidth * targetHeight / sourceHeight;
			} else {
				vals[0] = targetWidth;
				vals[1] = sourceHeight * targetWidth / sourceWidth;
			}
		}
		return vals;
	}
	
	private void previewData(byte[] data, String fileName, String type) {
		try {
			int w1 = this.centerPane.getWidth();
			int h1 = this.centerPane.getHeight();
			Image image = ImageIO.read(new ByteArrayInputStream(data));
			if (image == null) {
				JOptionPane.showMessageDialog(this, "The selected file cannot be previewed");
				return;
			}
			int w2 = image.getWidth(null);
			int h2 = image.getHeight(null);
			double[] dims = this.getScaledSize(w1, h1, w2, h2);
			JLabel label = new JLabel(new ImageIcon(image.getScaledInstance((int) dims[0], (int) dims[1], Image.SCALE_DEFAULT)));
			this.centerPane.removeAll();
			this.centerPane.add(label, BorderLayout.CENTER);
			this.centerPane.setBorder(BorderFactory.createTitledBorder("Preview - " + fileName));
			this.centerPane.validate();
		} catch (Exception e) {
			this.showError(e.getMessage());
		}
	}
	
	private void setInfoText(String name, String type, String size) {
		this.infoTextArea.setTabSize(4);
		if (name == null) {
			this.infoTextArea.setText("Name:\t----\nType:\t----\nSize:\t----\n");
			return;
		}
		if (size == null || size.trim().length() == 0) {
			size = "0";
		}
		this.infoTextArea.setText("Name:\t" + name + "\nType:\t" + type + "\nSize:\t" + size + " bytes\n");
	}
	
	private void refreshFilesList() {
		try {
			List<String> list = getFileNames();
			Vector<String> listContents = new Vector<String>();
			for (String item : list) {
				listContents.add(item);
			}
			this.filesList.setListData(listContents);
		} catch (Exception e) {
			e.printStackTrace();
			showError(e.getMessage());
		}
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	private InputStream contactURL(String url) throws IOException {
		URL urlObj = new URL(url);
		URLConnection conn = urlObj.openConnection();
		return conn.getInputStream();
	}
	
	private void doPost(String url, String content) throws IOException {
		URL urlObj = new URL(url);
		URLConnection conn = urlObj.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-Type",	"application/x-www-form-urlencoded");
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.writeBytes(content);
		out.flush();
		out.close();
		conn.getInputStream().close();
	}
	
	private Element getXMLDomFromStream(InputStream in) throws Exception {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = fac.newDocumentBuilder();
		Document doc = builder.parse(in);
		return doc.getDocumentElement();
	}
	
	private XMLStreamReader getXMLStreamFromInput(InputStream in) throws Exception {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = factory.createXMLStreamReader(in);
		return reader;
	}
	
	private String generateURL(String resPath) {
		String host = this.getHost();
		int port = this.getPort();
		if (host == null) {
		    host = this.getCodeBase().getHost();
		    port = this.getCodeBase().getPort();
		}
		String url = "http://" + host + ":" + port + "/services/samples/FileService.HTTPEndpoint/" + resPath;
		return url;
	}
	
	private List<String> getFileNames() throws Exception {
		Element el = this.getXMLDomFromStream(this.contactURL(this.generateURL("getFileNames")));
		List<String> retList = new ArrayList<String>();
		NodeList nodeList = el.getChildNodes();	
		int n = nodeList.getLength();
		Element tmpEl;
		for (int i = 0; i < n; i++) {
			tmpEl = (Element) nodeList.item(i);
			retList.add(tmpEl.getFirstChild().getTextContent());
		}
		return retList;
	}
	
	private String getFileType(String fileName) throws Exception {
		Element el = this.getXMLDomFromStream(this.contactURL(this.generateURL("getFileType?fileName=" + URLEncoder.encode(fileName, "UTF-8"))));
		return el.getFirstChild().getFirstChild().getTextContent();
	}
	
	private void deleteFile(String fileName) throws Exception {
		this.contactURL(this.generateURL("deleteFile?fileName=" + URLEncoder.encode(fileName, "UTF-8"))).close();
	}
	
	private String getFileSize(String fileName) throws Exception {
		Element el = this.getXMLDomFromStream(this.contactURL(this.generateURL("getFileSize?fileName=" + URLEncoder.encode(fileName, "UTF-8"))));
		return el.getFirstChild().getFirstChild().getTextContent();
	}
	
	private boolean checkFileExists(String fileName) throws Exception {
		Element el = this.getXMLDomFromStream(this.contactURL(this.generateURL("checkFileExists?fileName=" + URLEncoder.encode(fileName, "UTF-8"))));
		return el.getFirstChild().getFirstChild().getTextContent().equals("1");
	}
	
	private void createNewFile(String fileName, String type) throws Exception {
		contactURL(generateURL("createNewFile?fileName=" + URLEncoder.encode(fileName, "UTF-8")	+ "&fileType=" + URLEncoder.encode(type, "UTF-8"))).close();
	}
	
	private void sendFileRecord(String fileName, byte[] buff, int i) throws Exception {
		if (buff.length > i) {
			byte[] tmpBuff = new byte[i];
			for (int j = 0; j < i; j++) {
				tmpBuff[j] = buff[j];
			}
			buff = tmpBuff;
		}
		byte[] encData = Base64.encode(buff);
		String encStr = new String(encData, "UTF-8");
		String url = this.generateURL("appendDataToFile");
		this.doPost(url, "fileName=" + fileName + "&data=" + URLEncoder.encode(encStr, "UTF-8"));
	}
	
	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Unexpected error occured", JOptionPane.ERROR_MESSAGE);
	}
	
	private void saveFileData(String fileName, OutputStream out, long downloadId) throws Exception {
		FileHandler downloader = new FileHandler(fileName, out);
		downloader.setDownloadNotifyId(downloadId);
		Thread thread = new Thread(downloader);
		thread.start();
	}
	
	private void saveFileData(String fileName, OutputStream out) throws Exception {
		this.saveFileData(fileName, out, -1);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			Object src = e.getSource();
			if (src == this.downloadFileBtn) {
				if (this.filesList.getSelectedValues().length == 0) {
					return;
				}
				String fileName = this.filesList.getSelectedValue().toString();
				if (fileName != null) {
					JFileChooser fc = new JFileChooser();
					int result = fc.showSaveDialog(this);
					if (result == JFileChooser.APPROVE_OPTION) {
						this.saveFileData(fileName, new FileOutputStream(fc.getSelectedFile()));
					}					
				} else {
					this.setInfoText(null, null, null);
				}
			} else if (src == this.previewFileBtn) {
				String fileName = this.filesList.getSelectedValue().toString();
				if (fileName != null) {
					ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
					this.previewDataMap.put(this.downloadId, byteOut);
					this.saveFileData(fileName, byteOut, this.downloadId);
					this.downloadId++;
				}
			} else if (src == this.addFileBtn) {
				JFileChooser fc = new JFileChooser();
				int ret = fc.showOpenDialog(this);
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					FileInputStream in = new FileInputStream(file);
					String fn = file.getAbsolutePath();
					String fileName = null;
					String formattedFileName;
					while (true) {
						fileName = JOptionPane.showInputDialog(this,
								"Enter file name:", fn.substring(fn
										.lastIndexOf(File.separator) + 1));
						if (fileName == null) {
							return;
						}
						formattedFileName = fileName.replace(' ', '_');
						if (this.checkFileExists(formattedFileName)) {
							JOptionPane.showMessageDialog(this, "The file '" + fileName + "' already exists, please select another file name");
							continue;
						} else {
							break;
						}
					}
				    int index = fn.lastIndexOf(".");
				    String type = "";
				    if (index != -1) {
				    	type = fn.substring(index + 1);
				    }
				    
				    FileHandler downloader = new FileHandler(formattedFileName, type, in, file.length());
					Thread thread = new Thread(downloader);
					thread.start();
				}
			} else if (src == this.deleteFileBtn) {
				Object[] fileNames = this.filesList.getSelectedValues();
				if (fileNames.length == 0) {
					return;
				}
				int result;
				String message;
				if (fileNames.length == 1) {
					message = "Are you sure you want to delete the file '" + fileNames[0] + "' ?";
				} else {
					message = "Are you sure you want to delete " + fileNames.length + " files ?";
				}
				result = JOptionPane.showConfirmDialog(this, message, "Delete file(s)", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					for (Object fileNameObj : fileNames) {
						this.deleteFile(fileNameObj.toString());
					}
					this.refreshFilesList();
				}
			} else if (src == refreshFileBtn) {
				this.refreshFilesList();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			showError(ex.getMessage());
		}
	}

	private void doFileSelectionChange(String fileName) {
		try {
		    String type = this.getFileType(fileName);
		    String size = this.getFileSize(fileName);
		    this.setInfoText(fileName, type, size);
		} catch (Exception e) {
			e.printStackTrace();
			showError(e.getMessage());
		}
	}
	
	public void valueChanged(ListSelectionEvent e) {
		Object src = e.getSource();
		if (e.getValueIsAdjusting()) {
			return;
		}
		if (src == this.filesList) {
			if (this.filesList.getSelectedValues().length == 0) {
				this.setInfoText(null, null, null);
				return;
			}
			Object fileNameObj = this.filesList.getSelectedValue();
			if (fileNameObj != null) {
			    this.doFileSelectionChange(fileNameObj.toString());
			}
		}
	}
	
	private class FileHandler extends JDialog implements Runnable, ActionListener, WindowListener {
		
		private static final String DEF_CHAR_SET = "UTF-8";

		private static final long serialVersionUID = 1L;

		private final JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		
		private JPanel southPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		private JButton cancelBtn = new JButton("Cancel");
		
		private boolean stop = false;
		
		private String fileName;
		
		private OutputStream out;
		
		private InputStream in;
		
		private String type;
		
		private long size;
		
		private long downloadNotifyId = -1;
		
		private FileHandler(String title) {
			this.setTitle(title);
			this.setPreferredSize(new Dimension(400, 100));
			this.progressBar.setBorder(BorderFactory.createTitledBorder(""));
			this.progressBar.setStringPainted(true);
			this.cancelBtn.addActionListener(this);
			this.southPane.add(this.cancelBtn);
			
			this.addWindowListener(this);
			
			this.getContentPane().add(this.progressBar, BorderLayout.CENTER);
			this.getContentPane().add(this.southPane, BorderLayout.SOUTH);
			this.pack();
			this.setVisible(true);
		}
				
		public FileHandler(String fileName, OutputStream out) {
			this("Downloading file " + fileName);
			this.fileName = fileName;
			this.out = out;
		}
		
		public FileHandler(String fileName, String type, InputStream in, long size) {
			this("Uploading file '" + fileName + "'");
			this.fileName = fileName;
			this.type = type;
			this.in = in;
			this.size = size;
		}
		
		public void setDownloadNotifyId(long downloadNotifyId) {
			this.downloadNotifyId = downloadNotifyId;
		}
		
		private void download() {
			String strData = null;
			try {
				final long size = Long.parseLong(getFileSize(fileName));
				XMLStreamReader reader = getXMLStreamFromInput(contactURL(generateURL("getFileRecords?fileName="
						+ URLEncoder.encode(fileName, DEF_CHAR_SET))));				
				long count = 0;
				byte[] buff;
				int progValue = 0;
				while (!stop && reader.hasNext()) {
					if (reader.next() == XMLStreamReader.START_ELEMENT && reader.getName().getLocalPart().equals("record")) {
						strData = reader.getElementText();
						buff = Base64.decode(strData.getBytes(DEF_CHAR_SET));
						count += buff.length;
						final long c = count;
						this.out.write(buff);
						final int tmp = (int) ((c / (double) size) * 100);
						if (tmp > progValue) {
							progValue = tmp;
							SwingUtilities.invokeLater(new Runnable() {
					             public void run() {
					            	 progressBar.setValue(tmp);
					             }
					        });
						}
					}
				}
				if (this.downloadNotifyId != -1 && !stop) {
					final long id = this.downloadNotifyId;
					final String xtype = this.type;
					SwingUtilities.invokeLater(new Runnable() {
			             public void run() {
			            	 downloadFinished(id, fileName, xtype);
			             }
			        });				    
				}
			} catch (Exception e) {
				e.printStackTrace();
				showError(e.getMessage());
			}
		}
		
		private void upload() {
			try {
				byte[] buff = new byte[1024 * 50]; /* 50kb */
				int i, count = 0;
				if (type == null || type.length() == 0) {
					type = "Unknown";
				}
				createNewFile(fileName, type);
				int progValue = 0;
				while (!stop  && (i = in.read(buff)) != -1) {
					sendFileRecord(fileName, buff, i);
					count += i;
					final long c = count;
					final int tmp = (int) ((c / (double) this.size) * 100);
					if (tmp > progValue) {
						progValue = tmp;
						SwingUtilities.invokeLater(new Runnable() {
				             public void run() {
				            	 progressBar.setValue(tmp);
				             }
				        });
					}
				}
				in.close();
				refreshFilesList();
				filesList.setSelectedValue(fileName, true);
			} catch (Exception e) {
				e.printStackTrace();
				showError(e.getMessage());
			}
		}
		
		public void run() {
			if (this.out != null) {
			    this.download();
			} else if (this.in != null) {
				this.upload();
			}
			SwingUtilities.invokeLater(new Runnable() {
	             public void run() {
	            	 close();
	             }
	        });
		}
		
		private void close() {
			this.stop = true;
			this.setVisible(false);
			this.getParent().remove(this);
			System.gc();
		}

		public void windowActivated(WindowEvent e) {
		}

		public void windowClosed(WindowEvent e) {
			this.close();
		}

		public void windowClosing(WindowEvent e) {
		}

		public void windowDeactivated(WindowEvent e) {
		}

		public void windowDeiconified(WindowEvent e) {
		}

		public void windowIconified(WindowEvent e) {	
		}

		public void windowOpened(WindowEvent e) {	
		}

		public void actionPerformed(ActionEvent arg0) {
			this.close();
		}
		
	}
	
	public static void main(String[] args) {			    
	    JFrame frame = new JFrame("FileServiceApp");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    FileServiceApp applet = new FileServiceApp();
	    frame.getContentPane().add(applet, BorderLayout.CENTER);
	    applet.init();
	    applet.start();
	    frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
	    frame.setVisible(true);	    
	}
		
}
