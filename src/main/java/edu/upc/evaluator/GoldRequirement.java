package edu.upc.evaluator;

public class GoldRequirement {

	private String id;
	private String href;

	public GoldRequirement(String id, String href) {
		super();
		this.id = id;
		this.href = href;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

}
