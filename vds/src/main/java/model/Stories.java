package model;

import java.util.ArrayList;

public class Stories {
	private String tags;

	private String id;

	private String startDate;

	private String category;

	private String fullText;

	private String text;

	private String title;

	private String ownerName;

	private String ownerId;

	private String dateFormat;

	private String endDate;

	private ArrayList<String> media;

	private String externalLink;

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		this.fullText = fullText;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public ArrayList<String> getMedia() {
		return media;
	}

	public void setMedia(ArrayList<String> media) {
		this.media = media;
	}

	public String getExternalLink() {
		return externalLink;
	}

	public void setExternalLink(String externalLink) {
		this.externalLink = externalLink;
	}

	@Override
	public String toString() {
		return "ClassPojo [tags = " + tags + ", id = " + id + ", startDate = " + startDate + ", category = " + category
				+ ", fullText = " + fullText + ", text = " + text + ", title = " + title + ", ownerName = " + ownerName
				+ ", ownerId = " + ownerId + ", dateFormat = " + dateFormat + ", endDate = " + endDate + ", media = "
				+ media + ", externalLink = " + externalLink + "]";
	}
}
