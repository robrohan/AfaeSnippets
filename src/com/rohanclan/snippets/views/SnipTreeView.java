/*
 * Created on Feb 27, 2004
 *
 * The MIT License
 * Copyright (c) 2004 Rob Rohan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software 
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package com.rohanclan.snippets.views;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

//import com.rohanclan.afae.editor.AfaeEditor;
import com.rohanclan.afae.editor.actions.GenericEncloserAction;
//import com.rohanclan.afae.editor.indentstrategy.TagIndentStrategy;
import com.rohanclan.snippets.PluginImages;
import com.rohanclan.snippets.PropertyManager;
import com.rohanclan.snippets.SnippetsPlugin;
import com.rohanclan.snippets.actions.InsertSnippetAction;
import com.rohanclan.snippets.core.SnipDoubleClickListener;
import com.rohanclan.snippets.core.SnipFileDialog;
import com.rohanclan.snippets.core.SnipFolderDialog;
import com.rohanclan.snippets.core.SnipKeySequence;
import com.rohanclan.snippets.core.SnipReader;
import com.rohanclan.snippets.core.SnipTreeViewContentProvider;
import com.rohanclan.snippets.core.SnipTreeViewLabelProvider;
import com.rohanclan.snippets.core.SnipWriter;

/**
 * @author Rob
 *
 * This is a more complex view of snips with a tree view. This is the main
 * view class
 * 
 * @see ViewPart
 * 
 * This class was influenced by the aricle:
 * How to use the JFace Tree Viewer
 * By Chris Grindstaff, Applied Reasoning (chrisg at appliedReasoning.com)
 * May 5, 2002
 */
public class SnipTreeView extends ViewPart implements IPropertyChangeListener 
{
	public static final String ID_SNIPVIEWTREE = "com.rohanclan.snippets.views.SnipTreeViewId";

	public static final String DREAMWEAVER_SNIP_TYPE = "Dreamweaver";
	public static final String HOMESITE_SNIP_TYPE = "Homesite";
	public static final String ROHANCLAN_SNIP_TYPE = "RohanClan";
	public static final String UNKNOWN_SNIP_TYPE = "Unknown";
	
	public static final String DW_SNIP_EXT  = "csn";
	public static final String RRC_SNIP_EXT = "xml";
	
	/** the treeviewer control */
	protected TreeViewer treeViewer;
	protected Text text, preview;
	protected Label previewLabel;
	protected LabelProvider labelProvider;
		
	/** the path to the icons. i.e. file://C/blah/plugin/icons/ */
	protected static IPath snipBase;
	/** used as a proxy action to add snips to the editor */
	private static GenericEncloserAction tmpAction;
	
	/** Config file is used to load simple xml documents and get to
	 * simple items via DOM - not recommended for large documents
	 */
	protected static SnipReader snipReader;
	protected static SnipKeySequence snipTriggers;
	private String snippetType;
	
	MenuManager menuMgr;
	
	protected Action insertAction, createFolderAction, createSnippetAction, editSnippetAction, refreshSnippetsAction, deleteSnippetAction, deleteFolderAction;
	
	/** the root directory */
	protected File root;
	
	private PropertyManager propertyManager;
	
