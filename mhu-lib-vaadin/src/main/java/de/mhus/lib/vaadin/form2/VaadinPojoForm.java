package de.mhus.lib.vaadin.form2;

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.ComponentContainer;

import de.mhus.lib.form.LayoutFactory;
import de.mhus.lib.form.control.ActivatorControl;
import de.mhus.lib.form.control.InformationFocus;
import de.mhus.lib.form.pojo.LayoutModelByPojo;

public class VaadinPojoForm {

	private VaadinFormBuilder builder;
	private ActivatorControl control;
	private LayoutModelByPojo layout;
	private Object pojo;
	private ComponentContainer informationPane;

	public void doBuild(AbstractComponentContainer mainWindow) {
		try {
			builder = new VaadinFormBuilder();
			builder.setFormFactory(new LayoutFactory());
			builder.setInformationPane(informationPane);
			
			control = new ActivatorControl();
			control.setFocusManager(new InformationFocus());
			layout = new LayoutModelByPojo(pojo);
			layout.setFormControl(control);
			layout.setFormFactory(builder.getFormFactory());
			layout.doBuild();
			
			builder.setRoot(layout.getModelRoot());
			builder.doBuild();
			
			builder.getRootComposit().setWidth("100%");
			mainWindow.addComponent(builder.getRootComposit());
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public VaadinFormBuilder getBuilder() {
		return builder;
	}

	public ActivatorControl getControl() {
		return control;
	}

	public LayoutModelByPojo getLayout() {
		return layout;
	}

	public Object getPojo() {
		return pojo;
	}

	public void setPojo(Object pojo) {
		this.pojo = pojo;
		if (layout != null) {
			getLayout().setPojo(pojo);
			getLayout().getDataSource().fireAll();
		}
	}

	public void setEnabled(boolean b) {
		if (builder != null) getBuilder().getRootComposit().setEnabled(b);
	}

	public void setInformationContainer(ComponentContainer informationPane) {
		this.informationPane = informationPane;
	}
	
	public static VaadinPojoForm createForm(Object pojo, AbstractComponentContainer content, ComponentContainer informationPane) {
		VaadinPojoForm form = new VaadinPojoForm();
		form.setPojo(pojo);
		if (informationPane != null)
			form.setInformationContainer(informationPane);
		if (content != null)
			form.doBuild(content);
		return form;
	}
	
}