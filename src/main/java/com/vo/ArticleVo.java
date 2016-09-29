package com.vo;

public class ArticleVo {
	String title="";
	String content="";
	String contentText="";
	String source="";
	String addDate="";
	String url="";

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAddDate() {
		return addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}
	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	

	public String getContentText() {
		return contentText;
	}

	public void setContentText(String contentText) {
		this.contentText = contentText;
	}

	@Override
	public String toString() {
		return "ArticleVo [title=" + title + ", content=" + content
				+ ", source=" + source + ", addDate=" + addDate + "]";
	}

}
