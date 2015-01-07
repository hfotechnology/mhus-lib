package de.mhus.lib.vaadin;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public abstract class ModalDialog extends Window {

	private static final long serialVersionUID = 1L;

	public final static Action CLOSE = new CloseAction("close", "Close");
	public final static Action OK = new Action("ok", "OK");
	
	protected Action[] actions = new Action[] {OK,CLOSE};
	protected HorizontalLayout buttonBar;

	protected boolean pack;

	protected String dialogWidth = "650px";
	
	/**
	 * Set to pack if you want the dialog as small ass possible. This will pack
	 * the content. Otherwise the content is full size and the dialog is 90% of the
	 * screen. Will be set in initUI().
	 * 
	 * @param pack
	 */
	public void setPack(boolean pack) {
		this.pack = pack;
	}
	
	/**
	 * Set the width of the dialog. The default width is 650px and will be set in initUI().
	 * You can change the setWidth() after initUI() by yourself.
	 * 
	 * @param w
	 */
	public void setDialogWidth(String w) {
		this.dialogWidth = w;
	}
	
	public void show(UI ui) throws Exception {
		ui.addWindow(this);
	}

	protected void initUI() throws Exception {
		setModal(true);
		VerticalLayout layout = new VerticalLayout();
		VerticalLayout layout2 = new VerticalLayout();
		setContent(layout);
		
        layout.setMargin(true);
        layout.setSpacing(true);
        if (pack)
        	layout.setWidth("100%");
        else
        	layout.setSizeFull();
        

        setWidth(dialogWidth);
        if (!pack)
        	setHeight("90%");
        
        initContent(layout2);
        
        buttonBar = new HorizontalLayout();
        buttonBar.setSpacing(true);
        updateButtons();
        
        layout.addComponent(layout2);
        layout.setExpandRatio(layout2, 1);
        
        layout.addComponent(buttonBar);
        layout.setComponentAlignment(buttonBar, Alignment.MIDDLE_RIGHT);
        layout.setExpandRatio(buttonBar, 0);
        
        final ShortcutListener enter = new ShortcutListener("Enter",
                KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
            	for (Component c : buttonBar) {
            		if (
        				c instanceof Button 
        				&& 
        				((Button)c).getData() != null 
        				&&
        				((Action) ((Button)c).getData() ).isDefaultAction()
        				) {
	            			((Button)c).click();
	            			return;
            		}
            	}
            }
            
        };

        layout.addShortcutListener(enter);

        
	}

	
	protected abstract void initContent(VerticalLayout layout) throws Exception;

	protected void updateButtons() {
		buttonBar.removeAllComponents();
		for (final Action a : actions) {
			Button b = new Button();
			b.setData(a);
			b.setCaption(a.title);
			b.addListener(new ClickListener() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					a.doAction(ModalDialog.this);
				}
			});
			buttonBar.addComponent(b);
		}
	}


	/**
	 * 
	 * @param action
	 * @return true if the dialog should close
	 */
	protected abstract boolean doAction(Action action);

	public static class Action {

		private String id;
		private String title;
		private boolean defaultAction;

		public Action(String id, String title) {
			this.id = id;
			this.title = title;
		}

		public String getTitle() {
			return title;
		}
		
		public boolean equals(Object in) {
			if (in == null) return false;
			if (in instanceof Action) {
				return ((Action)in).id.equals(id);
			}
			return super.equals(in);
		}
		
		public String toString() {
			return id;
		}
		
		public void doAction(ModalDialog dialog) {
			if (dialog.doAction(this))
				dialog.close();
		}

		public boolean isDefaultAction() {
			return defaultAction;
		}

		public void setDefaultAction(boolean defaultAction) {
			this.defaultAction = defaultAction;
		}
		
	}
	
	public static class CloseAction extends Action {

		public CloseAction(String id, String title) {
			super(id, title);
		}
		
		public void doAction(ModalDialog dialog) {
				dialog.close();
		}
		
	}
	
}