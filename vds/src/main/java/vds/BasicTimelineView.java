package vds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.ItemSelectEvent;
import org.primefaces.event.timeline.TimelineSelectEvent;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import model.Categories;
import model.Stories;
import model.TLTimelineData;

@SuppressWarnings("serial")
@ManagedBean(name = "basicTimelineView")
@SessionScoped
public class BasicTimelineView implements Serializable {

	private TimelineModel model;

	private boolean selectable = false;
	private boolean zoomable = true;
	private boolean moveable = true;
	private boolean stackEvents = true;
	private boolean snapEvents = false;
	private String eventStyle = "dot";
	private boolean axisOnTop;
	private boolean showCurrentTime = true;
	private boolean showNavigation = true;
	private Long zoomMax = 15552000000L;
	private Long zoomMin = 1296000000L;
	private Integer eventMargin = 1;
	private Integer eventMarginAxis = 1;
	private Date min;
	private Date max;
	private Date start;
	private Date end;
	private TLTimelineData data;
	private LineChartModel dateModel;

	private String link;
	private String storyTitle;
	private String storyText;
	private String storyDate;
	private Boolean linkRendered;

	DateFormat format;
	HashMap<String, Categories> catMap;
	HashMap<String, LineChartSeries> serMap;
	HashMap<String, Stories> storMap;