	/**
	 * The constructor.
	 */
	public SnipTreeView() {
		super();
		
		try {
			propertyManager = new PropertyManager();
			snippetType = ROHANCLAN_SNIP_TYPE;

			// This ensures that we are notified when the properties are saved
			SnippetsPlugin.getDefault().getPropertyStore().addPropertyChangeListener(this);
			
			snipBase = new Path(propertyManager.snippetsPath());
			
			if(tmpAction == null)
				tmpAction = new GenericEncloserAction();
			
			if(snipReader == null)
				snipReader = new SnipReader();
			
			if(snipTriggers == null)
				snipTriggers = new SnipKeySequence();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	/*
	 * @see IWorkbenchPart#createPartControl(Composite)
	 */
	public void createPartControl(Composite parent) 
	{
		//Create a grid layout object so the text and treeviewer
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 2;
		parent.setLayout(layout);
		
		//Create a "label" to display information in. I'm
		//using a text field instead of a lable so you can
		//copy-paste out of it.
		text = new Text(parent, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER);
		// layout the text field above the treeviewer
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		text.setLayoutData(layoutData);
		
		
		//Create the tree viewer as a child of the composite parent
		treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(new SnipTreeViewContentProvider(getRootInput()));
		labelProvider = new SnipTreeViewLabelProvider();
		treeViewer.setLabelProvider(labelProvider);
		
		treeViewer.setUseHashlookup(true);
		
		//layout the tree viewer below the text field
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.FILL;
		treeViewer.getControl().setLayoutData(layoutData);
		
		previewLabel = new Label(parent, SWT.WRAP);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        previewLabel.setLayoutData(gridData);
        previewLabel.setText("Preview                             ");
		
		
		preview = new Text(parent, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		// layout the text field above the treeviewer
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.heightHint = 100;
		layoutData.horizontalAlignment = GridData.FILL;
		preview.setLayoutData(layoutData);
		
		//Create menu, toolbars, filters
		createActions();
		createMenus();
		createToolbar();
		createContextMenu();
		hookListeners();
		
		treeViewer.setInput(getRootInput());
		//treeViewer.expandAll();
	}
	
	protected void hookListeners() 
	{
		//add a selection listener so we can look at the selected file and
		//get the help information out
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) 
			{
				// if the selection is empty clear the label
				if(event.getSelection().isEmpty()) 
				{
					text.setText("");
					preview.setText("");
					return;
				}
				
				if(event.getSelection() instanceof IStructuredSelection) 
				{
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					StringBuffer toShow = new StringBuffer("");
					StringBuffer toPreview = new StringBuffer("");
					
					//IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
					File selectedfile = (File)selection.getFirstElement();
					
					if(selectedfile.isDirectory())
					{
						text.setText("");
						preview.setText("");
						previewLabel.setText("Preview                                ");
						return;
					}
						
					//get the full path to the file
					String f = selectedfile.getAbsolutePath();
					
					String triggertext = "None";
					try
					{
						snipReader.read(f);
						toShow.append(snipReader.getSnipDescription());
						toPreview.append(snipReader.getSnipStartBlock() + snipReader.getSnipEndBlock());
						triggertext = snipTriggers.getSequence(snipTriggers.getRelativeFromFullPath(f));
					}
					catch(Exception e)
					{
						e.printStackTrace(System.err);
					}
					
					text.setText(toShow.toString());
					preview.setText(toPreview.toString());
					previewLabel.setText("Trigger: " + ( (triggertext == null) ? "None" : triggertext) );
				}
			}
		});
		
		treeViewer.addDoubleClickListener(new SnipDoubleClickListener(this));
	}
	
	/**
	 * creates all the default actions
	 */
	protected void createActions() 
	{
		
		insertAction = new Action(
			"Insert",
			PluginImages.getImageRegistry().getDescriptor(PluginImages.ICON_INSERT)
		){
			public void run() { 
				insertItem();
			}
		};
		insertAction.setToolTipText("Insert the selected snip into the document");
		
		createFolderAction = new Action(
			"Create Folder",
			PluginImages.getImageRegistry().getDescriptor(PluginImages.ICON_FOLDER)
		){
			public void run() { 
				createSnipFolder();
			}
		};
		createFolderAction.setToolTipText("Create a new snip package");
		
		createSnippetAction = new Action(
			"Create Snippet",
			PluginImages.getImageRegistry().getDescriptor(PluginImages.ICON_CREATE)
		){
			public void run() { 
				createSnippet();
			}
		};
		createSnippetAction.setToolTipText("Create a new snip");
		
		editSnippetAction = new Action(
			"Edit Snippet",
			PluginImages.getImageRegistry().getDescriptor(PluginImages.ICON_SNIPPET)
		){
			public void run() { 
				editSnippet();
			}
		};
		editSnippetAction.setToolTipText("Edit the selected snip");
		
		refreshSnippetsAction = new Action(
			"Refresh Snippets",
			PluginImages.getImageRegistry().getDescriptor(PluginImages.ICON_REFRESH)		
		) {
			public void run() {
				reloadSnippets();
			}
		};
		refreshSnippetsAction.setToolTipText("Refresh snip view");
		
		deleteSnippetAction = new Action(
			"Delete Snippet",
			PluginImages.getImageRegistry().getDescriptor(PluginImages.ICON_DELETE)
		){
			public void run() {
				deleteSnippet();
			}
		};
		deleteSnippetAction.setToolTipText("Delete selected snip");
		
		deleteFolderAction = new Action(
			"Delete Folder",
			PluginImages.getImageRegistry().getDescriptor(PluginImages.ICON_FOLDER_DELETE)
		) {
			public void run() {
				deleteSnipFolder();
			}
		};
		deleteFolderAction.setToolTipText("Delete selected snip package (must be empty)");
	}
	
