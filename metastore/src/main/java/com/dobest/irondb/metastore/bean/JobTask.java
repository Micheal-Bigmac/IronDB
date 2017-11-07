package com.dobest.irondb.metastore.bean;

import java.io.Serializable;
import java.util.Date;

public class JobTask implements Serializable {
	
	private long id;
	private String status;
	private Date create_time;
	private String failed_reason;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getFailed_reason() {
		return failed_reason;
	}

	public void setFailed_reason(String failed_reason) {
		this.failed_reason = failed_reason;
	}
}
