package honeycrm.server.test.small;

import java.util.ArrayList;

import org.easymock.IAnswer;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;

import honeycrm.client.dto.Dto;
import honeycrm.client.mvp.events.OpenEvent;
import honeycrm.client.mvp.presenters.DetailPresenter;
import honeycrm.client.mvp.presenters.RelationshipPresenter;
import honeycrm.client.mvp.presenters.DetailPresenter.Display;
import honeycrm.client.services.CreateServiceAsync;
import honeycrm.client.services.ReadServiceAsync;
import honeycrm.client.services.UpdateServiceAsync;
import junit.framework.TestCase;
import static org.easymock.EasyMock.*;

public class DetailPresenterTest extends TestCase {
	private SimpleEventBus eventBus;
	private String module;
	private CreateServiceAsync createService;
	private ReadServiceAsync readService;
	private UpdateServiceAsync updateService;
	private Display view;
	private DetailPresenter presenter;
	private honeycrm.client.mvp.presenters.RelationshipPresenter.Display relView;
	private HasClickHandlers saveBtn;
	private HasClickHandlers cancelBtn;
	private HasClickHandlers editBtn;
	private HasClickHandlers createBtn;

	@Override
	protected void setUp() throws Exception {
		this.eventBus = new SimpleEventBus();
		this.module = "Contact";
		this.createService = createNiceMock(CreateServiceAsync.class);
		this.readService = createNiceMock(ReadServiceAsync.class);
		this.updateService = createNiceMock(UpdateServiceAsync.class);
		this.view = createNiceMock(DetailPresenter.Display.class);
		this.relView = createNiceMock(RelationshipPresenter.Display.class);
		this.saveBtn = createNiceMock(HasClickHandlers.class);
		this.cancelBtn = createNiceMock(HasClickHandlers.class);
		this.editBtn = createNiceMock(HasClickHandlers.class);
		this.createBtn = createNiceMock(HasClickHandlers.class);
		
		expect(view.getRelationshipsView()).andReturn(relView);
		expect(view.getSaveBtn()).andReturn(saveBtn);
		expect(view.getCancelBtn()).andReturn(cancelBtn);
		expect(view.getEditBtn()).andReturn(editBtn);
		expect(view.getCreateBtn()).andReturn(createBtn);
		
		view.setPresenter((DetailPresenter) anyObject());
		expectLastCall();
		
		createService.create(isA(Dto.class), isA(AsyncCallback.class));
		expectLastCall().andAnswer(new IAnswer<Object>() {
			@Override
			public Object answer() throws Throwable {
				final Object[] arguments = getCurrentArguments();
				AsyncCallback callback = (AsyncCallback) arguments[arguments.length - 1];
				callback.onSuccess(1L);
				return null;
			}
		});		
	}
	
	public void testCreate() throws InterruptedException {
		final Dto d = new Dto(module);
		d.set("name", "Test Name");
		d.set("email", "foo@example.com");
		
		expect(view.getData()).andReturn(d);

		replay(view);
		replay(createService);

		this.presenter = new DetailPresenter(eventBus, module, readService, updateService, createService, view);
		presenter.onSave();
	}
	
	public void testUpdate() {
		final Dto d = new Dto(module);
		d.set("name", "Test Name");
		d.set("email", "foo@example.com");
		d.setId(10L);
		
		expect(view.getData()).andReturn(d);

		updateService.update(isA(Dto.class), isA(AsyncCallback.class));
		expectLastCall().andAnswer(new IAnswer<Object>() {
			@Override
			public Object answer() throws Throwable {
				final Object[] arguments = getCurrentArguments();
				AsyncCallback<Void> callback = (AsyncCallback<Void>) arguments[arguments.length - 1];
				callback.onSuccess(null);
				return null;
			}
		});
		
		replay(view);
		replay(updateService);
		
		this.presenter = new DetailPresenter(eventBus, module, readService, updateService, createService, view);
		presenter.onSave();
	}
		
	public void testEventHandling() {
		final Dto d = new Dto("Contact");
		d.setId(23L);
		eventBus.fireEvent(new OpenEvent(d));
	}
}