	@PostConstruct
	protected void initialize() {
		dateModel = new LineChartModel();
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		model = new TimelineModel();
		final Type timelindeDataType = new TypeToken<TLTimelineData>() {
		}.getType();
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("vdsdates.json").getFile());
		Gson gson = new Gson();
		try {
			JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
			data = gson.fromJson(reader, timelindeDataType);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catMap = new HashMap<String, Categories>();
		serMap = new HashMap<String, LineChartSeries>();
		storMap = new HashMap<String, Stories>();
		for (Categories c : data.getCategories()) {
			catMap.put(c.getId(), c);
			LineChartSeries series = new LineChartSeries();
			series.setLabel(c.getTitle());
			series.setShowLine(false);
			serMap.put(c.getId(), series);

		}

		int i = 0;
		Random randomGenerator = new Random();
		Calendar cal = Calendar.getInstance();
		for (Stories s : data.getStories()) {
			try {
				adjustStartDateAndAddToStoryMap(cal, s);

				Date startDate = format.parse(s.getStartDate());
				Date endDate = format.parse(s.getEndDate());
				Categories categories = catMap.get(s.getCategory());
				String categoryTitle;
				String categoryId;
				if (null != categories && null != categories.getTitle() && null != categories.getId()) {
					categoryTitle = categories.getTitle();
					categoryId = categories.getId();
					if (null != startDate && null != endDate) {
						TimelineEvent event = new TimelineEvent(s, startDate);
						Double d = randomGenerator.nextDouble();
						model.add(event);
						serMap.get(s.getCategory()).set(s.getStartDate(), d);
					}
				}
				if (start == null) {
					start = startDate;
				} else {
					if (start.after(startDate)) {
						start = startDate;
						cal.setTime(startDate);
						cal.add(Calendar.DATE, 30);
						end = cal.getTime();
					}
				}
				if (min == null) {
					min = startDate;
				} else {
					if (min.after(startDate)) {
						min = startDate;
					}
				}
				if (max == null) {
					max = endDate;
				} else {
					if (max.before(endDate)) {
						max = startDate;
					}
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		StringBuilder seriesColors = new StringBuilder();

		for (String key : serMap.keySet()) {
			dateModel.addSeries(serMap.get(key));
			if (seriesColors.length() < 1) {
				seriesColors.append(catMap.get(key).getColour());
			} else {
				seriesColors.append(",");
				seriesColors.append(catMap.get(key).getColour());
			}

		}

		dateModel.setSeriesColors(seriesColors.toString());

		dateModel.setTitle("Events");
		dateModel.setZoom(false);
		dateModel.getAxis(AxisType.Y).setLabel("");
		dateModel.getAxis(AxisType.Y).setMax(2);
		dateModel.getAxis(AxisType.Y).setMin(-1);
		DateAxis axis = new DateAxis("");
		axis.setTickFormat("%#d. %b. %y");
		dateModel.getAxes().put(AxisType.X, axis);
		System.out.println("done");

	}

	private void adjustStartDateAndAddToStoryMap(Calendar cal, Stories s) {
		if (!storMap.containsKey(s.getStartDate())) {
			storMap.put(s.getStartDate(), s);

		} else {

			try {
				cal.setTime(format.parse(s.getStartDate()));
				cal.add(Calendar.SECOND, 1);
				s.setStartDate(format.format(cal.getTime()));
				cal.setTime(format.parse(s.getEndDate()));
				cal.add(Calendar.SECOND, 1);
				s.setEndDate(format.format(cal.getTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			adjustStartDateAndAddToStoryMap(cal, s);
		}
	}

	public void chartItemSelect(ItemSelectEvent event) {
		event.getItemIndex();

		LineChartSeries series = (LineChartSeries) dateModel.getSeries().get(event.getSeriesIndex());
		ArrayList<String> dates = new ArrayList<String>();
		for (Object key : series.getData().keySet()) {
			if (key instanceof String) {
				dates.add((String) key);
			}
		}
		Collections.sort(dates);
		Date d = null;
		try {
			d = format.parse(dates.get(event.getItemIndex()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (null != d) {
			this.start = d;
		}
	}

	public void onSelect(TimelineSelectEvent e) {
		TimelineEvent timelineEvent = e.getTimelineEvent();

		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected event:",
				timelineEvent.getData().toString());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public String wrapStringAfterWhitespaceWithIndex(String text, int index) {
		StringBuilder sb = new StringBuilder(text);
		int i = 0;
		while ((i = sb.indexOf(" ", i + index)) != -1) {
			sb.replace(i, i + 1, "\r\n");
		}
		return sb.toString();
	}

	public String trimString(String text, int index) {
		if (text.length() > index) {
			return text.substring(0, index) + "...";
		}
		return text;
	}

	public String getCategoryTitle(String cat) {
		return catMap.get(cat).getTitle();
	}

	public String getCategoryColor(String cat) {
		return "background: #" + catMap.get(cat).getColour() + ";";
	}

	public void setDialogValues() {
		Map<String, String> parameterMap = (Map<String, String>) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();

		this.storyDate = parameterMap.get("date");
		Stories story = storMap.get(this.storyDate);
		this.storyTitle = story.getTitle();
		this.storyText = wrapStringAfterWhitespaceWithIndex(story.getText(), 50);
		this.link = story.getExternalLink();
		if (this.link == null || this.link.isEmpty()) {
			this.linkRendered = false;
		} else {
			this.linkRendered = true;
		}

	}

	public TimelineModel getModel() {
		return model;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public boolean isZoomable() {
		return zoomable;
	}

	public void setZoomable(boolean zoomable) {
		this.zoomable = zoomable;
	}

	public boolean isMoveable() {
		return moveable;
	}

	public void setMoveable(boolean moveable) {
		this.moveable = moveable;
	}

	public boolean isStackEvents() {
		return stackEvents;
	}

	public void setStackEvents(boolean stackEvents) {
		this.stackEvents = stackEvents;
	}

	public String getEventStyle() {
		return eventStyle;
	}

	public void setEventStyle(String eventStyle) {
		this.eventStyle = eventStyle;
	}

	public boolean isAxisOnTop() {
		return axisOnTop;
	}

	public void setAxisOnTop(boolean axisOnTop) {
		this.axisOnTop = axisOnTop;
	}

	public boolean isShowCurrentTime() {
		return showCurrentTime;
	}

	public void setShowCurrentTime(boolean showCurrentTime) {
		this.showCurrentTime = showCurrentTime;
	}

	public boolean isShowNavigation() {
		return showNavigation;
	}

	public void setShowNavigation(boolean showNavigation) {
		this.showNavigation = showNavigation;
	}

	public boolean isSnap() {
		return snapEvents;
	}

	public void setSnap(boolean snap) {
		this.snapEvents = snap;
	}

	public Long getZoomMax() {
		return zoomMax;
	}

	public void setZoomMax(Long zoomMax) {
		this.zoomMax = zoomMax;
	}

	public Long getZoomMin() {
		return zoomMin;
	}

	public void setZoomMin(Long zoomMin) {
		this.zoomMin = zoomMin;
	}

	public boolean isSnapEvents() {
		return snapEvents;
	}

	public void setSnapEvents(boolean snapEvents) {
		this.snapEvents = snapEvents;
	}

	public Integer getEventMargin() {
		return eventMargin;
	}

	public void setEventMargin(Integer eventMargin) {
		this.eventMargin = eventMargin;
	}

	public Integer getEventMarginAxis() {
		return eventMarginAxis;
	}

	public void setEventMarginAxis(Integer eventMarginAxis) {
		this.eventMarginAxis = eventMarginAxis;
	}

	public Date getMin() {
		return min;
	}

	public void setMin(Date min) {
		this.min = min;
	}

	public Date getMax() {
		return max;
	}

	public void setMax(Date max) {
		this.max = max;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public TLTimelineData getData() {
		return data;
	}

	public void setData(TLTimelineData data) {
		this.data = data;
	}

	public LineChartModel getDateModel() {
		return dateModel;
	}

	public void setDateModel(LineChartModel dateModel) {
		this.dateModel = dateModel;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getStorytext() {
		return storyText;
	}

	public void setStorytext(String storytext) {
		this.storyText = storytext;
	}

	public String getStoryDate() {
		return storyDate;
	}

	public void setStoryDate(String storyDate) {
		this.storyDate = storyDate;
	}

	public String getStoryTitle() {
		return storyTitle;
	}

	public void setStoryTitle(String storyTitle) {
		this.storyTitle = storyTitle;
	}

	public String getStoryText() {
		return storyText;
	}

	public void setStoryText(String storyText) {
		this.storyText = storyText;
	}

	public Boolean getLinkRendered() {
		return linkRendered;
	}

	public void setLinkRendered(Boolean linkRendered) {
		this.linkRendered = linkRendered;
	}

}