	/**
	 * creates all the menus
	 * This is here mosly because I have found Mac users dont like to right 
	 * click most of the time (ctrl+click actually)
	 */
	protected void createMenus() 
	{
		IMenuManager rootMenuManager = getViewSite().getActionBars().getMenuManager();
		rootMenuManager.add(refreshSnippetsAction);
		rootMenuManager.add(insertAction);
		
		rootMenuManager.add(createSnippetAction);
		rootMenuManager.add(editSnippetAction);
		rootMenuManager.add(deleteSnippetAction);
		
		rootMenuManager.add(createFolderAction);
		rootMenuManager.add(deleteFolderAction);
	}
	
	/**
	 * Create context menu.
	 */
	private void createContextMenu() 
	{
		// Create menu manager.
		menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillContextMenu(mgr);
			}
		});
		
		// Create menu.
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		
		// Register menu for extension.
		getSite().registerContextMenu(menuMgr, treeViewer);
	}
	
	/**
	 * 
	 * @param mgr
	 */
	private void fillContextMenu(IMenuManager mgr) 
	{
		File selectedFile = getSelectedFile();
		
		if(selectedFile.isDirectory()) {
			mgr.add(createFolderAction);
			mgr.add(createSnippetAction);
			String[] files = selectedFile.list();
			if (files.length == 0) {
				mgr.add(deleteFolderAction);
			}
			
		} else {
			mgr.add(insertAction);
			mgr.add(editSnippetAction);
			mgr.add(deleteSnippetAction);
		}
		
		mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	/**
	 * creates the toolbars
	 */
	protected void createToolbar() 
	{
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
		toolbarManager.add(refreshSnippetsAction);
		toolbarManager.add(insertAction);
		
		toolbarManager.add(createSnippetAction);
		toolbarManager.add(editSnippetAction);
		toolbarManager.add(deleteSnippetAction);
		
		toolbarManager.add(createFolderAction);
		toolbarManager.add(deleteFolderAction);
	}
	
	/**
	 * Gets the root directory used as the snips base
	 * @return the root directory
	 */
	public File getRootInput()
	{
		try 
		{
			URL installURL = SnippetsPlugin.getDefault().getBundle().getEntry("/");
			//URL snipsdir = Platform.resolve(new URL(installURL, "snippets"));
			URL snipsdir = FileLocator.resolve(new URL(installURL, "snippets"));
			return new File(snipsdir.getFile());
		}
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Gets the selected item parses it, and adds the defined stuff to the
	 * editor
	 */
	public void insertItem() 
	{
		//get a handle to the current editor and assign it to our temp action
		IEditorPart iep = this.getViewSite().getWorkbenchWindow()
			.getActivePage().getActiveEditor();
		
		
		InsertSnippetAction isa = new InsertSnippetAction();
		isa.setActiveEditor(null, iep);
		
		File selectedfile = null;
		
		if(treeViewer.getSelection().isEmpty())	{
			return;
		} else {
			IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
			selectedfile = (File)selection.getFirstElement();
		}
		
		if(selectedfile.isDirectory()) 
			return;
		
		//get the full path to the file
		String f = selectedfile.getAbsolutePath();
		
		isa.insertSnippetFile(f, 1, "");	
	}
	
	/**
	 * Returns the currently selected file or the root directory if nothing is 
	 * selected
	 * @return
	 */
	private File getSelectedFile() 
	{
		File selectedfile = null;
		
		if(treeViewer.getSelection().isEmpty()) 
		{
			selectedfile = getRootInput();
		}
		else 
		{
			IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
			selectedfile = (File)selection.getFirstElement();
			treeViewer.setExpandedState(selection.getFirstElement(),true);
		}
		return selectedfile;
	}

	/**
	 * 
	 *
	 */
	protected void reloadSnippets() 
	{
		treeViewer.setInput(getRootInput());		
	}
	
	/**
	 * Creates a new folder called below the currently active folder
	 * If no folder is currently active it creates the folder below the root.
	 */
	protected void createSnipFolder() 
	{
		File selectedfile = getSelectedFile();
		
		if(!selectedfile.isDirectory())
		{
			selectedfile = selectedfile.getParentFile();
		}

		SnipWriter writer = new SnipWriter(selectedfile, snippetType, snipBase);
		
		SnipFolderDialog folderDialog = new SnipFolderDialog(
			this.getViewSite().getShell(), writer, this.treeViewer
		);
		
		folderDialog.open();
	}
	
	/**
	 * 
	 *
	 */
	protected void deleteSnipFolder() 
	{
		File selectedfile = getSelectedFile();
		
		if(!selectedfile.isDirectory())  {
			selectedfile = selectedfile.getParentFile();
		}

		MessageBox deleteDialog = new MessageBox(this.getViewSite().getShell(),SWT.YES | SWT.NO);
		deleteDialog.setMessage("Are you sure you want to delete this folder?");
		if (deleteDialog.open() == SWT.YES) {
			selectedfile.delete();
			reloadSnippets();
		}
	}
	
	/**
	 * 
	 *
	 */
	protected void createSnippet() 
	{
		File selectedfile = getSelectedFile();

		if(!selectedfile.isDirectory())  {
			selectedfile = selectedfile.getParentFile();
		}
		
		snippetType = ROHANCLAN_SNIP_TYPE;
		
		SnipWriter writer = new SnipWriter(selectedfile, snippetType, snipBase);
		
		SnipFileDialog snippetDialog = new SnipFileDialog(
			this.getViewSite().getShell(),
			writer,
			this.treeViewer,"","","","","",false,"txt"
		);
		snippetDialog.open();

	}
	
	protected void deleteSnippet() {
		File selectedfile = getSelectedFile();

		if(selectedfile.isDirectory())  {
			return;
		}
		MessageBox deleteDialog = new MessageBox(this.getViewSite().getShell(),SWT.YES | SWT.NO);
		deleteDialog.setMessage("Are you sure you want to delete this snippet?");
		if (deleteDialog.open() == SWT.YES) {
			selectedfile.delete();
			reloadSnippets();
		}
	}
	
	/**
	 * 
	 *
	 */
	protected void editSnippet() 
	{
		File selectedfile = getSelectedFile();

		if(selectedfile.isDirectory())  
		{
			return;
		}
		
		File parentDirectory = selectedfile.getParentFile();

		String f = selectedfile.getAbsolutePath().toLowerCase();

		if (f.endsWith(SnipTreeView.DW_SNIP_EXT))
		{
			snippetType = DREAMWEAVER_SNIP_TYPE;
		}
		else 
		{
			snippetType = ROHANCLAN_SNIP_TYPE;
		}
		
		snipReader.read(f);
		
		SnipKeySequence keyCombos = new SnipKeySequence();
		
		String filepath = selectedfile.getAbsolutePath().replaceAll("\\\\","/");
		String basePath = snipBase.toString();
		
		String relativePath = filepath.replaceFirst(basePath,"");

		String snippetName = selectedfile.getName().substring(0,selectedfile.getName().length()-4);
		String snippetKeyCombo = keyCombos.getSequence(keyCombos.getRelativeFromFullPath(relativePath));
		String snippetDescription = snipReader.getSnipDescription();
		String snippetStartText = snipReader.getSnipStartBlock();
		String snippetEndText = snipReader.getSnipEndBlock();
		boolean isTemplate = snipReader.isFileTemplate();
		String templateExtension = snipReader.getTemplateExtension();
		
		SnipWriter writer = new SnipWriter(parentDirectory,snippetType,snipBase);
		
		SnipFileDialog snippetDialog = new SnipFileDialog(
			this.getViewSite().getShell(),
			writer,
			this.treeViewer,
			snippetName,
			snippetKeyCombo,
			snippetDescription,
			snippetStartText,
			snippetEndText,
			isTemplate,
			templateExtension
		);
		
		snippetDialog.open();
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event)
    {
		/* if(event.getProperty().equals(PreferenceConstants.P_SNIPPETS_PATH)) 
    		{
    			snipBase = new Path(propertyManager.snippetsPath());
    			treeViewer.setInput(getRootInput());
    		} */
    }
	
	/*
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus(){;}
}